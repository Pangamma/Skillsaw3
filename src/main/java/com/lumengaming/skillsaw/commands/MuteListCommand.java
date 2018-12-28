package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.MutedPlayer;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.BaseComponent;

public class MuteListCommand extends BungeeCommand {

    

    public MuteListCommand(BungeeMain plugin) {
        super(plugin, "mutelist", null, "muted");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.MUTE)) {
            return;
        }

        DataService system = plugin.getApi();

        ArrayList<MutedPlayer> muted = system.getMutedPlayersReadOnly();
        cs.sendMessage(Constants.C_DIV_LINE);
        cs.sendMessage(Constants.C_DIV_LINE);
        if (muted.isEmpty()){
            cs.sendMessage(Constants.C_MENU_CONTENT2+"No users are muted right now.");
        }else{
            for(int i = 0; i < muted.size(); i++){
                MutedPlayer mp = muted.get(i);
                String line = i % 2 == 0 ? Constants.C_MENU_CONTENT2 : Constants.C_MENU_CONTENT;
                line += mp.getMutedPlayerName() + " -- "+mp.getTimeRemainingStr();
                BaseComponent[] txt = CText.hoverTextForce(line, "Click to unmute \n"+mp.getMutedPlayerName(), "/unmute "+mp.getMutedPlayerName());
                cs.sendMessage(txt);
            }
        }
        cs.sendMessage(Constants.C_DIV_LINE);
    }
}
