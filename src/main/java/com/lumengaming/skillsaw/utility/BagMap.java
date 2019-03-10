/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author prota
 * @param <T>
 */
public class BagMap <T> {
    private final int MAX_SIZE = 300;
    private AtomicLong counter = new AtomicLong();
    private final ConcurrentHashMap<Long, T> map = new ConcurrentHashMap<>();
    public long push(T t){
        if (counter.get() == Long.MAX_VALUE) counter.set(0);
        long key = counter.incrementAndGet();
        map.put(key, t);
        enforceMaxSize();
        return key;
    }
    
    public T pop(long t){
        T get = map.remove(t);
        return get;
    }

    private void enforceMaxSize() {
        if (map.size() > MAX_SIZE){
            List<Long> lis = new ArrayList<>();
            long minVal = counter.get() - MAX_SIZE;
             // how tf does this even work? JAVA syntax is terrible.
            map.keySet().stream().filter(x -> x < minVal).forEach(lis::add);
            for(int i = 0; i < lis.size(); i++){
                map.remove(lis.get(i));
            }
        }
    }
    
}
