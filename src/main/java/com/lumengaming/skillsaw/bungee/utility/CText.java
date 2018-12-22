package com.lumengaming.skillsaw.bungee.utility;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Taylor Love (Pangamma)
 */
public class CText{

    public static BaseComponent[] legacy(String s){
        return TextComponent.fromLegacyText(s);
    }
	
	public static String toLegacyString(BaseComponent[] orig){
        return TextComponent.toLegacyText(orig);
    }
    
    /** Merges two arrays of base components.
     * @param o1
     * @param o2
     * @return  **/
    public static BaseComponent[] merge(BaseComponent[]  o1,BaseComponent[] o2){
        BaseComponent[] n = new BaseComponent[o1.length+o2.length];
        int i = 0;
        
        for(BaseComponent bc : o1)
            n[i++] = bc;
        for(BaseComponent bc : o2)
            n[i++] = bc;
        
        return n;
    }
    
    /** Iterates through all the base components and sets an event on them.
     * @param bcs
     * @param e **/
    public static void applyEvent(BaseComponent[] bcs,HoverEvent e){
        for (BaseComponent bc : bcs){
            bc.setHoverEvent(e);
        }
    }
    /** Iterates through all the base components and sets an event on them.
     * @param bcs
     * @param e **/
    public static void applyEvent(BaseComponent[] bcs,ClickEvent e){
        for (BaseComponent bc : bcs){
            bc.setClickEvent(e);
        }
    }
	
    /** Iterates through all the base components and sets an event on them.
     * @param bcs
     * @return  **/
    public static BaseComponent[] clone(BaseComponent[] bcs){
		BaseComponent[] clone = new BaseComponent[bcs.length];
        for (int i = 0; i < bcs.length;i++){
			clone[i] = bcs[i].duplicate();
        }
		return clone;
    }
	
    /** Iterates through all the base components and sets an event on them.
     * @param bcs
     * @param c **/
    public static void applyColor(BaseComponent[] bcs, net.md_5.bungee.api.ChatColor c){
        for (BaseComponent bc : bcs){
            bc.setColor(c);
        }
    }
    
    /**
     * Creates hover text easily.
     * @param displayText
     * @param hoverText
     * @return 
     */
    public static BaseComponent[] hoverText(String displayText,String hoverText){
        BaseComponent[] txt = CText.legacy(displayText);
        CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(hoverText)));
        return txt;
    }
    
    /**
     * Creates hover text easily. Click event suggests the command.
     * @param displayText
     * @param hoverText
     * @param commandText
     * @return 
     */
    public static BaseComponent[] hoverTextSuggest(String displayText,String hoverText,String commandText){
        BaseComponent[] txt = CText.legacy(displayText);
        CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(hoverText)));
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandText));
        return txt;
    }    
    
    /**
     * Creates hover text easily. Click event runs the command.
     * @param displayText
     * @param hoverText
     * @param commandText
     * @return 
     */
    public static BaseComponent[] hoverTextForce(String displayText,String hoverText,String commandText){
        BaseComponent[] txt = CText.legacy(displayText);
        CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(hoverText)));
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText));
        return txt;
    }
}
