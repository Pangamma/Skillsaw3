///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.lumengaming.skillsaw.commands;
//
//import com.lumengaming.skillsaw.BungeeMain;
//import com.lumengaming.skillsaw.spigot.STATIC;
//import com.lumengaming.skillsaw.utility.C;
//import com.lumengaming.skillsaw.wrappers.BungeePlayer;
//import org.bukkit.permissions.Permission;
//
///**
// * @author Taylor
// */
//public class ChatAsCommand extends BungeeCommand {
//
//	public ChatAsCommand(BungeeMain aThis) {
//        super(aThis, "chatas", null, "ca");
//	}
//    @Override
//    public void execute(BungeePlayer cs, String[] args) {
//        if (Permission.USER_HAS_PERMISSION(cs, Permission.CHAT_AS.node)) {
//			try {
//				Player p = STATIC.getPlayer(args[0]);
//				String msg = "";
//				for (int i = 1; i < args.length; i++) {
//					msg += args[i] + " ";
//				}
//				if (p != null) {
//					p.sendMessage(C.C_SERVERLOG + cs.getName() + " ----> " + msg);
//					cs.sendMessage(C.C_SERVERLOG + cs.getName() + " ----> " + msg);
//					p.chat(msg);
//				} else {
//					cs.sendMessage(STATIC.ERROR_PLAYER_NOT_FOUND);
//				}
//			} catch (ArrayIndexOutOfBoundsException aio) {
//				cs.sendMessage("/ca <player> <message/command>");
//			}
//		} else {
//			cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(STATIC.PERMISSION.CHAT_AS.node));
//		}
//		return true;
//    }
//}
