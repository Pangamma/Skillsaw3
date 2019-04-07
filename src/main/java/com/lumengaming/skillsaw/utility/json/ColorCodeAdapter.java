/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.lumengaming.skillsaw.utility.SharedUtility;
import java.io.IOException;

/**
 *
 * @author prota
 */
public class ColorCodeAdapter extends TypeAdapter<String>{

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        value = SharedUtility.disableColorCodes(value);
        out.value(value);
    }

    @Override
    public String read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.NULL) {
          in.nextNull();
          return null;
        }
        /* coerce booleans to strings for backwards compatibility */
        if (peek == JsonToken.BOOLEAN) {
          return Boolean.toString(in.nextBoolean());
        }
        
        return SharedUtility.enableColorCodes(in.nextString());
    }
}
