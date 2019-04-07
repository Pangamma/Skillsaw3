package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author Taylor
 */
public class LanguageTranslateCommand extends BungeeCommand {

    public LanguageTranslateCommand(BungeeMain plugin) {
        super(plugin, "languagetranslate", null, "lang", "translatelanguage", "translang", "autotranslate","locale");
    }

    /**
     * Just as it sounds. Returns array with one fewer argument. Missing the first arg. *
     */
    private String[] stripArg(String[] origArray) {
        String[] nArray = new String[origArray.length - 1];
        if (origArray.length > 0) {
            for (int i = 1; i < origArray.length; i++) {
                nArray[i - 1] = origArray[i];
            }
        }
        return nArray;
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage(CText.hoverText("§c/lang [user] <en/es/nl/pl/ru/de/cs/ja/ko>", "Sets your preferred language."));
        cs.sendMessage(CText.hoverText("§c/lang [user] 0/auto/off ", "Disables the locale converter"));
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        
        if (args.length > 2 || args.length < 1){
            printHelp(cs);
        }
        
        if (args.length == 1 && !Permissions.USER_HAS_PERMISSION(cs, Permissions.TRANSLATE_SELF, true)){
            return;
        } else if (args.length >= 2 && !Permissions.USER_HAS_PERMISSION(cs, Permissions.TRANSLATE_OTHERS, true)){
            return;
        }
        
        if (args.length == 1 && !cs.isPlayer()){
            cs.sendMessage(C.ERROR_PLAYERS_ONLY);
            return;
        }
        
        ArrayList<User> us = new ArrayList<User>();
        
        if (args.length == 2 && args[0].equals("*") && Permissions.USER_HAS_PERMISSION(cs, Permissions.TRANSLATE_OTHERS, false)){
            us = plugin.getApi().getOnlineUsersReadOnly();
        }else{
        
            // TODO: Make it so * works for everyone.
            User u = args.length == 1 ? plugin.getApi().getUser(cs.getUniqueId()) : plugin.getApi().getUserBestOnlineMatch(args[0]);
            if (u != null) us.add(u);
            else { cs.sendMessage(C.ERROR_P_NOT_FOUND); return;}
        }
        
        
        try {
            // Otherwise see if they used the in-command thing with arg[0]
            boolean isSelf = false;
            switch (args[args.length-1].toLowerCase()) {
                case "0":
                case "1":
                case "off":
                case "auto":
                    for(User u : us){
                        u.setLocale(null);
                        if (u.getUniqueId().equals(cs.getUniqueId())){
                            cs.sendMessage(C.MSG_TRANSLATE_DISABLED);
                            isSelf = true;
                        }else{
                            u.sendMessage("§aTranslation service has been §cdisabled§a by §f"+cs.getDisplayName());
                        }
                    }
                    
                    if (!isSelf){
                        cs.sendMessage("§aTranslation service §cdisabled§a for §f"+us.toString());
                    }
                    break;
                default:
                    if (args[args.length-1].length() == 2){
                        for(User u : us){
                            u.setLocale(args[args.length-1]);
                            if (u.getUniqueId().equals(cs.getUniqueId())){
                                u.sendMessage(C.MSG_TRANSLATE_UPDATED+"("+args[args.length-1]+")");
                                isSelf = true;
                            }else{
                                u.sendMessage("§aTranslations §2enabled§a by §f"+cs.getDisplayName()+"§a. ("+args[args.length-1]+")");
                            }
                        }

                        if (!isSelf){
                            cs.sendMessage("§aTranslation service §2enabled§a for §f"+us.toString()+"§a. ("+args[args.length-1]+")");
                        }
                    }else{
                        cs.sendMessage(C.ERROR_INVALID_LOCALE);
                    }
                break;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            printHelp(cs);
        }
    }
}
