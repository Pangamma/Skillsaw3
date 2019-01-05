///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.lumengaming.skillsaw.commands;
//
//import lumenplus.Main;
//import lumenplus.STATIC;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
///**
// * @author Taylor
// */
//public class ChatAsCommand implements CommandExecutor {
//
//	private final Main plugin;
//
//	public ChatAsCommand(Main aThis) {
//		this.plugin = aThis;
//	}
//
//	@Override
//	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
//		if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHAT_AS.node)) {
//			try {
//				Player p = STATIC.getPlayer(args[0]);
//				String msg = "";
//				for (int i = 1; i < args.length; i++) {
//					msg += args[i] + " ";
//				}
//				if (p != null) {
//					p.sendMessage(STATIC.C_SERVERLOG + cs.getName() + " ----> " + msg);
//					cs.sendMessage(STATIC.C_SERVERLOG + cs.getName() + " ----> " + msg);
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
//	}
////<editor-fold defaultstate="collapsed" desc="Accessors">
////</editor-fold>
//}
