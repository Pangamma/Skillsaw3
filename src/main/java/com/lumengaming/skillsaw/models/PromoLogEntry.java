package com.lumengaming.skillsaw.models;

import java.sql.Timestamp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Taylor
 */
public class PromoLogEntry {
    private int id;
    private String issuerName;
    private String targetName;
    private int issuerId;
    private int targetId;
    private String skillType;
    private int oLevel;
    private int nLevel;
    private String locationStr;
    private Timestamp time;

    public PromoLogEntry(int id, String issuerName, String targetName, int issuerId, int targetId, String skillType, int oLevel, int nLevel, String locationStr, Timestamp time) {
        this.id = id;
        this.issuerName = issuerName;
        this.targetName = targetName;
        this.issuerId = issuerId;
        this.targetId = targetId;
        this.skillType = skillType;
        this.oLevel = oLevel;
        this.nLevel = nLevel;
        this.locationStr = locationStr;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getIssuerId() {
        return issuerId;
    }

    public int getTargetId() {
        return targetId;
    }

    public String getSkillType() {
        return skillType;
    }

    public int getoLevel() {
        return oLevel;
    }

    public int getnLevel() {
        return nLevel;
    }

    public String getLocationStr() {
        return locationStr;
    }
    
    /** Parses location string to try to get a Location instance. If parse
     * fails, null is returned.
     * @return 
     */
    public Location getLocation() {
        if (this.locationStr != null && this.locationStr != ""){
            String[] split = this.locationStr.split(" ");
            if (split.length >= 4){
                int x = Integer.parseInt(split[0]);
                int y = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);
                World w = Bukkit.getWorld(split[3]);
                if (w != null){
                    Location l = new Location(w,x,y,z);
                    return l;
                }
            }
        }
        return null;
    }

    public Timestamp getTime() {
        return time;
    }
    
}
