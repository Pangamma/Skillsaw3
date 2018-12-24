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
public class GetPlayerLocationResponse extends IBridgePayload<GetPlayerLocationResponse>{
    public XLocation Loc;

    public GetPlayerLocationResponse(Long key, XLocation loc) {
        this.Key = key;
        this.SubChannel = Constants.CH_GetPlayerLocation;
        this.Loc = loc;
    }

    public GetPlayerLocationResponse() {
    }

    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        out.writeUTF(gson.toJson(this.Loc));
        return out.toByteArray();
    }

    @Override
    public GetPlayerLocationResponse FromBytes(DataInputStream in) throws IOException {
        in.reset();
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        this.Loc = gson.fromJson(in.readUTF(), XLocation.class);
        return this;
    }
    
}
