package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public abstract class IRepCommand extends Command {

    protected final BungeeMain plugin;
    protected final DataService dh;

    public IRepCommand(BungeeMain plugin, String command, String... aliases){
        super(command,null, aliases);
        this.plugin = plugin;
        this.dh = plugin.getDataService();
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        IPlayer csw = new BungeePlayer(cs); 
        
        try
        {
            if (args.length > 0){
                User onlineU = this.dh.getUserBestOnlineMatch(args[0]);
                final String fName = onlineU != null ? onlineU.getName() : args[0];

                dh.getOfflineUser(fName, true, (User target) -> {
                    if (target == null){
                        csw.sendMessage(Constants.ERROR_P_NOT_FOUND);
                        return;
                    }
					
                    if (args.length == 1){
                        // View Info?
                        target.showStatisticsTo(csw);
                    }
                    else if (args.length >= 3){

                        if (!csw.isPlayer()){
                            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
                            return;
                        }

                        // I allow it for debug reasons.
                        if (fName.equalsIgnoreCase(cs.getName()) && !cs.getName().equalsIgnoreCase("Pangamma")){
                            csw.sendMessage("§cYou cannot rep yourself!");
                            return;
                        }

                        // Already have our target.
                        double amount = Double.parseDouble(args[1]);

                        String reason = "";
                        for (int i = 2; i < args.length; i++){
                            reason += args[i];
                            if (i < args.length - 1){
                                reason += " ";
                            }
                        }

                        // Cap amount of rep to send.
                        if (reason.length() < 10){
                            csw.sendMessage("§cLeave a better reason than that. The reasons are saved so we can look at them in the future.");
                            return;
                        }
                        doRep(csw, target, amount, reason);
                    }
                    else{
                        printHelp(csw);
                    }
                });

            }
            else{
                printHelp(csw);
            }
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException ex){
            printHelp(csw);
        }
    }

    protected abstract void printHelp(IPlayer cs);

    /**
     * Assumes CS, Target, Amount, and Reason are all VALID.
     *
     * @param cs
     * @param target
     * @param amount
     * @param reason      *
     * // Okay try to rep then. // Permissions? // Check number times repped
     * within time area. // Check sender is valid. // Cap amount of rep to send.
     * // Send the rep. // Log the rep. // Update the user in the DB.
     */
    protected abstract boolean doRep(final IPlayer cs, final User target, double amount, final String reason);

}
