/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.lumengaming.skillsaw.utility.CText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

/**
 *
 * @author prota
 */
public class Hyperlink {
    public final String Text;
    public final String URL;
    private final String HoverText;

    public Hyperlink(String txt, String url) {
        this.Text = txt;
        this.URL = url;
        this.HoverText = "Click to Open";
    }
    
    public Hyperlink(String txt, String url, String hover) {
        this.Text = txt;
        this.URL = url;
        this.HoverText = hover;
    }
    
    public BaseComponent[] ToBaseComponent(){
        BaseComponent[] text = CText.hoverText(Text.replace("&", "§"), HoverText.replace("&", "§"));
        CText.applyEvent(text, new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
        return text;
    }
}
