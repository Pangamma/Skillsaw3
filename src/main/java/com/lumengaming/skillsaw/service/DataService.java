/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.service;

import com.lumengaming.skillsaw.ISkillsaw;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.common.AsyncCallback;
import com.lumengaming.skillsaw.common.AsyncEmptyCallback;
import com.lumengaming.skillsaw.common.AsyncManyCallback;
import com.lumengaming.skillsaw.models.GlobalStatsView;
import com.lumengaming.skillsaw.models.MutedPlayer;
import com.lumengaming.skillsaw.models.PromoLogEntry;
import com.lumengaming.skillsaw.models.RepLogEntry;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.UserStatsView;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.ExpireMap;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author prota
 */
public class DataService {
    
	//<editor-fold defaultstate="collapsed" desc="General">
	private final IDataRepository repo;
    private final ExpireMap<String, Object> cache = new ExpireMap<>();
	private final ConcurrentHashMap<UUID, User> onlineUsers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, MutedPlayer> mutedUsers = new ConcurrentHashMap<>();
    private final ISkillsaw plugin;

	public DataService(ISkillsaw p_plugin, MySqlDataRepository p_repository) {
		this.repo = p_repository;
		this.plugin = p_plugin;
	}

	public boolean onEnable() {
        boolean success = this.repo.onEnable();
        return success;
	}

	/**
	 * Non asynchronous. *
	 */
	public boolean onDisable() {
		this.saveUsers(false);
		this.onlineUsers.clear(); // ^make sure the above is synchronous first.
		return this.repo.onDisable();
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Saving Users">
	/**
	 * Should probably run this asynchronously. *
	 */
	private void saveUsers(boolean isAsync) {
		if (isAsync) {
			plugin.runTaskAsynchronously(() -> {
                for (User u : onlineUsers.values()) {
                    DataService.this.repo.saveUser(u);
                }
            });
		} else {
			for (User u : onlineUsers.values()) {
				this.repo.saveUser(u);
			}
		}
	}

    
	/**
	 * Save is asynchronous. If user is not writable, an illegal state exception
	 * is thrown. So basically, make sure it is not being called before this is
	 * initialized.
	 *
	 * @param user
	 */
	public void saveUser(User user) {
		this.saveUser(user, true);
	}

	/**
	 * Should probably run this asynchronously. *
	 */
	public void saveUser(User user, boolean isAsync) {
		if (isAsync) {
			plugin.runTaskAsynchronously(() -> {
                repo.saveUser(user);
            });
		} else {
			this.repo.saveUser(user);
		}
	}

	/**
	 * Should probably run this asynchronously. Use this only when making users
	 * for the first time. *
	 */
	private void createUser(User user, boolean isAsync) {
		if (isAsync) {
			plugin.runTaskAsynchronously(() -> {
                repo.createUser(user);
            });
		} else {
			this.repo.createUser(user);
		}
	}
	//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Mutes">
    
    public synchronized ArrayList<MutedPlayer> getMutedPlayersReadOnly() {
		ArrayList<MutedPlayer> users = new ArrayList<>(this.mutedUsers.values());
        return users;
    }

	/** Removes any original matching mp then adds a new one. **/
	public synchronized void addMutedPlayer(MutedPlayer mp){
		this.mutedUsers.remove(mp.getUniqueId());
		this.mutedUsers.put(mp.getUniqueId(), mp);
	}
	
	/** Removes any original matching mp. Returns the original or null. **/
	public synchronized  MutedPlayer removeMutedPlayer(UUID mp){
        MutedPlayer remove = mutedUsers.remove(mp);
        return remove;
	}
	
	/** Removes any expired mutes. **/
	public synchronized  void removeExpiredMutedPlayers(){
		TreeSet<UUID> toRemove = new TreeSet<>();
		for(UUID uuid : this.mutedUsers.keySet()){
            MutedPlayer mp = this.mutedUsers.get(uuid);
			if (mp.isExpired()){
				toRemove.add(uuid);
			}
		}
        for(UUID uuid : toRemove){
            this.mutedUsers.remove(uuid);
        }
	}
	
	/** Checks non case sensitive username **/
	public synchronized boolean isMuted(UUID uuid){
		return this.mutedUsers.containsKey(uuid);
	}
	
	/** returns null if not found.
     * @param username
     * @return  **/
	public synchronized MutedPlayer getMutedPlayer(String username){
		MutedPlayer mp = null;
        for(MutedPlayer tmp : mutedUsers.values()){
            if (tmp.getMutedPlayerName().equalsIgnoreCase(username)){
                mp = tmp; break;
            }
        }
		return mp;
	}
    
	/** returns null if not found.
     * @param uuid
     * @return  **/
	public synchronized MutedPlayer getMutedPlayer(UUID  uuid){
		return mutedUsers.get(uuid);
	}
	
	/** Removes all mutes. **/
	public synchronized  void clearMutedPlayers(){
        for(MutedPlayer mp : getMutedPlayersReadOnly()){
            User u = getUser(mp.getUniqueId());
            if (u != null){
                if (!mp.isSoftMute()) {
                    u.sendMessage("§aUnmuted.");
                } else {
                    u.sendMessage("§7You can talk again. :)");
                }
            }
        }
		this.mutedUsers.clear();
	}
    //</editor-fold>
    
	//<editor-fold defaultstate="collapsed" desc="Chat">

	/**
	 * The list is read only. The users can be edited... in theory. Try to avoid
	 * editing any users within this list if you can. Concurrency is a tricky
	 * thing that creates hard to find bugs if you aren't careful. To be safe,
	 * avoid writing at all to the users from this method.
	 *
	 * @return
	 */
	public synchronized ArrayList<User> getOnlineUsersReadOnly() {
		ArrayList<User> users = new ArrayList<>(this.onlineUsers.values());
		return users;
	}

	/**
	 * Sends message to all ChatPlayers listening to the channel. *
	 */
	@Deprecated
	public synchronized void sendMessageToChannel(String channelName, BaseComponent[] message) {
		sendMessageToChannel("$", channelName, message);
	}

	/**
	 * Sends message to all ChatPlayers listening to the channel. Respects the
	 * ignored sender lists of each chat player. *
	 */
	public synchronized void sendMessageToChannel(String p_senderName, String channelName, BaseComponent[] message) {
		MutedPlayer mp = this.getMutedPlayer(p_senderName);
		if (mp != null) {
			if (!mp.isExpired()) {
				if (mp.isSoftMute()) {
					for (User cp : this.onlineUsers.values()) {
						if (cp.getName().equalsIgnoreCase(p_senderName)) {
							cp.sendMessage(message);
						}
					}
				}
				return;
			} else {
				this.removeExpiredMutedPlayers();
			}
		}
        
        
		BaseComponent[] grayMessage = null;
		for (User cp : this.onlineUsers.values()) {
			if (!cp.isIgnoringPlayer(p_senderName)) {
				if (cp.isSpeakingOnChannel(channelName)) {
					cp.sendMessage(message);
				} else if (cp.isListeningOnChannel(channelName)) {
					if (grayMessage == null){
						grayMessage = CText.clone(message);
						CText.applyColor(grayMessage,ChatColor.GRAY);
					}
					cp.sendMessage(grayMessage);
				}
			}
		}
	}

	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Login">
	/**
	 * Must be called from a synchronous context. Creates user if not exist.
	 * Otherwise updates username, ipv4, and lastPlayed time.
	 *
	 * @param p *
	 */
	public void loginUser(final IPlayer p, AsyncCallback<User> callback) {
        if (this.onlineUsers.containsKey(p.getUniqueId())){
            
        }
		if (!this.onlineUsers.containsKey(p.getUniqueId())) {
			final UUID uuid = p.getUniqueId();
			final ArrayList<SkillType> sts = Options.Get().getSkillTypes();
			this.getOfflineUser(uuid, false, (User u) -> {
                
				final User nU;
				if (u != null) {
					nU = u;
					nU.setName(p.getName());
					nU.setIpv4(p.getIpv4());
					nU.setLastPlayed(System.currentTimeMillis());
					// Insert all the possible skill types if not already present.
					for (int i = 0; i < sts.size(); i++) {
						SkillType st = sts.get(i);
						nU.getSkill(st);
					}
                    plugin.runTaskAsynchronously(() -> {
                        DataService.this.saveUser(nU, true);	// Update the user into the DB.
                        plugin.runTask(() -> {
                            onlineUsers.put(uuid, nU);
                            callback.doCallback(nU);
                        });
                    });
				} else {
					nU = new User(this.plugin, p);
					// Insert all the possible skill types if not already present.
					for (int i = 0; i < sts.size(); i++) {
						SkillType st = sts.get(i);
						nU.getSkill(st);
					}
                    plugin.runTaskAsynchronously(() -> {
                        DataService.this.createUser(nU, true);	// Update the user into the DB.
                        plugin.runTask(() -> {
                            onlineUsers.put(uuid, nU);
                            callback.doCallback(nU);
                        });
                    });
				}
			});
		} else {
			throw new IllegalStateException("Logging in a user that was already logged in? Examine the code, or this could turn into a memory leak.");
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Logout">
	/**
	 * Saves the user and removes it from the online user list. Save operation
	 * happens asynchronously. Don't use this method for closing the plugin. Use
	 * save all users instead.
	 *
	 * @param p
	 */
	public void logoutUser(final IPlayer p) {
		if (this.onlineUsers.containsKey(p.getUniqueId())) {
			User user = this.onlineUsers.remove(p.getUniqueId());
			this.saveUser(user, true);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Get">
	/**
	 * Only checks through currently online users for the data. Null if not
	 * found. *
	 */
	public User getUser(UUID uuid) {
		if (onlineUsers.containsKey(uuid)) {
			return onlineUsers.get(uuid);
		}
		return null;
	}

    public void hasPlayedBefore(UUID uniqueId, AsyncCallback<Boolean> callback) {
        getOfflineUser(uniqueId, true, (u) -> {
            if (u == null) callback.doCallback(false);
            if (u.getFirstPlayed() + 20000 < System.currentTimeMillis()) callback.doCallback(false);
            callback.doCallback(true);
        });
    }

	/**
	 * Attempts to resolve the partial name into a full name of an online
	 * player. If found, the User is returned. Otherwise null is returned. Only
	 * checks results from online users. Will make no calls to file system for
	 * the data.
	 *
	 * @param partialUsername
	 * @return
	 *
	 */
	public User getUserBestOnlineMatch(String partialUsername) {
        synchronized(this.onlineUsers){
            if (partialUsername == null) return null;
            String name = partialUsername.toLowerCase();
            User exactMatch = null;
            User bestPartialMatch = null;
            String bestPartialMatchStr = null;

            // I am fully aware that this function could be made more efficient. Total size of the collection will 
            // never be above 2000 though, so any minor efficiency glitches can be ignored. 3N would still be fine.

            // Exact
            // RealPartial
            // NickPartial
            // Any ties in length are won by the item that comes first in this list.
            for(User u : this.onlineUsers.values()){
                if (name.equalsIgnoreCase(u.getName())){
                    exactMatch = u;
                    break;
                }

                String realName = ChatColor.stripColor(u.getName()).toLowerCase();
                if (realName.startsWith(name)){
                    if (bestPartialMatch == null){
                        bestPartialMatch = u;
                        bestPartialMatchStr = realName;
                    }else if (realName.length() <= bestPartialMatchStr.length()){
                        bestPartialMatch = u;
                        bestPartialMatchStr = realName;
                    }
                }

                String dispName = ChatColor.stripColor(u.getDisplayName()).toLowerCase();
                if (dispName.startsWith(name)){
                    if (bestPartialMatch == null){
                        bestPartialMatch = u;
                        bestPartialMatchStr = dispName;
                    }else if (dispName.length() < bestPartialMatchStr.length()){
                        bestPartialMatch = u;
                        bestPartialMatchStr = dispName;
                    }
                }
            }

            if (exactMatch != null){
                return exactMatch;
            }

            if (bestPartialMatch != null){
                return bestPartialMatch;
            }

            return null;
        }
	}

	/**
	 * All users from the database where is_staff = 1 will be returned. Ordered
	 * by most recent login.
	 *
	 * @param uuid
	 * @param useCache
	 * @param callback
	 * @return *
	 */
	public void getOfflineStaff(AsyncManyCallback<User> callback) {
		plugin.runTaskAsynchronously(() -> {
			ArrayList<User> u = this.repo.getStaff();
			plugin.runTask(() -> {
				callback.doCallback(u);
			});
		});
	}

	/**
	 * All users from the database where is_instructor = 1 will be returned.
	 * Ordered by most recent login.
	 *
	 * @param uuid
	 * @param useCache
	 * @param callback
	 * @return *
	 */
	public void getOfflineInstructors(AsyncManyCallback<User> callback) {
		plugin.runTaskAsynchronously(() -> {
			ArrayList<User> u = this.repo.getInstructors();
			plugin.runTask(() -> {
				callback.doCallback(u);
			});
		});
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param uuid
	 * @param useCache
	 * @param callback
	 * @return *
	 */
	public void getOfflineUser(UUID uuid, boolean useCache, AsyncCallback<User> callback) {
		if (useCache) {
			if (this.onlineUsers.containsKey(uuid)) {
				User u = this.onlineUsers.get(uuid);
				callback.doCallback(u);
				return;
			}
		}

		plugin.runTaskAsynchronously(() -> {
			User u = this.repo.getUser(uuid);
			plugin.runTask(() -> {
				callback.doCallback(u);
			});
		});
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param username
	 * @param callback
	 * @param useCache
	 * @return *
	 */
	public void getOfflineUser(String username, boolean useCache, AsyncCallback<User> callback) {
		if (useCache) {
			synchronized (this.onlineUsers) {
                for (User u : this.onlineUsers.values()) {
					if (u.getName().equalsIgnoreCase(username)) {
						callback.doCallback(u);
						return;
					}
				}
			}
		}
		this.getOfflineUsers(username, useCache, (ArrayList<User> users) -> {
			int shortestLen = Integer.MAX_VALUE;
			User tmp = null;
			for (int i = 0; i < users.size(); i++) {
				User u = users.get(i);
				if (u.getName().length() < shortestLen) {
					shortestLen = u.getName().length();
					tmp = u;
				}
			}
			callback.doCallback(tmp);
		});
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param username
	 * @param callback
	 * @param useCache
	 * @return *
	 */
	public void getOfflineUserByNameOrDisplayName(String username, AsyncCallback<User> callback) {
        User bestMatch = this.getUserBestOnlineMatch(username);
        if (bestMatch != null){
            callback.doCallback(bestMatch);
        }else{
            this.getOfflineUsers(username, false, (ArrayList<User> users) -> {
                int shortestLen = Integer.MAX_VALUE;
                User tmp = null;
                for (int i = 0; i < users.size(); i++) {
                    User u = users.get(i);
                    if (u.getName().length() < shortestLen) {
                        shortestLen = u.getName().length();
                        tmp = u;
                    }
                }
                callback.doCallback(tmp);
            });
        }
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param username
	 * @param callback
	 * @param useCache
	 * @return *
	 */
	public void getOfflineUsers(String username, boolean useCache, AsyncManyCallback<User> callback) {
		if (useCache) {
			synchronized (this.onlineUsers) {
				for (User u : this.onlineUsers.values()) {
					if (u.getName().equalsIgnoreCase(username)) {
						ArrayList<User> us = new ArrayList<User>();
						us.add(u);
						callback.doCallback(us);
						return;
					}
				}
			}
		}

		plugin.runTaskAsynchronously(() -> {
			ArrayList<User> us = this.repo.getUsers(username);
			plugin.runTask(() -> {
				callback.doCallback(us);
			});
		});
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param username
	 * @param callback
	 * @param useCache
	 * @return *
	 */
	public void getOfflineUsersByIP(String ipv4, AsyncManyCallback<User> callback) {
        if (ipv4 == null || ipv4.isEmpty()){ callback.doCallback(new ArrayList<>()); return; }
		plugin.runTaskAsynchronously(() -> {
			ArrayList<User> us = this.repo.getUsersByIP(ipv4);
			plugin.runTask(() -> {
                ArrayList<User> uss = new ArrayList<>();
                for(User u : us){
                    User online = this.onlineUsers.get(u.getUniqueId());
                    if (online == null){
                        uss.add(u);
                    }else{
                        uss.add(online);
                    }
                }
				callback.doCallback(us);
			});
		});
	}
	//</editor-fold>
    
	//<editor-fold defaultstate="collapsed" desc="Logging">
    
    public void logVote(String username, String userIP, String serviceName, AsyncEmptyCallback callback){
        plugin.runTaskAsynchronously(() -> { 
            this.repo.logVote(username, userIP, serviceName);
            callback.doCallback();
        });
    }
    
    public void getVoteCountForLastNDays(int n, AsyncCallback<Integer> callback){
        
    }
    
    public void logMessage(String username, UUID uuid, String server, String message, boolean command) {
        this.plugin.runTaskAsynchronously(() -> { 
            repo.logMessage(uuid, username, server, message, command);
        });
    }
    
    public void logActivity(UUID playerUUID, String serverName, boolean afk) {
        plugin.runTaskAsynchronously(() -> {
            repo.logActivity(playerUUID, serverName, afk);
        });
    }
    
    /**
     * Updates cached values in server memory based on values from repository.
     * @param callback 
     */
    public void updateCalculatedCacheValues(AsyncEmptyCallback callback){
        final Set<UUID> keySet = this.onlineUsers.keySet();
        plugin.runTaskAsynchronously(() -> {
            repo.refreshActivityScoresCache();
            final HashMap<UUID, Integer> vals = repo.getUpdatedActivityScores(keySet);
            
            plugin.runTask(() -> { 
                for(UUID uuid : vals.keySet()){
                    int score = vals.get(uuid);
                    User u = this.onlineUsers.get(uuid);
                    if (u != null){
                        u.setActivityScore(score);
                    }
                }
                callback.doCallback();
            });
        });
    }
    
	/**
	 * asynchronously logs the rep to the DB or save file. *
	 */
	public void logRep(User issuer, User target, double amount, RepType repType, String reason) {
		plugin.runTaskAsynchronously(() -> {
			this.repo.logRep(issuer, target, amount, repType, reason);
		});
	}

	public void getLogEntries(RepType type, int maxResultsReturned, AsyncManyCallback<RepLogEntry> callback) {
		plugin.runTaskAsynchronously(() -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntries(type, maxResultsReturned);
			sortLogEntriesDateAscending(result);
			plugin.runTask(() -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate, AsyncManyCallback<RepLogEntry> callback) {
		plugin.runTaskAsynchronously(() -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntriesByTarget(type, targetUuid, maxResultsReturned, minLogDate);
			sortLogEntriesDateAscending(result);
			plugin.runTask(() -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate, AsyncManyCallback<RepLogEntry> callback) {
		plugin.runTaskAsynchronously(() -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntriesByTarget(targetUuid, maxResultsReturned, minLogDate);
			sortLogEntriesDateAscending(result);
			plugin.runTask(() -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByTarget(RepType type, UUID targetUuid, AsyncManyCallback<RepLogEntry> callback) {
		getLogEntriesByTarget(type, targetUuid, 1000, (System.currentTimeMillis() - 86400000), callback);
	}

	public void getLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate, AsyncManyCallback<RepLogEntry> callback) {
		plugin.runTaskAsynchronously(() -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntriesByIssuer(type, issuerUuid, maxResultsReturned, minLogDate);
			sortLogEntriesDateAscending(result);
			plugin.runTask(() -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByIssuer(RepType type, UUID issuerUuid, AsyncManyCallback<RepLogEntry> callback) {
		getLogEntriesByIssuer(type, issuerUuid, 1000, (System.currentTimeMillis() - 86400000), callback);
	}

	private void sortLogEntriesDateAscending(ArrayList<RepLogEntry> p_toBeSorted) {
		p_toBeSorted.sort(new Comparator<RepLogEntry>() {
			@Override
			public int compare(RepLogEntry t, RepLogEntry t1) {
				return t.getId() - t1.getId();
			}
		});
	}

	/**
	 * Log a skilltype level change. *
	 */
	public void logPromotion(User issuer, User target, SkillType st, int oLevel, int nLevel, XLocation location) {
		plugin.runTaskAsynchronously(() -> {
			DataService.this.repo.logPromotion(issuer, target, st, oLevel, nLevel, location);
		});
	}

	public void rollbackUsers(ArrayList<String> p_userNames, ArrayList<Integer> p_userIds) {
		ArrayList<String> userNames = new ArrayList<>();
		HashMap<String, User> affectedUsers = new HashMap<String, User>();
		for (String s : p_userNames) {
			userNames.add(s.toLowerCase());
		}
		this.getPromotionLogEntries(Timestamp.from(Instant.now().minus(20, ChronoUnit.DAYS)), Timestamp.from(Instant.now()), Integer.MAX_VALUE, (ArrayList<PromoLogEntry> entries) -> {
			LinkedList<PromoLogEntry> badEntries = new LinkedList<PromoLogEntry>();
			for (PromoLogEntry entry : entries) {
				if (p_userIds.contains(entry.getIssuerId()) || p_userNames.contains(entry.getIssuerName().toLowerCase())) {
					badEntries.addLast(entry);
				}
			}
		});
	}

	public void getPromotionLogEntries(AsyncManyCallback<PromoLogEntry> callback) {

	}

	public void getPromotionLogEntries(Timestamp beginTime, Timestamp endTime, int limit, AsyncManyCallback<PromoLogEntry> callback) {

	}
	//</editor-fold>

    public void purgeOldMessages(int numToKeep) {
        plugin.runTaskAsynchronously(() -> { 
            this.repo.purgeOldMessages(numToKeep);
        });
    }
    
    public void getGlobalStats(boolean useCache, AsyncCallback<GlobalStatsView> callback){
        if (useCache && this.cache.contains(C.CK_GlobalStats)){
            GlobalStatsView ov = (GlobalStatsView) this.cache.get(C.CK_GlobalStats);
            callback.doCallback(ov);
            return;
        }
        
        plugin.runTaskAsynchronously(() -> {
            GlobalStatsView view = repo.getGlobalStats();
            plugin.runTask(() -> { 
                this.cache.put(C.CK_GlobalStats, view, Duration.ofMinutes(6));
                callback.doCallback(view);
            });
        });
    }
    
    public void getIndividualStats(UUID uuid, boolean useCache, AsyncCallback<UserStatsView> callback){
        String key = C.CK_IndividualStats+uuid.toString();
        if (useCache && this.cache.contains(key)){
            UserStatsView ov = (UserStatsView) this.cache.get(key);
            callback.doCallback(ov);
            return;
        }
        
        plugin.runTaskAsynchronously(() -> {
            UserStatsView view = repo.getUserStats(uuid);
            plugin.runTask(() -> { 
                this.cache.put(key, view, Duration.ofMinutes(6));
                callback.doCallback(view);
            });
        });
    }
}
