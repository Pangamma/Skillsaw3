package com.lumengaming.skillsaw.bungee.models;

import java.util.UUID;

/**
 *
 * @author prota
 */
public class ChatMessage {
	public String getSocketChannel(){ return "SKILLSAW_CHAT";}
	public String message;
	public String senderUUID;
	public String senderName;
	public String senderServer;
	public String chatChannel;
}
