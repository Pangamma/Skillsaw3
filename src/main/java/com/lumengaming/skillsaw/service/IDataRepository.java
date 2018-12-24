package com.lumengaming.skillsaw.service;
import com.lumengaming.skillsaw.models.RepLogEntry;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.XLocation;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public interface IDataRepository {
	
	//<editor-fold defaultstate="collapsed" desc="Instance">
	/** Opening connections, initializing databases or files, etc **/
    public boolean onEnable();
	
	/** Closing hanging connections, saving files, etc. **/
	public boolean onDisable();
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Users">
	
    @Deprecated // ("Do we really need it?")
	public boolean hasPlayedBefore(UUID uuid);
    
    
	public User getUser(UUID uniqueId);
	
	public boolean saveUser(User user);
	
	public ArrayList<User> getUsers(String username);
	
	public void createUser(User user);
	
	public ArrayList<User> getStaff();
	
	public ArrayList<User> getInstructors();
	
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Logs">
    
    public void getActivityScore(UUID uuid, boolean excludeAfk);
    
    public void logActivity(UUID uuid, String serverName,boolean isAfk);
    
	public void logChatMessage(User user, String serverName, String message, boolean isCommand);
    
	public void logRep(User issuer, User target, double amount, RepType repType, String reason);
	
	public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned);
	
	public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type,  UUID targetUuid,int maxResultsReturned,long minLogDate);
	
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate);
	
	public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned,long minLogDate);
	
	public void logPromotion(User suer, User target, SkillType st, int oLevel, int nLevel, XLocation location);

    
//</editor-fold>

}
