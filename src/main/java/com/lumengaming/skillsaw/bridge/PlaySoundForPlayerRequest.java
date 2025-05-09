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
public class PlaySoundForPlayerRequest extends IBridgePayload<PlaySoundForPlayerRequest>{
    public UUID UUID;
    public String SoundName;

    public PlaySoundForPlayerRequest(Long key, String uuid, String SoundName) {
        this.Key = key;
        this.SubChannel = C.CH_PlaySoundForPlayer;
        this.UUID = java.util.UUID.fromString(uuid);
        this.SoundName = SoundName;
    }
    
    @Override
    public PlaySoundForPlayerRequest FromBytes(DataInputStream in) throws IOException {
        this.SubChannel = in.readUTF();
        this.Key = in.readLong();
        this.UUID = UUID.fromString(in.readUTF());
        this.SoundName = in.readUTF();
        return this;
    }

    @Override
    protected byte[] ToBytes(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(this.SubChannel);
        out.writeLong(this.Key);
        out.writeUTF(this.UUID.toString());
        out.writeUTF(this.SoundName);
        return out.toByteArray();
    }
}
