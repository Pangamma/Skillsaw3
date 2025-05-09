/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author prota
 * @param <T>
 */
public class Range<T extends Comparable> {

//    private static transient final T INVALID = -1;
    /**
     * -1 means no range.
     */
    public T Min = null;
    public T Max = null;

    public Range() {
    }

    public Range(T min) {
        this.Min = min;
    }

    public Range(T min, T max) {
        this.Min = min;
        this.Max = max;
    }

    public boolean isInRange(T val) {
        if (val == null) return (this.Min == null && this.Max == null);
        
        if (this.Min != null) {
            if (val.compareTo(this.Min) == -1) return false;
        }

        if (this.Max != null) {
            if (val.compareTo(this.Max) == 1) return false;
        }

        return true;
    }
    
    public boolean isInRangeGeneric(Comparable val) {
        if (val == null) return (this.Min == null && this.Max == null);
        
        if (this.Min != null) {
            if (val.compareTo(this.Min) == -1) return false;
        }

        if (this.Max != null) {
            if (val.compareTo(this.Max) == 1) return false;
        }

        return true;
    }
    
    private int compare(Number o1, Number o2) {
        if (o1 instanceof Short && o2 instanceof Short) {
            return ((Short) o1).compareTo((Short) o2);
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).compareTo((Integer) o2);
        }  else if (o1 instanceof Long && o2 instanceof Long) {
            return ((Long) o1).compareTo((Long) o2);
        } else if (o1 instanceof Float && o2 instanceof Float) {
            return ((Float) o1).compareTo((Float) o2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            return ((Double) o1).compareTo((Double) o2);
        } else if (o1 instanceof Byte && o2 instanceof Byte) {
            return ((Byte) o1).compareTo((Byte) o2);
        } else if (o1 instanceof BigInteger && o2 instanceof BigInteger) {
            return ((BigInteger) o1).compareTo((BigInteger) o2);
        } else if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
            return ((BigDecimal) o1).compareTo((BigDecimal) o2);
        } else {
            throw new RuntimeException("Woah, some kind of glitch happened.");
        }
    }
}
