package com.lumengaming.skillsaw.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Taylor
 */
public class ChatPlayer{

	private final UUID uuid;
	private String username;
	private UUID lastWhispered = null;

	public ChatPlayer(ProxiedPlayer p){
		this.uuid = p.getUniqueId();
		this.username = p.getName();
	}

	/** UUID of the player most recently whispered to. **/
	public UUID getLastWhispered(){
		return lastWhispered;
	}

	/** UUID of player most recently whispered to. **/
	public void setLastWhispered(UUID lastWhispered){
		this.lastWhispered = lastWhispered;
	}
	
	public String getName(){
		return this.username;
	}

	/**
	 * Might return null, so check for nulls. Performs a new lookup on the
	 * ProxyServer every time the method is called. *
	 */
	public ProxiedPlayer p(){
		return ProxyServer.getInstance().getPlayer(this.uuid);
	}


	public void sendMessage(BaseComponent message){
		ProxiedPlayer p = p();
		if (p != null){
			p.sendMessage(message);
		}
	}
	
	public void sendMessage(BaseComponent[] message){
		ProxiedPlayer p = p();
		if (p != null){
			p.sendMessage(message);
		}
	}

	public UUID getUUID(){
		return this.uuid;
	}
}
