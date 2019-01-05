package com.lumengaming.skillsaw.commands.discipline;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.MutedPlayer;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

public class SoftMuteCommand extends BungeeCommand {

    public SoftMuteCommand(BungeeMain plugin){
        super(plugin, "softmute", null,"smute");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
    
        
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.MUTE)){
            return;
        }

        DataService system = plugin.getApi();
        
        try{
            if (args.length < 1) { 
                MuteCommand.printHelp(cs);
            }
            
            String name = args[0];
            system.getOfflineUserByNameOrDisplayName(name, (u) -> {
                if (u == null){
                    cs.sendMessage("§cCould not find a user by that name.");
                    return;
                }
                
                if (system.isMuted(u.getUniqueId())){
                    cs.sendMessage("§c"+u.getName()+" is already muted.");
                    return;
                }
                
                long seconds = args.length == 2 ? Long.parseLong(args[1]) : 300;
                MutedPlayer mp = new MutedPlayer(u.getUniqueId(), u.getName(), true, seconds);
                system.addMutedPlayer(mp);
                if (u.p() != null){
                    u.p().sendMessage("§7Shhhhh..."); // Idk, it seems mean to completely not tell them they're being muted.
                }
                cs.sendMessage("§a" + u.getName() + " was muted softly for " + mp.getTotalMuteTimeStr() + ". They will not be notified of being muted and they will still see their own messages as if nothing has happened.");
            });
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException ex){
            MuteCommand.printHelp(cs);
        }
    }

}
