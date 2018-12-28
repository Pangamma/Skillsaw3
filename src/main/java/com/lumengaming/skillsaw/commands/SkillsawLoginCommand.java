//package com.lumengaming.skillsaw.commands;
//
//import com.lumengaming.skillsaw.BungeeMain;
//import com.lumengaming.skillsaw.STATIC;
//import com.lumengaming.skillsaw.models.User;
//import com.lumengaming.skillsaw.service.DataService;
//import org.bukkit.command.Command;
//import net.md_5.bungee.api.plugin.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//public class SkillsawLoginCommand extends BungeeCommand{
//
//	
//	private final DataService service;
//
//	public SkillsawLoginCommand(BungeeMain plugin){
//		this.plugin = plugin;
//		this.service = plugin.getDataService();
//	}
//
//	@Override
//	public boolean onCommand(CommandSender csw, Command cmnd, String cmdAlias, String[] args){\r\n\r\n
//		if (cs.isPlayer()){
//			Player p = (Player) cs;
//			User user = service.getUser(p.getUniqueId());
//			p.sendMessage("§7Click this link to go to the skillsaw website. §9http://skillsaw.lumengaming.com/loginAuto.php?username="+user.getName()+"&pw_hash="+user.getPwHash());
//		}else{
//			cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
//		}
//		return true;
//	}
//	
//}