
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lumengaming.skillsaw.common.AsyncCallback;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;

/**
 *
 * @author prota
 */
public class SharedUtility {
    
    
    /**
     * [0] days [1] hours [2] minutes [3] seconds [4] millis
     *
     * @param ms
     */
    public static long[] getTimeParts(long ms) {
        long[] t = new long[5];
        t[0] = TimeUnit.MILLISECONDS.toDays(ms);
        t[1] = TimeUnit.MILLISECONDS.toHours(ms - TimeUnit.DAYS.toMillis(t[0]));
        t[2] = TimeUnit.MILLISECONDS.toMinutes(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]));
        t[3] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]));
        t[4] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]) - TimeUnit.SECONDS.toMillis(t[3]));
        return t;
    }

    /**
     * '1d 5h 23m 22s'
     *
     * @param ms
     * @return
     */
    public static String getTimePartsString(long ms) {
        
        long[] timeParts = getTimeParts(ms);
        String s = "";
        s += timeParts[0] + "d ";
        s += timeParts[1] + "h ";
        s += timeParts[2] + "m ";
        s += timeParts[3] + "s";
        return s;
    }
    
    
    // & --> §
    public static String enableColorCodes(String input){
        String output = input;
        String colorCodes = "abcdef0123456789rlonmk";
        
        for(char c : colorCodes.toCharArray()){
            output = output.replace("&"+c, "§"+c);
        }
        return output;
    }
    
    // § --> &
    public static String disableColorCodes(String input){
        String output = input;
        String colorCodes = "abcdef0123456789rlonmk";
        
        for(char c : colorCodes.toCharArray()){
            output = output.replace("§"+c, "&"+c);
        }
        return output;
    }

    /**
     * Expects input to be pre-prepared to § color code format. Goes through the
     * input string and removes all the color codes as needed. Useful for doing
     * stuff like checking if user has permissions to use certain color codes.
     * Or maybe not for checking. But certainly for doing actions based on it. *
     */
    public static String removeColorCodes(String input, boolean canUseFormatCodes, boolean canUseBasicColors, boolean canUseBlack) {
        String output = input;
        for (ChatColor cc : ChatColor.values()) {
            switch(cc){
                case AQUA:
                case BLUE: 
                case DARK_AQUA:
                case DARK_BLUE:
                case DARK_GRAY:
                case DARK_GREEN:
                case DARK_PURPLE:
                case DARK_RED:
                case GOLD:
                case GRAY:
                case GREEN:
                case LIGHT_PURPLE:
                case RED:
                case WHITE:
                case YELLOW:
                    if (!canUseBasicColors){
                        output = output.replace(cc.toString().toUpperCase(), cc.toString().toLowerCase());
                        output = output.replace(cc.toString().toLowerCase(), cc.toString().replace('§', '&'));
                    }
                    break;
                case BLACK:
                    if (!canUseBlack){
                        output = output.replace("§0", "&0");
                    }
                    break;
                case BOLD:
                case ITALIC:
                case UNDERLINE:
                case STRIKETHROUGH:
                case MAGIC:
                case RESET:
                    if (!canUseFormatCodes){
                        output = output.replace(cc.toString().toUpperCase(), cc.toString().toLowerCase());
                        output = output.replace(cc.toString().toLowerCase(), cc.toString().replace('§', '&'));
                    }
                    break;
                default: break;
            }
        }
        return output;
    }
    
    public static void sendToUsers(IPlayer commandSender, User target, String msgIfSelf, String msgToCs, String msgToTarget){
        boolean isSelf = commandSender.isPlayer() && commandSender.getUniqueId().equals(target.getUniqueId());
        if (isSelf){
            commandSender.sendMessage(msgIfSelf);
            return;
        }
        
        target.sendMessage(msgToTarget);
        commandSender.sendMessage(msgToCs);
        
    }
    
    
    public static void translateToLocale(String sourceText, String targetLang, AsyncCallback<String> callback){
        try {
            String sourceLang = "auto";
               String encodedSourceText = URLEncoder.encode(sourceText, StandardCharsets.UTF_8.toString());
               
                String url = "https://translation.googleapis.com/language/translate/v2"
                    + "?key="+Options.Get().ChatSystem.ApiKeyForTranslator;

                String urlParameters = "&q=" + (encodedSourceText)
                    + "&target="+targetLang
                    + "&format=text"
                    ;
//
            URL urll = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urll.openConnection();
            
            //add reuqest header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Skillsaw3");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            
            BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            //print result
            System.out.println(response.toString());
            String reply = response.toString();
            
            JsonObject arr = new Gson().fromJson(reply, JsonObject.class);
            String translated = arr.get("data").getAsJsonObject()
                .get("translations").getAsJsonArray().get(0).getAsJsonObject()
                .get("translatedText").getAsString();
            callback.doCallback(translated);
            return;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SharedUtility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(SharedUtility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SharedUtility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SharedUtility.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception ex){
            Logger.getLogger(SharedUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        callback.doCallback(sourceText);
    }
}
