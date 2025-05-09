package com.lumengaming.skillsaw.utility;

/**
 * Simulates a REF parameter.
 * @author prota
 */
public class SimRef<T> {
    private T value;
    
    public SimRef(T val){
        this.value = val;
    }
    public T val(){
        return value;
    }
    
    public void val(T val){
        this.value = val;
    }
}
