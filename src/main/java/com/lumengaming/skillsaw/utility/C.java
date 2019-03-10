/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;


/**
 *
 * @author prota
 */
public class C {
    //<editor-fold defaultstate="collapsed" desc="Channels">
    public static final String CH_RootChannel = "BungeeCord";
    public static final String CH_CompositeEffect = "Skillsaw_CompositeEffect";
    public static final String CH_GetPlayerLocation = "Skillsaw_GetPlayerLocation";
    public static final String CH_SetPlayerLocation = "Skillsaw_SetPlayerLocation";
    public static final String CH_PlaySoundForPlayer = "Skillsaw_PlaySoundForPlayer";
    public static final String CHANNEL_CONSOLE_COMMAND = "Skillsaw_ConsoleCommand";
    public static final String SND_VILLAGER_HMMM = "ENTITY_VILLAGER_AMBIENT";
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CacheKeys">
    public static final String CK_GlobalStats = "GlobalStats";
    public static final String CK_IndividualStats = "IndividualStats_";
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="colors & formatting">
    /**
     * the color used for the things being shown between the divider bars. §2=§7
     */
    public static final String C_MENU_CONTENT = "§2=§7 ";

    /**
     * Use this color for alternating lists. Color used for things between the
     * divider bars. bars.§2=§f *
     */
    public static final String C_MENU_CONTENT2 = "§2=§f ";

    /**
     * the color used for the things being shown between the divider bars. §2=§7
     */
    public static final String C_MENU_CONTENT_NC = "= ";

    /**
     * §2=§e§l SkillSaw - * Start with a div line above this and end with a div
     * line below it.
     */
    public static final String C_DIV_TITLE_PREFIX = "§2=§e§l SkillSaw3 - ";

    /**
     * §2=§e§l SkillSaw - * Start with a div line above this and end with a div
     * line below it.
     */
    public static final String C_DIV_SUBTITLE_PREFIX = "§2=§e ";

    /**
     * = Skillsaw - *
     */
    public static final String C_DIV_TITLE_PREFIX_NC = "= SkillSaw - ";

    /**
     * has no color by default.*
     */
    public static final String C_DIV_LINE_NC = "=====================================================";

    /**
     * 53 things in length §2=§a=§2=§a=§2=§a=§2=....§2=§a=§2=§a=§2=§a=§2= *
     */
    public static final String C_DIV_LINE = "§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=";

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ERRORS">
    public static final String ERROR_NOT_YET_IMPLEMENTED = "§cThis feature isn't ready yet.";
    public static final String ERROR_FEATURE_REMOVED = "§cThis feature isn't available for your current version.";
    public static final String ERROR_P_NOT_FOUND = "§cThat player is not available.";
    public static final String ERROR_FUKKITRAGE = "§cThis might work normally, but because of an update to craftbukkit it is currently broken. You'll have to hunt down a new version of this plugin if you want to use this feature again. Sorry. :(";
    public static final String ERROR_DIRECTORY_NOT_FOUND = "Directory not found.";
    public static final String ERROR_PLAYERS_ONLY = "Only players may use this feature.";
    public static final String ERROR_IMPOSSIBLE = "§cNot sure what happened, but you shouldn't have reached this section of code. If you see this message, report it to Pangamma.";
    public static final String ERROR_TRY_AGAIN_LATER_COMMAND = "§cSorry, the system wasn't prepared for what you just did. Can you please try that again? Log in and out if the problem keeps happening.";
    public static final String ERROR_TRY_AGAIN_LATER_CHAT = "§cHang on... try that chat message again. Log in and out if the problem keeps happening.";
    public static final String MSG_PROCESSING = "§7Processing...";
    public static final String ERROR_P_IGNORING_YOU = "§cThat message could not be sent because that player is ignoring you.";
    public static final String ERROR_P_YOU_ARE_IGNORING = "§cThat message could not be sent because you are ignoring that player right now.";
    public static final String C_ERROR = "§c";
    public static final String ERROR_DOING_IT_WRONG = "§cYou're DOING it wrong! D:";

    public static String ERROR_REPORT_THIS_TO_PANGAMMA(int errIdentifier) {
        return "§cYou shouldn't be seeing this. Report this number to Pangamma: '" + errIdentifier + "'.";
    }

    public static String ERROR_NOT_INSTRUCTOR_FOR_CATEGORY(String letter) {
        return "§cYou are not an instructor for this category! : " + letter;
    }
    //</editor-fold>
}
