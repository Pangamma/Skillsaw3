package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.PERMISSION;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class StaffRepCommand extends IRepCommand{

	public StaffRepCommand(BungeeMain plugin){
		super(plugin, "srep","staffrep");
	}

	@Override
	protected void printHelp(IPlayer cs){
		cs.sendMessage("§c/srep <target>");
		cs.sendMessage("§c/srep <target> <amount> <reason>");
	}

	@Override
	protected boolean doRep(IPlayer cs, final User target, final double amount, final String reason){
		// Permissions?
		if (PERMISSION.USER_HAS_PERMISSION(cs, PERMISSION.REP_STAFF, true)){
			
			// Check sender is valid.
			final User issuer = dh.getUserBestOnlineMatch(cs.getName());
			if (issuer == null){
				cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
				return false;
			}
			
			// Cap amount of rep to send.
			if (Math.abs(amount) > 10 ){
				cs.sendMessage("§cRemember that staff rep is weighted differently than natural rep. Do not give over powered amounts of rep! (+- 1 is recommended.)");
				return false;
			}
			
			target.addStaffRep(amount);
            dh.saveUser(target);
            
            {
                String format = Options.Get().Strings.sRepTargetMessage_012;
				format = Options.Get().Strings.compileMessageFormat(format,cs.getName(),  target.getName(), amount, reason);
				target.sendMessage(format);
            }
				
            {
                String format = Options.Get().Strings.sRepIssuerMessage_012;
                format = Options.Get().Strings.compileMessageFormat(format, cs.getName(),  target.getName(), amount, reason);
                cs.sendMessage(format);
            }
			
			dh.logRep(issuer,target,amount, RepType.StaffRep, reason);
			return true;
		}
        
		return false;
	}
    
}
