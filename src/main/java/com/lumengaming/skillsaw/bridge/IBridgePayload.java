/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author prota
 */
public abstract class IBridgePayload<T> {
    public String SubChannel;
    public Long Key;
    public Gson gson = new Gson();
    
    public IBridgePayload(){
    }
    
    public byte[] ToBytes() throws IOException{
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        return this.ToBytes(out);
    }
    protected abstract byte[] ToBytes(ByteArrayDataOutput out) throws IOException;
    public abstract T FromBytes(DataInputStream in) throws IOException ;
}
