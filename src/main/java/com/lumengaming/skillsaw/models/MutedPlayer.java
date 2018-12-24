package com.lumengaming.skillsaw.models;

import com.lumengaming.skillsaw.utility.SharedUtility;
import java.util.Objects;
import java.util.UUID;


public class MutedPlayer implements Comparable<MutedPlayer>{
	private final UUID uuid;
	private final boolean isSoftMute;
	private final long expireAt;
	private final long secondsToLive;
    private final String name;
	
	/** Hard mute that lasts 5 minutes **/
	public MutedPlayer(UUID uuid, String username){
		this(uuid,username, false,300);
	}
	
	/** expires in 5 minutes **/
	public MutedPlayer(UUID uuid,String username, boolean isSoftMute){
		this(uuid,username, isSoftMute, 300);
	}
    
	public MutedPlayer(UUID uuid, String username, boolean isSoftMute, long  secondsToLive){
		this.uuid = uuid;
        this.name = username;
		this.isSoftMute = isSoftMute;
		this.expireAt = secondsToLive == -1 ? -1 : secondsToLive*1000 + System.currentTimeMillis();
		this.secondsToLive = secondsToLive;
	}
	
	/** D:H:M:S **/
	public String getTotalMuteTimeStr(){
		if (this.secondsToLive == -1){ return "infnite";}
		String s = "";
		long[] timeParts = SharedUtility.getTimeParts(secondsToLive*1000);
		s += timeParts[0]+"d ";
		s += timeParts[1]+"h ";
		s += timeParts[2]+"m ";
		s += timeParts[3]+"s ";
		return s;
	}
	
	/** D:H:M:S **/
	public String getTimeRemainingStr(){
		if (this.secondsToLive == -1){ return "infnite";}
		String s = "";
		long ms = expireAt - System.currentTimeMillis();
		if (ms < 0){ ms = 0;}
		long[] timeParts = SharedUtility.getTimeParts(ms);
		s += timeParts[0]+"d ";
		s += timeParts[1]+"h ";
		s += timeParts[2]+"m ";
		s += timeParts[3]+"s ";
		return s;
	}
	
	public boolean isExpired(){
		return expireAt != -1 && expireAt < System.currentTimeMillis();
	}
	
	public boolean isSoftMute(){
		return this.isSoftMute;
	}
	
	/** lowercase version
     * @return  **/
	public String getMutedPlayerName(){
		return this.name;
	}
    
	/** lowercase version
     * @return  **/
	public UUID getUniqueId(){
		return this.uuid;
	}
	
	@Override
	public boolean equals(Object o){
		if (o != null){
			if (o instanceof MutedPlayer){
				MutedPlayer mp = (MutedPlayer) o;
				if (this.uuid != null){
					return this.uuid.equals(mp.uuid);
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode(){
		int hash = 5;
		hash = 37 * hash + Objects.hashCode(this.uuid);
		return hash;
	}

	@Override
	public int compareTo(MutedPlayer o){
		return this.uuid.compareTo(this.uuid);
	}
}
