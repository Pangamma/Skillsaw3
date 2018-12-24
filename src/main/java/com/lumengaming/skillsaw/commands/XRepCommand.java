package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import org.bukkit.command.CommandSender;

public class XRepCommand extends IRepCommand{

	public XRepCommand(BungeeMain plugin){
		super(plugin, "xrep", "fixrep", "repfix");
	}

	@Override
	protected void printHelp(IPlayer cs){
		cs.sendMessage("§c/xrep <name>");
		cs.sendMessage("§c/xrep <name> <amount> <reason>");
	}

	@Override
	protected boolean doRep(IPlayer cs, final User target, final double amount, final String reason){
		// Permissions?
		if (Permissions.USER_HAS_PERMISSION(cs, Permissions. REP_FIX)){
			
            Options.StringOptions strings = Options.Get().Strings;
			// Check sender is valid.
			final User issuer = dh.getUserBestOnlineMatch(cs.getName());
			if (issuer == null){
				cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
				return false;
			}
			
			if (target != null){
				String format = strings.xRepTargetMessage_01;
				format = strings.compileMessageFormat(format,cs.getName(),  target.getName(), amount, reason);
				target.sendMessage(format);
			}
			
            double added = target.addNaturalRep(amount);
            dh.saveUser(target);
			String format = strings.xRepIssuerMessage_012;
			format = strings.compileMessageFormat(format, cs.getName(),  target.getName(), amount, reason);
			cs.sendMessage(format);
			dh.logRep(issuer,target,amount, RepType.XRep, reason);
			
		}else{
			cs.sendMessage(Permissions.TELL_USER_PERMISSION_THEY_LACK(Permissions.REP_FIX));
			return false;
		}
		return true;
	
	}
}
