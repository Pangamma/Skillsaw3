package com.lumengaming.skillsaw.models;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Taylor
 */
public class ActivityRecord implements Comparable{
    private String serverName;
    private UUID uuid;
	private boolean isAfk;	// true by default. 
    /** Recommended afk=true by default **/
    public ActivityRecord(UUID plrUuid, String srvrName,boolean isAfk){
        this.serverName = srvrName;
        this.uuid = plrUuid;
		this.isAfk = isAfk;
    }  
	/** Player is afk by default. **/
	public ActivityRecord(UUID plrUuid, String srvrName){
        this(plrUuid,srvrName,true);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.uuid = playerUUID;
    }

	public boolean isAfk(){
		return isAfk;
	}

	public void setIsAfk(boolean isAfk){
		this.isAfk = isAfk;
	}
	
    @Override
    public boolean equals(Object o){
        if (o != null && o instanceof ActivityRecord){
            ActivityRecord r = (ActivityRecord) o;
            if (r.uuid != null && r.uuid.equals(this.uuid)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.uuid);
        return hash;
    }

    @Override
    public int compareTo(Object t) {
        return this.hashCode() - t.hashCode();
    }
}
