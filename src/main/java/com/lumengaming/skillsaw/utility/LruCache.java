/*
 * Copyright 2020 Taylor Love.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.common.PromiseOneCallback;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Taylor Love
 */
/// <summary>
/// A Least Recently Used cache implementation with forced expiry logic added on.
/// Any extra questions just ask Taylor Love.
/// </summary>
/// <typeparam name="K"></typeparam>
/// <typeparam name="V"></typeparam>
public class LruCache<K, V> {

  private int capacity;
  private Long maxTTLms = null;
  private final HashMap<K, LinkedListNode<K, V>> cacheMap = new HashMap<>();
  private LinkedListNode<K, V> head;
  private LinkedListNode<K, V> tail;

  public LruCache(int capacity) {
    this.capacity = capacity;
    maxTTLms = null;
  }

  public LruCache(int capacity, Long maxTTL) {
    this.capacity = capacity;
    this.maxTTLms = maxTTL;
  }
  
  public Collection<LinkedListNode<K, V>> values() {
    return this.cacheMap.values();
  }
  
  public V get(K key) {
    synchronized (cacheMap) {
      if (cacheMap.containsKey(key)) {
        LinkedListNode<K, V> get = cacheMap.get(key);
        if (get.forceExpireAt != null && System.currentTimeMillis() > get.forceExpireAt) {
          this.cacheMap.remove(key);
          this.removeLinkedListNode(get);
        } else {
          this.removeLinkedListNode(get);
          this.addLinkedListHead(get);
        }

        return get.val;
      }
    }
    return null;
  }

  public V getOrDefault(K key, PromiseOneCallback<V> defaultFunc) {
    synchronized (cacheMap) {
      if (cacheMap.containsKey(key)) {
        // cycle in cache
        LinkedListNode<K, V> get = cacheMap.get(key);

        if (get.forceExpireAt != null && System.currentTimeMillis() > get.forceExpireAt) {
          this.cacheMap.remove(key);
          this.removeLinkedListNode(get);
        } else {
          this.removeLinkedListNode(get);
          this.addLinkedListHead(get);
        }

        return get.val;
      } else {
        V doCallback = defaultFunc.doCallback();
        Long timeToExpire = this.maxTTLms == null ? null : (System.currentTimeMillis() + this.maxTTLms);
        LinkedListNode<K, V> toAdd = new LinkedListNode<>(key, doCallback, timeToExpire);
        this.addLinkedListHead(toAdd);
        this.cacheMap.put(key, toAdd);
        this.ensureCapacity();
        return doCallback;
      }
    }
  }

  public void put(K key, V defaultFunc) {
    Long expireTime = this.maxTTLms != null ? (System.currentTimeMillis() + this.maxTTLms) : null;
    synchronized (cacheMap) {
      if (cacheMap.containsKey(key)) {
        // cycle in cache
        LinkedListNode<K, V> get = cacheMap.get(key);
        this.removeLinkedListNode(get);
        this.addLinkedListHead(get);
        get.forceExpireAt = expireTime;
        get.val = defaultFunc;
      } else {
        LinkedListNode node = new LinkedListNode(key, defaultFunc, expireTime);
        this.addLinkedListHead(node);
        this.cacheMap.put(key, node);
        this.ensureCapacity();
      }
    }
  }

  private void ensureCapacity() {
    if (this.cacheMap.size() < capacity) {
      return;
    }

    while (this.cacheMap.size() > capacity) {
      LinkedListNode<K, V> removed = this.removeLinkedListTail();
      if (removed != null) {
        this.cacheMap.remove(removed.key);
      }
    }
  }

  private void removeLinkedListNode(LinkedListNode node) {
    if (node == null) return;

    if (node.prev != null) {
      node.prev.next = node.next;
    } else {
      // assign new HEAD pointer.
      this.head = node.next;
    }

    if (node.next != null) {
      node.next.prev = node.prev;
    } else {
      // assign new TAIL pointer?
      this.tail = node.prev;
    }
  }

  private void removeLinkedListHead() {
    if (this.head != null) {
      this.head = this.head.next;
    } else {
      this.head = null;
      this.tail = null;
    }
      
    if (this.head != null && this.head.prev != null){
      this.head.prev = null;
    }
  }

  private LinkedListNode<K, V> removeLinkedListTail() {
    LinkedListNode<K, V> rt = this.tail;

    if (this.tail != null) {
      this.tail = this.tail.prev;
    } else {
      this.tail = null;
      this.head = null;
    }

    if (this.tail != null && this.tail.next != null){
      this.tail.next = null;
    }
    return rt;
  }

  private void addLinkedListHead(LinkedListNode<K, V> node) {
    LinkedListNode<K, V> temp = this.head;
    this.head = node;
    this.head.next = temp;
    this.head.prev = null;

    if (temp != null) {
      temp.prev = node;
    }

    if (this.tail == null) {
      this.tail = this.head;
    }
  }

  private void addLinkedListTail(LinkedListNode<K, V> node) {
    LinkedListNode<K, V> temp = this.tail;
    this.tail = node;
    this.tail.prev = temp;
    this.tail.next = null;

    if (temp != null) {
      temp.next = node;
    }
  }

  public int size() {
    synchronized (cacheMap) {
      return cacheMap.size();
    }
  }

  public void remove(K key) {
    synchronized (this.cacheMap) {
      if (this.cacheMap.containsKey(key)) {
        LinkedListNode<K, V> remove = this.cacheMap.remove(key);
        this.removeLinkedListNode(remove);
      }
    }
  }

  public void clear() {
    synchronized (this.cacheMap) {
      this.cacheMap.clear();
      this.head = null;
      this.tail = null;
    }
  }

  public static class LinkedListNode<K, V> {
    private final K key;
    public V val;
    public LinkedListNode<K, V> prev;
    public LinkedListNode<K, V> next;
    public Long forceExpireAt;

    public LinkedListNode(K key, V val) {
      this.val = val;
      this.key = key;
    }

    public LinkedListNode(K key, V val, Long forceExpireAt) {
      this.val = val;
      this.key = key;
      this.forceExpireAt = forceExpireAt;
    }
  }
}