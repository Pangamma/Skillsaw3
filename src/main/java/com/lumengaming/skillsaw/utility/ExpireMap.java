/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.common.AsyncCallback;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maybe not super insanely efficient, considering the key still requires o(N) search to get through.
 * Why did I make this class? I mean I like what it DOES. I like the functions it makes easier. In all honesty,
 * I highly doubt I'd ever need to store more than 2k elements in this collection. 
 * @author prota
 * @param <K>
 * @param <V>
 */
public class ExpireMap<K, V> {
    
    public static class ExpireMapHeapNode<K, V>{
        public K key;
        public V val;
        public Long expireMS;
        public AsyncCallback<V> onExpire; 
        private Integer TTL = null;

        public ExpireMapHeapNode(K key, V val, Long expireMS, AsyncCallback<V> onExpire) {
            this.key = key;
            this.val = val;
            this.expireMS = expireMS;
            this.onExpire = onExpire;
        }        
        
        public ExpireMapHeapNode(K key, V val, int expireTTL, AsyncCallback<V> onExpire) {
            this.key = key;
            this.val = val;
            this.expireMS = System.currentTimeMillis() + expireTTL;
            this.onExpire = onExpire;
            this.TTL = expireTTL;
        }
    }
    
    public final PriorityQueue<ExpireMapHeapNode<K, V>> minHeap = new PriorityQueue<>(new Comparator<ExpireMapHeapNode<K, V>>() {
        @Override
        public int compare(ExpireMapHeapNode<K, V> x, ExpireMapHeapNode<K, V> y) {
            int val = (int) Math.max(x.expireMS - y.expireMS, Integer.MAX_VALUE);
            return val;
        }
    });
    
    public ExpireMap(){
    }
    
    public synchronized boolean removeIf(Predicate<? super ExpireMapHeapNode<K, V>> filter) {
        return minHeap.removeIf(filter);
    }
    /**
     * Returns null if not found.
     * @param key
     * @return 
     */
    public synchronized V remove(K key){
        ExpireMapHeapNode<K,V> toRemove = null;
        for(ExpireMapHeapNode n : minHeap){
            if (n.key.equals(key)){
                toRemove = n;
                break;
            }
        }
        
        if (toRemove == null) return null;
        minHeap.remove(toRemove);
        return toRemove.val;
    }
    
    public synchronized void put(K key, V val, Duration timeToLive, AsyncCallback<V> onExpire){
        long futureDate = System.currentTimeMillis()+timeToLive.toMillis();
        remove(key);
        purgeExpired();
        this.minHeap.add(new ExpireMapHeapNode<>(key, val, futureDate, onExpire));
    }
    
    public synchronized void putWithBumpableCache(K key, V val, Duration timeToLive, AsyncCallback<V> onExpire){
        long futureDate = System.currentTimeMillis()+timeToLive.toMillis();
        remove(key);
        purgeExpired();
        this.minHeap.add(new ExpireMapHeapNode<>(key, val, (int) timeToLive.toMillis(), onExpire));
    }
    
    public synchronized void put(K key, V val, Duration timeToLive){
        this.put(key, val, timeToLive, null);
    }
    
    public synchronized void purgeExpired(){
        while (!minHeap.isEmpty() && System.currentTimeMillis() > minHeap.peek().expireMS){
            ExpireMapHeapNode<K, V> poll = minHeap.poll();
            try{
                if (poll.onExpire != null){
                    poll.onExpire.doCallback(poll.val);
                }
            }catch(Exception ex){
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Problem when running expiration callback", ex);
            }
        }
    }
    
    public synchronized V get(K key){
        if (key == null) return null;
        Optional<ExpireMapHeapNode<K, V>> fst = minHeap.stream().filter(x -> x.key.equals(key)).findFirst();
        if (!fst.isPresent()) return null;
        ExpireMapHeapNode<K, V> get = fst.get();
        if (get.TTL != null){
            get.expireMS = System.currentTimeMillis() + get.TTL;
        }
        return get.val;
    }
    
    public synchronized boolean contains(K key){
        purgeExpired();
        return key != null && minHeap.stream().anyMatch(x -> x.key.equals(key));
    }

    public synchronized void clear() {
        this.minHeap.clear();
    }
}
