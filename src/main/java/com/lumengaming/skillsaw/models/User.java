/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.lumengaming.skillsaw.ISkillsaw;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 *
 * @author prota
 */
public class User {
    
    //<editor-fold defaultstate="collapsed" desc="Fields / Constructors">
//    protected IPlayer p;
    private final ISkillsaw plugin;
    protected String ipv4 = "127.0.0.7";
    protected UUID uuid;
    protected String name = "";
    protected String displayName = "";

    protected ArrayList<Title> customTitles = new ArrayList<Title>();
    protected Title title = new Title("§e?", "§eTitle is still loading");
    protected HashMap<SkillType, Integer> skills = new HashMap<>();
    protected double staffRep = 0;
    protected double nRep = 0;
    protected long lastPlayed = 0;
    protected long firstPlayed = 0;
    protected String chatColor = "";
    protected int LEVEL = 0;
    protected boolean isStaff = false;
    protected boolean isInstructor = false;

    private String speakingChannel;
    private CopyOnWriteArraySet<String> stickyChannels = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<String> ignored = new CopyOnWriteArraySet<>();
    private IPlayer p;
    private int activityScore = 0;
    private UUID lastWhisperedUuid;

    /**
     * Use only for creating a new default user object when one can't be pulled
     * from the repository.
     *
     * @param plugin
     * @param p
     */
    public User(ISkillsaw plugin, IPlayer p) {
        this.plugin = plugin;
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.displayName = p.getDisplayName();
        this.p = p;
        this.lastPlayed = System.currentTimeMillis();
        this.firstPlayed = p.hasPlayedBefore() ? p.getFirstPlayed() : System.currentTimeMillis();
        this.ipv4 = p.getIpv4();
        this.title = getMasterSkillTitle();
        this.speakingChannel = "1";
        this.isStaff = false;
        this.isInstructor = false;
    }

    /**
     * Copy constructor.
     *
     * @param orig *
     */
    protected User(User orig) {
        this.plugin = orig.plugin;
        this.name = orig.name;
        this.uuid = orig.uuid;
        this.ipv4 = orig.ipv4;

        this.LEVEL = orig.LEVEL;
        this.chatColor = orig.chatColor;
        this.customTitles = new ArrayList<>(orig.customTitles);
        this.displayName = orig.displayName;
        this.firstPlayed = orig.firstPlayed;
        this.lastPlayed = orig.lastPlayed;
        this.nRep = orig.nRep;
        this.skills = new HashMap<>(orig.skills);
        this.staffRep = orig.staffRep;
        this.title = orig.title;

        this.speakingChannel = orig.speakingChannel;
        this.ignored = new CopyOnWriteArraySet<>(orig.ignored);
        this.stickyChannels = new CopyOnWriteArraySet<>(orig.stickyChannels);
        this.isInstructor = orig.isInstructor;
        this.isStaff = orig.isStaff;
        this.activityScore = orig.activityScore;
        this.p = orig.p;
    }

    /**
     * Use this constructor when loading from the repository layer. *
     * @param p_plugin
     * @param p_uuid
     * @param p_username
     * @param p_displayName
     * @param p_customTitles
     * @param p_lastPlayed
     * @param p_curTitle
     * @param p_firstPlayed
     * @param p_nRep
     * @param p_ipv4
     * @param p_sRep
     * @param p_skills
     * @param p_chatColor
     * @param p_speakingChannel
     * @param p_stickyChannels
     * @param p_ignored
     * @param p_isStaff
     * @param p_isInstructor
     */
    public User(ISkillsaw p_plugin, UUID p_uuid, String p_username, String p_displayName, long p_lastPlayed,
            long p_firstPlayed, double p_nRep, double p_sRep, HashMap<SkillType, Integer> p_skills,
            ArrayList<Title> p_customTitles, Title p_curTitle, String p_chatColor, String p_ipv4,
            String p_speakingChannel, CopyOnWriteArraySet<String> p_stickyChannels,
            CopyOnWriteArraySet<String> p_ignored, boolean p_isStaff, boolean p_isInstructor, int p_activityScore) {
        this.plugin = p_plugin;
        this.uuid = p_uuid;
        this.name = p_username;
        this.displayName = p_displayName;
        this.ipv4 = p_ipv4;

        this.lastPlayed = p_lastPlayed;
        this.firstPlayed = p_firstPlayed;
        this.nRep = p_nRep;
        this.staffRep = p_sRep;
        this.skills = p_skills;
        this.customTitles = p_customTitles;
        this.chatColor = p_chatColor;
        this.LEVEL = calculateRepLevel(nRep);
        this.speakingChannel = p_speakingChannel;
        this.ignored = p_ignored;
        this.stickyChannels = p_stickyChannels;
        this.isInstructor = p_isInstructor;
        this.isStaff = p_isStaff;
        this.activityScore = p_activityScore;

        ArrayList<Title> allTitles = this.getAllTitles();
        this.title = Title.getMatchedTitle(p_curTitle, allTitles);

        if (this.title == null) {
            this.title = this.getMasterSkillTitle(); // We can create a custom title if they don't have one set.
        }
        if (p() != null) {
            p().setDisplayName(this.displayName);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Titles">
    /**
     * Returns new arraylist full of the custom titles this user has. *
     */
    public synchronized ArrayList<Title> getCustomTitlesReadOnly() {
        return new ArrayList<Title>(this.customTitles);
    }

    /**
     * @return
     * @deprecated Replace with "getCurrentTitle"
     */
    @Deprecated
    public synchronized Title getTitle() {
        return this.title;
    }

    public synchronized Title getCurrentTitle() {
        return title;
    }

    public synchronized void setTitle(Title title) {
        this.title = title;
    }

    public synchronized void addTitle(Title title) {
        this.customTitles.add(title);
    }

    public synchronized void removeTitle(Title title) {
        boolean isCurTitle = (this.title != null && this.title.equals(title));
        this.customTitles.remove(title);
        if (isCurTitle) {
            this.title = this.getAllTitles().get(0);    // Will always have SOMETHING due to master title.
        }
    }

    public final synchronized Title getMasterSkillTitle() {
        return new MasterSkillTitle(skills);
    }

    /**
     * Read only arraylist of Titles that the user has available.
     *
     * @return *
     */
    public synchronized ArrayList<Title> getAllTitles() {

        ArrayList<Title> titles = new ArrayList<Title>();

        for (SkillType st : skills.keySet()) {
            int lvl = skills.get(st);
            titles.add(st.getTitle(lvl));
        }

        titles.add(this.getMasterSkillTitle());

        titles.addAll(customTitles);

        return titles;
    }

    public boolean hasExactTitle(Title nTitle) {
        ArrayList<Title> allTitles = this.getAllTitles();
        for (int i = 0; i < allTitles.size(); i++) {
            Title t = allTitles.get(i);
            if (t.toString().equals(nTitle.toString())) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Identifiers">
    public synchronized UUID getUniqueId() {
        return uuid;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * Returns it with fully usable legacy color codes. *
     */
    public synchronized String getDisplayName() {
        return displayName;
    }

    /**
     * also sets player disp name if avail *
     */
    public synchronized void setDisplayName(String displayName) {
        if (p() != null) {
            p().setDisplayName(displayName);
        }
        this.displayName = displayName;
    }

    public synchronized String getIpv4() {
        return ipv4;
    }

    public synchronized void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    /**
     * Shows the display name as the display text. On hover, show username. On
     * click, suggest /msg username
     *
     * @return
     */
    public synchronized BaseComponent[] getNameForChat() {
        BaseComponent[] txt = CText.legacy("§e" + this.getDisplayName());
        CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Username: " + this.getName())));
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
        return txt;
    }

    /**
     * Compares the uuid. That's it. *
     */
    public synchronized boolean equals(Object o) {
        if (o != null && o instanceof User) {
            User cp = (User) o;
            return this.uuid.equals(cp.uuid);
        }
        return false;
    }

    @Override
    public synchronized int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.uuid);
        return hash;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Skills / Rep">
    public synchronized double getNaturalRep() {
        return User.round(this.nRep);
    }

    public synchronized double getStaffRep() {
        return User.round(this.staffRep);
    }

    public synchronized int getSkill(SkillType st) {
        if (!this.skills.containsKey(st)) {
            this.skills.put(st, st.getDefLevel());
        }
        return this.skills.get(st);
    }

    public synchronized void setSkill(SkillType st, int level) {
        if (this.skills.containsKey(st)) {
            Integer originalLevel = this.skills.get(st);
            Title matchedTitle = Title.getMatchedTitle(st.getTitle(originalLevel), this.getAllTitles());
            if (matchedTitle != null) {
                if (this.title.equals(matchedTitle)) {
                    this.title = st.getTitle(level);
                }
            }
        }
        this.skills.put(st, level);
    }

    /**
     * does not check to see if values are valid, does not log anything. All it
     * does is increment the value.*
     */
    public synchronized void addStaffRep(double amount) {
        this.staffRep += User.round(amount);
    }

    public int getRepLevel() {
        return this.LEVEL;
    }

    /**
     * Returns level you would be at if you had the passed in rep amount *
     */
    public static int calculateRepLevel(double normalRep) {
        double lvl = 0;
        double curSum = 0;
        if (normalRep > 0) {
            while (curSum < normalRep) {
                lvl += 1; // we increment before, because that way the next line won't barely surpass the other thing, and then like... give you the wrong level.
                curSum += lvl * lvl / 2;
            }
        } else if (normalRep < 0) {
            while (curSum > normalRep) {
                lvl -= 1; // we increment before, because that way the next line won't barely surpass the other thing, and then like... give you the wrong level.
                curSum -= lvl * lvl / 2;
            }
        }
        return (int) lvl;
    }

    public static double getRepBetweenLevelAndLevel(int lower, int higher) {
        return getMinimumRepRequiredToBeAtLevel(higher)
                - getMinimumRepRequiredToBeAtLevel(lower);
    }

    /**
     * Passed in value is 5? Calculates maximum amount of rep that you can have
     * at level 4 before being promoted to level 5.*
     */
    public static double getMinimumRepRequiredToBeAtLevel(int x) {
        double n = x;
        double requirement = n * (n - 1) * (2 * n - 1) / 6;
        requirement /= 2;
        return round(requirement);
    }

    /**
     * returns a double value showing a user their ability to rep people. *
     */
    public double getRepPower() {
        double tmp = LEVEL;
        if (tmp < 0) {
            return 0;
        } else if (tmp == 1) {
            return 0.75;
        } else {
            double bonus = this.staffRep / 10;

            for (SkillType st : this.skills.keySet()) {
                double skill = skills.get(st);
                bonus += (skill * skill / Math.max(st.getMaxLevel(), 20));
            }

            double pow = Math.pow(1.05, tmp);
            return round(tmp / 2 + pow + bonus);
        }
    }

    /**
     * returns corrected rep amount. *
     */
    public synchronized double addNaturalRep(double amount, User from) {
        //<editor-fold defaultstate="collapsed" desc="ensure amount isn't OP">
        if (amount > from.getRepPower()) {
            amount = from.getRepPower();
        } else if (amount < -from.getRepPower() / 2) {
            amount = -from.getRepPower() / 2;
        }

        //alright, it is not overpowered.
        //now shank it if they are repping more than the max change limit.
        if (amount > getRepChangeIncreaseLimit()) {
            amount = getRepChangeIncreaseLimit();
        } else if (amount < -getRepChangeDecreaseLimit()) {
            amount = -getRepChangeDecreaseLimit();
        }
        //shank if negative.
        if (amount < -from.getRepPower() / 2) {
            amount /= 2;
        }
        //</editor-fold>
        return addNaturalRep(amount);
    }

    /**
     * Adds rep without checking the amount that the sender can send, or the
     * amount the target can receive. *
     */
    public synchronized double addNaturalRep(double amount) {

        this.nRep += amount;
        int oLevel = this.LEVEL;
        this.LEVEL = User.calculateRepLevel(nRep);
        
        //throw new UnsupportedOperationException("Hey, don't forget to add the below code into the COMMAND. Check level up effects here as well.");
        
        if (p() != null && p().isValid()){
            plugin.playVillagerSound(p());
        }
        
        if (oLevel < this.LEVEL) {
            if (p() == null){
                if (!p.isValid()){
                    plugin.broadcast("ZZZZ0");
                }else{
                    plugin.broadcast("ZZZZ1");
                }
            }
            plugin.playLevelUpEffect(p(), "Reputation Level Increased", "§aCongratulations! Your total§2 Reputation Level§a has increased!");
        } else if (oLevel > this.LEVEL) {
            plugin.playLevelDownEffect(p(), "§cYour total §4Reputation Level§c has decreased.");
        }

        return round(amount);//if successful
    }

    /**
     * returns the number of rep points a that are required to complete a 100%
     * transition from one level to the next level. Make sure any rep amount
     * changes to this player do not surpass this limit. This prevents people
     * from leveling multiple times from one transaction.*
     */
    private synchronized double getRepChangeIncreaseLimit() {
        double lvl = LEVEL;
        double limit = lvl * lvl / 2;
        if (limit <= 0.5) {
            limit = 0.5;
        }
        return round(limit);
    }

    /**
     * returns the number of rep points a that are required to complete a 100%
     * transition from one level to a lower level. Make sure any rep amount
     * changes to this player do not surpass this limit. This prevents people
     * nuking each other in single transactions.*
     */
    private synchronized double getRepChangeDecreaseLimit() {
        double lvl = LEVEL - 1;
        double limit = lvl * lvl / 2;
        if (limit <= 0.5) {
            limit = 0.5;
        }
        return round(limit);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Other / Utility">
    public synchronized boolean isStaff() {
        return isStaff;
    }

    public synchronized void setIsStaff(boolean isStaff) {
        this.isStaff = isStaff;
    }

    public synchronized boolean isInstructor() {
        return isInstructor;
    }

    public synchronized void setIsInstructor(boolean isInstructor) {
        this.isInstructor = isInstructor;
    }

    /**
     * Use for those fancier join messages.
     * @return 
     */
    public synchronized long getPreviouslyPlayed(){
        return this.prevPlayed;
    }
    
    public synchronized long getLastPlayed() {
        return lastPlayed;
    }

    private long prevPlayed = Long.MAX_VALUE;
    public synchronized void setLastPlayed(long currentTimeMillis) {
        prevPlayed = this.lastPlayed;
        this.lastPlayed = currentTimeMillis;
    }

    public synchronized long getFirstPlayed() {
        return firstPlayed;
    }

    public static double round(double input) {
        double output = 0;
        DecimalFormat decim = new DecimalFormat("0.00");
        output = Double.parseDouble(decim.format(input));
        return output;
    }

    public void showStatisticsTo(IPlayer p) {
        User usr = this;
        boolean isChatEnabled =  Options.Get().ChatSystem.IsEnabled;

        Calendar lPlayed = Calendar.getInstance();
        lPlayed.setTimeInMillis(usr.getLastPlayed());
        Calendar fPlayed = Calendar.getInstance();
        fPlayed.setTimeInMillis(usr.getFirstPlayed());
        //lPlayed.get(Calendar.DAY_OF_MONTH);
        String firstPlayedStr = (fPlayed.get(Calendar.MONTH) + 1) + "/" + fPlayed.get((Calendar.DAY_OF_MONTH)) + "/" + fPlayed.get(Calendar.YEAR);
        String lastPlayedStr = (lPlayed.get(Calendar.MONTH) + 1) + "/" + lPlayed.get((Calendar.DAY_OF_MONTH)) + "/" + lPlayed.get(Calendar.YEAR);
        p.sendMessage(Constants.C_DIV_LINE);
        p.sendMessage(Constants.C_DIV_TITLE_PREFIX + "Info for " + usr.getName());
        p.sendMessage(Constants.C_DIV_LINE);
        if (usr.getFirstPlayed() == 0) {
            p.sendMessage(Constants.C_MENU_CONTENT + "first joined = §eUnknown");
        } else {
            p.sendMessage(Constants.C_MENU_CONTENT + "first joined = §e" + firstPlayedStr);
        }
        p.sendMessage(Constants.C_MENU_CONTENT + "last online = §e" + lastPlayedStr);
        
        if (isChatEnabled)  {
            p.sendMessage(Constants.C_MENU_CONTENT + "nickname = §e" + usr.getDisplayName());
            p.sendMessage(CText.hoverTextSuggest(Constants.C_MENU_CONTENT + "title = §e" + usr.getCurrentTitle().getLongTitle(), usr.getCurrentTitle().getShortTitle(), "/title " + usr.getName() + " list"));
        }
        
        if (usr.isStaff) {
            p.sendMessage(Constants.C_MENU_CONTENT + "Staff = " + (usr.isStaff() ? "§2yes" : "§eno"));
        }
        if (usr.isInstructor) {
            p.sendMessage(Constants.C_MENU_CONTENT + "Instructor = " + (usr.isInstructor() ? "§2yes" : "§eno"));
        }
        p.sendMessage(CText.hoverTextSuggest(Constants.C_MENU_CONTENT + "UUID = §e" + usr.getUniqueId(), "Click to get the UUID", usr.getUniqueId().toString()));
        p.sendMessage(Constants.C_MENU_CONTENT + "Normal Rep = §e" + usr.getNaturalRep());
        p.sendMessage(Constants.C_MENU_CONTENT + "Staff Rep = §e" + usr.getStaffRep());
        p.sendMessage(Constants.C_MENU_CONTENT + "Total Rep Level = §e" + usr.getRepLevel());
        p.sendMessage(Constants.C_MENU_CONTENT + "Repping Power = §e" + usr.getRepPower());
        
        {
            int score = usr.getActivityScore();
            String scoreStr = "";
            if (score > 350) scoreStr = "§2350+";
            else if (score > 100) scoreStr = "§2"+score;
            else if (score > 70) scoreStr = "§a"+score;
            else if (score >= 0 && (usr.isStaff || usr.isInstructor)) scoreStr = "§c"+score;
            else scoreStr = "§e"+score;
            BaseComponent[] legacy = CText.legacy(Constants.C_MENU_CONTENT + "Activity Score = " + scoreStr);
            CText.applyEvent(legacy, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, score+""));
            p.sendMessage(legacy);
        }

        double upperLim = User.getMinimumRepRequiredToBeAtLevel(usr.getRepLevel() + 1);
        double lowerLim = User.getMinimumRepRequiredToBeAtLevel(usr.getRepLevel());
        double curRep = usr.getNaturalRep();
        p.sendMessage(Constants.C_MENU_CONTENT + "Rep towards next level = (§e" + User.round(curRep - lowerLim) + "§7/§e" + User.round(upperLim - lowerLim) + "§7)");

//		ArrayList<SkillType> skillTypes = plugin.getConfigHandler().getSkillTypes();
        for (SkillType st : skills.keySet()) {
            p.sendMessage(Constants.C_MENU_CONTENT + st.getListName() + " Tier = §e" + skills.get(st));
        }
        p.sendMessage(Constants.C_DIV_LINE);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Chat">
    public synchronized String getChatColor() {
        return this.chatColor;
    }

    public synchronized void setChatColor(String p_chatColor) {
        this.chatColor = p_chatColor;
    }

    public synchronized boolean isIgnoringPlayer(String p_playerName) {
        return !p_playerName.equalsIgnoreCase(name)
                && (this.ignored.contains("*")
                || this.ignored.contains(p_playerName.toLowerCase()));
    }

    public CopyOnWriteArraySet<String> getIgnored() {
        return ignored;
    }

    /**
     * Adds a channel the user always listens to. *
     */
    public synchronized void addStickyChannel(String channelName) {
        this.stickyChannels.add(channelName.toLowerCase());
    }

    public CopyOnWriteArraySet<String> getStickyChannels() {
        return stickyChannels;
    }

    /**
     * Removes a channel the user always listens to. *
     */
    public synchronized void removeStickyChannel(String channelName) {
        this.stickyChannels.remove(channelName.toLowerCase());
    }

    /**
     * Case sensitive. *
     */
    public synchronized boolean isSpeakingOnChannel(String channelName) {
        if (channelName == null) {
            return false;
        }
        if (channelName.equals(this.speakingChannel)) {
            return true;
        }
        return false;
    }

    /**
     * Case sensitive. *
     */
    public synchronized boolean isListeningOnChannel(String channelName) {
        if (channelName == null) {
            return false;
        }
        if (channelName.equals(this.speakingChannel)) {
            return true;
        }
		if (this.stickyChannels.contains(channelName)){
			return true;
		}
		if (this.stickyChannels.contains("*") && !channelName.startsWith("_")){
			return true;
		}
		
        return  false;
    }

    public synchronized void sendMessage(String legacyText) {
        IPlayer p = p();
        if (p != null) {
            p.sendMessage(legacyText);
        }
    }

    public synchronized void sendMessage(BaseComponent message) {
        IPlayer p = p();
        if (p != null) {
            p.sendMessage(message);
        }
    }

    public synchronized void sendMessage(BaseComponent[] message) {
        IPlayer p = p();
        if (p != null) {
            p.sendMessage(message);
        }
    }

    /**
     * Returns the channel the player is currently speaking in. If opted out,
     * this will instead return, ""
     */
    public synchronized String getSpeakingChannel() {
        return this.speakingChannel;
    }

    /**
     * Sets the channel to speak on. *
     */
    public synchronized void setSpeakingChannel(String currentChannel) {
        this.speakingChannel = currentChannel;
    }
    //</editor-fold>

    public String getPwHash() {
        return "notYetSupported.";
    }

    public IPlayer p() {
        if ((p == null || !this.p.isValid()) && this.uuid != null){
            this.p = plugin.getPlayer(this.uuid);
        }
        return this.p;
    }
    
    /** You will need to cast it to ProxiedPlayer or Player or CommandSender. **/
    public Object getRawPlayer() {
        IPlayer pl = this.p();
        if (pl != null) return pl.getRaw();
        return null;
    }

    public UUID getUuid() {
        return this.getUniqueId();
    }

    public int getActivityScore() {
        return this.activityScore;
    }
    public void setActivityScore(int score) {
        this.activityScore = score;
    }

    public void setLastWhispered(UUID uniqueId) {
        this.lastWhisperedUuid = uniqueId;
    }
    
    public UUID getLastWhispered() {
        return this.lastWhisperedUuid;
    }

}
