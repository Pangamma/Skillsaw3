/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lumengaming.skillsaw.bungee.utility.Constants;
import com.lumengaming.skillsaw.models.XLocation;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author prota
 */
public class BooleanResponse extends IBridgePayload<BooleanResponse>{
    public boolean Value;

    public BooleanResponse(String subchannel, Long key, boolean value) {
        this.SubChannel = subchannel;
        this.Key = key;
        this.Value = value;
    }

    public BooleanResponse() {
    }
    
    @Override
    public BooleanResponse FromBytes(DataInputStream in) throws IOException {
        in.reset();
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        this.Value = in.readBoolean();
        return this;
    }
    

    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        out.writeBoolean(this.Value);
        return out.toByteArray();
    }
}
