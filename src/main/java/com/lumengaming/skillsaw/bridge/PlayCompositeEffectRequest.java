/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.lumengaming.skillsaw.utility.C;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author prota
 */
public class PlayCompositeEffectRequest extends IBridgePayload<PlayCompositeEffectRequest>{
    public UUID UUID;
    public CompositeEffectType Type;

    public PlayCompositeEffectRequest(Long key, UUID uuid, CompositeEffectType type) {
        this.Key = key;
        this.SubChannel = C.CH_CompositeEffect;
        this.UUID = uuid;
        this.Type = type;
    }

    public PlayCompositeEffectRequest() {
    }
    
    @Override
    public PlayCompositeEffectRequest FromBytes(DataInputStream in) throws IOException {
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        this.UUID = UUID.fromString(in.readUTF());
        this.Type = CompositeEffectType.valueOf(in.readUTF());        
        return this;
    }

    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        out.writeUTF(this.UUID.toString());
        out.writeUTF(this.Type.name());
        return out.toByteArray();
    }
}
