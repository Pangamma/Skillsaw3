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
public abstract class IRepCommand extends BungeeCommand {

    protected final DataService dh;

    public IRepCommand(BungeeMain plugin, String command, String... aliases) {
        super(plugin, command, null, aliases);
        this.dh = plugin.getDataService();
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        try {
            if (args.length > 0) {
                User onlineU = this.dh.getUserBestOnlineMatch(args[0]);
                final String fName = onlineU != null ? onlineU.getName() : args[0];

                dh.getOfflineUser(fName, true, (User target) -> {
                    if (target == null) {
                        cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                        return;
                    }

                    if (args.length == 1) {
                        // View Info?
                        target.showStatisticsTo(cs);
                    } else if (args.length >= 3) {

                        if (!cs.isPlayer()) {
                            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
                            return;
                        }

                        // I allow it for debug reasons.
                        if (fName.equalsIgnoreCase(cs.getName()) && !cs.getName().equalsIgnoreCase("Pangamma")) {
                            cs.sendMessage("§cYou cannot rep yourself!");
                            return;
                        }

                        // Already have our target.
                        double amount = Double.parseDouble(args[1]);

                        String reason = "";
                        for (int i = 2; i < args.length; i++) {
                            reason += args[i];
                            if (i < args.length - 1) {
                                reason += " ";
                            }
                        }

                        // Cap amount of rep to send.
                        if (reason.length() < 10) {
                            cs.sendMessage("§cLeave a better reason than that. The reasons are saved so we can look at them in the future.");
                            return;
                        }
                        doRep(cs, target, amount, reason);
                    } else {
                        printHelp(cs);
                    }
                });

            } else {
                printHelp(cs);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            printHelp(cs);
        }
    }

    protected abstract void printHelp(IPlayer cs);

    /**
     * Assumes CS, Target, Amount, and Reason are all VALID.
     *
     * @param cs
     * @param target
     * @param amount
     * @param reason * // Okay try to rep then. // Permissions? // Check number times repped within time area. // Check
     * sender is valid. // Cap amount of rep to send. // Send the rep. // Log the rep. // Update the user in the DB.
     */
    protected abstract boolean doRep(final IPlayer cs, final User target, double amount, final String reason);

}
