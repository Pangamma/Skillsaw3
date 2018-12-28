/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.lumengaming.skillsaw.utility.Constants;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author prota
 */
public class GetPlayerLocationRequest extends IBridgePayload<GetPlayerLocationRequest>{
    public UUID UUID;
    public String ServerName;

    public GetPlayerLocationRequest(Long key, String uuid, String serverName) {
        this.Key = key;
        this.SubChannel = Constants.CH_GetPlayerLocation;
        this.UUID = UUID.fromString(uuid);
        this.ServerName = serverName;
    }

    public GetPlayerLocationRequest() {}

    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        out.writeUTF(this.UUID.toString());
        out.writeUTF(this.ServerName);
        return out.toByteArray();
    }

    @Override
    public GetPlayerLocationRequest FromBytes(DataInputStream in) throws IOException {
        in.reset();
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        this.UUID = UUID.fromString(in.readUTF());
        this.ServerName = in.readUTF();
        return this;
    }
}
