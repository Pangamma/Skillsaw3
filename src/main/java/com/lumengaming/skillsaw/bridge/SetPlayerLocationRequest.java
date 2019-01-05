/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.models.XLocation;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author prota
 */
public class SetPlayerLocationRequest extends IBridgePayload<SetPlayerLocationRequest>{
    public UUID UUID;
    public XLocation Loc;

    public SetPlayerLocationRequest(Long key, UUID uuid, XLocation loc) {
        this.Key = key;
        this.SubChannel = Constants.CH_SetPlayerLocation;
        this.Loc = loc;
        this.UUID = uuid;
    }

    public SetPlayerLocationRequest() {
    }

    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        out.writeUTF(this.UUID.toString());
        out.writeUTF(gson.toJson(this.Loc));
        return out.toByteArray();
    }

    @Override
    public SetPlayerLocationRequest FromBytes(DataInputStream in) throws IOException {
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        this.UUID = UUID.fromString(in.readUTF());
        this.Loc = gson.fromJson(in.readUTF(), XLocation.class);
        return this;
    }
    
}
