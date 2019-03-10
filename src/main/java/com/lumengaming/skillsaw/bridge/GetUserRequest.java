/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.XLocation;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author prota
 */
public class GetUserRequest extends IBridgePayload{
    private User user;

    public GetUserRequest(){};
    public GetUserRequest(User user) {
        this.user = user;
    }
    
    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        String json = gson.toJson(this.user, User.class);
        out.writeUTF(json);
        return out.toByteArray();
    }

    @Override
    public Object FromBytes(DataInputStream in) throws IOException {
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        String locJson = in.readUTF();
        this.user = gson.fromJson(locJson, User.class);
        return this;
    }
    
}
