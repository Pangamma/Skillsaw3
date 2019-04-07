/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.common.ICommandSender;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import net.md_5.bungee.api.ChatColor;


//<editor-fold defaultstate="collapsed" desc="permissions">

public enum Permissions {

    ALL("Skillsaw.*"),
    CHANNEL_ALL("Skillsaw.chat.*"),
    CHANNEL_GLOBAL("Skillsaw.chat.global"),
    CHANNEL_LIST("Skillsaw.chat.list"),
    CHANNEL_LIST_PRIVATE("Skillsaw.chat.list.private"),
    CHANNEL_STICKIES("Skillsaw.chat.stickies"),
    CHANNEL_STICKIES_INFINITE("Skillsaw.chat.stickies.infinite"),
    CHANNEL_INFO("Skillsaw.chat.info"),
    /**
     * allows colors *
     */
    CHAT_COLOR_BASIC("Skillsaw.chat.color.basic"),
    CHAT_COLOR_BLACK("Skillsaw.chat.color.black"),
    /**
     * Allows formatting codes. *
     */
    CHAT_COLOR_FORMATTNG("Skillsaw.chat.color.formatting"),
    /**
     * To send fireworks to people. *
     */
    CONGRATULATE("SkillSaw.congrats"),
    REP_NATURAL_INF("Skillsaw.nrep.override"),
    REP_NATURAL("Skillsaw.nrep"),
    REP_STAFF("Skillsaw.srep"),
    REP_FIX("Skillsaw.xrep"),
    REP_NOTE("Skillsaw.note"),
    REVIEW_LIST("Skillsaw.review.list"),
    REVIEW_THIS("Skillsaw.review.this"),
    REVIEW_REMOVE_SELF("Skillsaw.review.remove.self"),
    REVIEW_REMOVE_OTHERS("Skillsaw.review.remove.others"),
    
    
    TRANSLATE_SELF("Skillsaw.translate.self"),
    TRANSLATE_OTHERS("Skillsaw.translate.others"),
    /**
     * Allows someone to instruct in categories they have level 5 or higher in.
     */
    INSTRUCT("SkillSaw.instruct"),
    /**
     * the node required to NOT require being level 5 or higher in a category *
     */
    INSTRUCT_OVERRIDE("SkillSaw.instruct.override"),
    /**
     * /mee message *
     */
    MEE("Skillsaw.mee"),
    MUTE("Skillsaw.mute"),
    IGNORE("Skillsaw.ignore"),
    IGNORE_INF("Skillsaw.ignore.infinite"),
    /**
     * Basic nickname ability. Does not grant colors or anything special.
     */
    NICK_SELF("Skillsaw.nick.self"),
    /**
     * Allow player to change another person's nickname. It is assumed if they have this power they will also have the
     * other nick powers as well because they are staff members.
     */
    NICK_OTHERS("Skillsaw.nick.others"),
    /**
     * Allows hearts and stars and stuff.
     */
    NICK_STYLE_SPECIAL_CHARS("Skillsaw.nick.style.special_chars"),
    /**
     * All color codes for the nicknames.
     */
    NICK_STYLE_COLORS("Skillsaw.nick.style.colors"),
    /**
     * All color codes for the nicknames.
     */
    NICK_STYLE_COLOR_BLACK("Skillsaw.nick.style.colorblack"),
    /**
     * Formatting codes.
     */
    NICK_STYLE_FORMATTING("Skillsaw.nick.style.formatting"),
    STAFF_LIST("Skillsaw.staff.list"),
    /**
     * For adding/removing from staff list
     */
    STAFF_MODIFY("Skillsaw.staff.modify"),
    INSTRUCTORS_MODIFY("Skillsaw.instructors.modify"),
    INSTRUCTORS_LIST("Skillsaw.instructors.list"),
    TEST_LOCK("Skillsaw.test.lock"),
    TITLE_SET_SELF("Skillsaw.title.set.self"),
    TITLE_SET_OTHERS("Skillsaw.title.set.others"),
    TITLE_EDIT_ANY("SkillSaw.title.edit.*"),
    CUSTOM_TITLES("Skillsaw.customtitles"),
    VIEWLOGS_STAFF_REP("SkillSaw.viewlogs.staffrep"),
    VIEWLOGS_NATURAL_REP("SkillSaw.viewlogs.naturalrep"),
    VIEWLOGS_NOTE("SkillSaw.viewlogs.note"),
    VIEWLOGS_REP_FIX("SkillSaw.viewlogs.xrep"),
    
    TPA_LOCK("Skillsaw.teleport.tpalock"), 
    TELEPORT_SELF("skillsaw.teleport.tp"),
    TELEPORT_OTHERS("skillsaw.teleport.tphere"),
    TPA_TO("skillsaw.teleport.tpa"),
    TPA_HERE("skillsaw.teleport.tpahere"),
    SLOG("skillsaw.slog"), 
    PVPMODE("Skillsaw.pvpmode")
    ;
    
    //<editor-fold defaultstate="collapsed" desc="methods">
    public String node = "SkillSaw.*";

    /**
     * returns the node.*
     */
    @Override
    public String toString() {
        return node;
    }

    private Permissions(String node) {
        this.node = node;
    }

    public static String TELL_USER_LEVEL_THEY_LACK(int lvl) {
        return ChatColor.RED + "Sorry, it seems that you need to have a total rep level of " + lvl + " or higher to be able to use this command.";
    }

    public static String TELL_USER_PERMISSION_THEY_LACK(Permissions node) {
        return TELL_USER_PERMISSION_THEY_LACK(node.node);
    }

    public static String TELL_USER_PERMISSION_THEY_LACK(String node) {
        return ChatColor.RED + "Oh teh noes! D: It appears you lack the '" + node + "' permission node that is required to perform this operation.";
    }

    /**
     * Tells user if they lack permissions.
     *
     * @param cs
     * @param node
     * @return *
     */
    public static boolean USER_HAS_PERMISSION(IPlayer cs, Permissions node) {
        return USER_HAS_PERMISSION(cs, node, true);
    }

    public static boolean USER_HAS_PERMISSION(IPlayer cs, Permissions node, boolean tellIfLacking) {

        if (cs.isOp()) {
            return true;
        }

        if (cs.hasPermission(node.node)) {
            return true;
        }

        String[] args = node.node.split("\\.");
        if (args.length > 0) {
            String perm = "";
            for (int i = 0; i < args.length - 1; i++) {
                if (i > 0) {
                    perm += "." + args[i];
                } else {
                    perm = args[i];
                }
                if (cs.hasPermission(perm + ".*")) {
                    return true;
                }
            }
        }

        if (tellIfLacking) {
            cs.sendMessage(ChatColor.RED + "Oh teh noes! D: It appears you lack the '" + node + "' permission node that is required to perform this operation.");
        }

        return false;
    }
    //</editor-fold>
}
