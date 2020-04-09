/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.service;

import com.lumengaming.skillsaw.models.GlobalStatsView;
import com.lumengaming.skillsaw.models.RepLogEntry;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.UserStatsView;
import com.lumengaming.skillsaw.models.XLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author prota
 */
public class SpigotDataRepository implements IDataRepository{

    @Override
    public boolean onEnable() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean onDisable() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public User getUser(UUID uniqueId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean saveUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<User> getUsers(String username) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void createUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<User> getStaff() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<User> getInstructors() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void logVote(String username, String userIP, String serviceName) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void getActivityScore(UUID uuid, boolean excludeAfk) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void logActivity(UUID uuid, String serverName, boolean isAfk) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void purgeOldMessages(int numToKeep) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void logMessage(UUID uuid, String username, String serverName, String channel, String message, boolean isCommand) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void logRep(User issuer, User target, double amount, RepType repType, String reason) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void logPromotion(User suer, User target, SkillType st, int oLevel, int nLevel, XLocation location) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public HashMap<UUID, Integer> getUpdatedActivityScores(Set<UUID> keySet) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void refreshActivityScoresCache() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public GlobalStatsView getGlobalStats() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public UserStatsView getUserStats(UUID uuid) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<User> getUsersByIP(String ipv4) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void logDonation(String username, String packageName, double cost) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
