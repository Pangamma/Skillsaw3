/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
