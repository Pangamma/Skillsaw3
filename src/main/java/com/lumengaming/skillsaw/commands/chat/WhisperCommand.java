package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

/**
 *
 * @author Taylor
 */
public class WhisperCommand extends BungeeCommand{
    

    public WhisperCommand(BungeeMain plug){
        super(plug, "whisper",null,"msg","tell","w","message");
    }
    
    @Override
    public void execute(BungeePlayer cs, String[] args) {
        
		if (!cs.isPlayer()){
			cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
			return;
		}
		
		try{
            User cSender = plugin.getApi().getUser(cs.getUniqueId());
            User cTarget = plugin.getApi().getUserBestOnlineMatch(args[0]);
            if (cTarget == null){
                cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                return;
            }   
            
            if (cSender == null){
                cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
                return;
            }
            
            if (cTarget.isIgnoringPlayer(cSender.getName())){
                cs.sendMessage(Constants.ERROR_P_IGNORING_YOU);
                return;
            }
            
            if (cSender.isIgnoringPlayer(cTarget.getName())){
                cs.sendMessage(Constants.ERROR_P_YOU_ARE_IGNORING);
                return;
            }
                
            String s = "";
            for(int i = 1; i < args.length; i++){
                s += args[i] + " ";
            }
            s = s.trim();
            
            if (cSender != null){
                cSender.setLastWhispered(cTarget.getUniqueId());
            }
            
            if (cTarget != null){
                cTarget.setLastWhispered(cSender.getUniqueId());
            }
            
            cTarget.sendMessage(CText.hoverTextSuggest("§7From "+cSender.getName()+": "+s, "Click to reply", "/msg "+cSender.getName()+" "));
            cSender.sendMessage(CText.hoverTextSuggest("§7To "+cTarget.getName()+": "+s, "Click to message again", "/msg "+cTarget.getName()+" "));
		}catch(ArrayIndexOutOfBoundsException ex){
			cs.sendMessage(CText.legacy("§c/msg <player> <message>"));
		}
    }
	
}
