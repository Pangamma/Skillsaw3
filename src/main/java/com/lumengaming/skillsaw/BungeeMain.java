/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.config.ConfigHelper;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.commands.skills.NoteLogCommand;
import com.lumengaming.skillsaw.commands.skills.NoteCommand;
import com.lumengaming.skillsaw.commands.skills.StaffRepLogCommand;
import com.lumengaming.skillsaw.commands.skills.NaturalRepCommand;
import com.lumengaming.skillsaw.commands.skills.SetSkillCommand;
import com.lumengaming.skillsaw.commands.skills.XRepLogCommand;
import com.lumengaming.skillsaw.commands.skills.RepLogCommand;
import com.lumengaming.skillsaw.commands.skills.XRepCommand;
import com.lumengaming.skillsaw.commands.skills.StaffRepCommand;
import com.lumengaming.skillsaw.commands.discipline.SoftMuteCommand;
import com.lumengaming.skillsaw.commands.discipline.MuteCommand;
import com.lumengaming.skillsaw.commands.discipline.MuteListCommand;
import com.lumengaming.skillsaw.commands.discipline.UnmuteCommand;
import com.lumengaming.skillsaw.commands.chat.NickCommand;
import com.lumengaming.skillsaw.commands.chat.ChannelCommand;
import com.lumengaming.skillsaw.commands.chat.MeeCommand;
import com.lumengaming.skillsaw.commands.chat.ChatColorCommand;
import com.lumengaming.skillsaw.commands.chat.ReplyCommand;
import com.lumengaming.skillsaw.commands.chat.WhisperCommand;
import com.lumengaming.skillsaw.commands.chat.TitleCommand;
import com.lumengaming.skillsaw.commands.chat.IgnoreCommand;
import com.lumengaming.skillsaw.commands.chat.GlobalCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpaCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpLockCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpHereCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpDenyCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpAcceptCommand;
import com.lumengaming.skillsaw.commands.teleportation.TpaHereCommand;
import com.lumengaming.skillsaw.config.Options.MysqlOptions;
import com.lumengaming.skillsaw.bridge.BungeeSender;
import com.lumengaming.skillsaw.commands.*;
import com.lumengaming.skillsaw.commands.admin.SlogCommand;
import com.lumengaming.skillsaw.listeners.BungeeChatListener;
import com.lumengaming.skillsaw.listeners.BungeeSlogListener;
import com.lumengaming.skillsaw.listeners.BungeePlayerActivityListener;
import com.lumengaming.skillsaw.listeners.BungeeServerCloseListener;
import com.lumengaming.skillsaw.listeners.BungeeVoteListener;
import com.lumengaming.skillsaw.listeners.BungeeWrongHostListener;
import com.lumengaming.skillsaw.models.TPRequest;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.service.MySqlDataRepository;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.ExpireMap;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 *
 * @author prota
 */
public class BungeeMain extends Plugin implements ISkillsaw {

    private final BungeeSender sender = new BungeeSender(this);
    private DataService dataService;
    private BungeePlayerActivityListener activityListener;
    private final ExpireMap<String, TPRequest> teleportRequests = new ExpireMap<>();

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        ConfigHelper.DATA_FOLDER = ProxyServer.getInstance().getPluginManager().getPlugin("Skillsaw3").getDataFolder();

        Options.Load();
        Options.Save();

        if (Options.Get().Mysql.IsEnabled) {
            MysqlOptions opt = Options.Get().Mysql;
            this.dataService = new DataService(this, new MySqlDataRepository(this, opt.Host, opt.Port, opt.User, opt.Pass, opt.Database, false));
        }

        if (this.dataService.onEnable()) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                this.dataService.loginUser(new BungeePlayer(p), (u) -> {
                });
            }
        } else {
            java.util.logging.Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, "Failed to load plugin.");
            return;
        }

        this.getProxy().getPluginManager().registerListener(this, sender);
        this.activityListener = new BungeePlayerActivityListener(this);
        this.activityListener.onEnable();
        this.getProxy().getPluginManager().registerListener(this, this.activityListener);
        this.getProxy().getPluginManager().registerCommand(this, new SetSkillCommand(this));
//        this.getProxy().getPluginManager().registerCommand(this, new SkillSawCommand(this));

        this.getProxy().getPluginManager().registerCommand(this, new SlogCommand(this));
        this.getProxy().getPluginManager().registerListener(this, new BungeeSlogListener(this));

        this.getProxy().getPluginManager().registerCommand(this, new TestCommand(this));

        if (Options.Get().Discord.IsEnabled){
            this.getProxy().getPluginManager().registerCommand(this, new DiscordCommand(this));
        }
        
        //<editor-fold defaultstate="collapsed" desc="Server Disconnect">
        if (Options.Get().ServerClosePlayerMover.IsEnabled) {
            this.getProxy().getPluginManager().registerListener(this, new BungeeServerCloseListener(this));
        }

        if (!Options.Get().ForcedHosts.isEmpty()) {
            this.getProxy().getPluginManager().registerListener(this, new BungeeWrongHostListener(this));
        }

        //<editor-fold defaultstate="collapsed" desc="Chat">
        if (Options.Get().ChatSystem.IsEnabled) {
            this.getProxy().getPluginManager().registerListener(this, new BungeeChatListener(this));
            this.getProxy().getPluginManager().registerCommand(this, new ChannelCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new ChatColorCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new GlobalCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new IgnoreCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new MeeCommand(this));
//            this.getProxy().getPluginManager().registerCommand(this, new UnmuteCommand(this));
//            this.getProxy().getPluginManager().registerCommand(this, new SoftMuteCommand(this));
//            this.getProxy().getPluginManager().registerCommand(this, new MuteCommand(this));
//            this.getProxy().getPluginManager().registerCommand(this, new MuteListCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new NickCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new WhisperCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new ReplyCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TitleCommand(this));
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Rep">
        if (Options.Get().RepSystem.IsEnabled) {
            this.getProxy().getPluginManager().registerCommand(this, new NoteCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new NaturalRepCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new StaffRepCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new XRepCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new RepLogCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new StaffRepLogCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new NoteLogCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new XRepLogCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new InstructorCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new StaffCommand(this));
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Teleports">
        if (Options.Get().Teleport.IsEnabled) {
            this.getProxy().getPluginManager().registerCommand(this, new TpLockCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TpCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TpHereCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TpaCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TpaHereCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TpAcceptCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new TpDenyCommand(this));
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Review List">
        if (Options.Get().ReviewList.IsEnabled) {
            this.getProxy().getPluginManager().registerCommand(this, new ReviewCommand(this));
        }
        //</editor-fold>

        this.getProxy().getScheduler().schedule(this, () -> {
            getTeleportRequests().purgeExpired();
        }, 2, 2, TimeUnit.SECONDS);

        if (this.getProxy().getPluginManager().getPlugin("NuVotifier") != null) {
            this.getProxy().getPluginManager().registerListener(this, new BungeeVoteListener(this));
        }
    }

    @Override
    public void onDisable() {
        this.activityListener.onDisable();
        this.getProxy().getPluginManager().unregisterCommands(this);
        this.getProxy().getPluginManager().unregisterListeners(this);

        this.dataService.onDisable();
        this.dataService = null;
        this.getProxy().getScheduler().cancel(this);
        this.teleportRequests.clear();
    }

    public BungeeSender getSender() {
        return this.sender;
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        ScheduledTask task = ProxyServer.getInstance().getScheduler().runAsync(this, runnable);
    }

    @Override
    public void runTask(Runnable runnable) {
        ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(this, runnable, 0, TimeUnit.MILLISECONDS);
    }

    public DataService getDataService() {
        return this.dataService;
    }

    @Override
    public void playVillagerSound(IPlayer p) {
        BungeePlayer bp = (BungeePlayer) p;
        this.getSender().doHmmmEffect(bp.p(), (b) -> { /* I dun currrr */ });
    }

    @Override
    public void playLevelUpEffect(IPlayer p, String title, String subtitle) {
        BungeePlayer bp = (BungeePlayer) p;
        this.getSender().doLevelUpEffect(bp.p(), title, subtitle, subtitle, (b) -> {
        });
    }

    @Override
    public void playLevelDownEffect(IPlayer p, String subtitle) {
        if (p == null) {
            return;
        }
        BungeePlayer bp = (BungeePlayer) p;
        this.getSender().doLevelDownEffect(
            bp.p(),
            "§4Level-Down",
            subtitle,
            subtitle,
            (b) -> {
            });
    }

    public DataService getApi() {
        return this.getDataService();
    }

    @Override
    public void broadcast(String legacyText) {
        this.getProxy().broadcast(CText.legacy(legacyText));
//        if (this.getProxy().getPlayer("Pangamma") != null)
//        this.getProxy().getPlayer("Pangamma").sendMessage(legacyText);
    }

    @Override
    public IPlayer getPlayer(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        ProxiedPlayer player = this.getProxy().getPlayer(uuid);
        if (player == null) {
            return null;
        }
        return new BungeePlayer(player);
    }

    public void printHelp(BungeePlayer cs) {
        cs.sendMessage(C.C_DIV_LINE);
        cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Help");
        cs.sendMessage(C.C_DIV_LINE);
        printSyntax(cs, "/skillsaw perms", "List all Skillsaw permissions.");
        printSyntax(cs, Permissions.ALL, "/skillsaw reload", "Reloads skillsaw.\nDoes a save and a load.");
        printSyntax(cs, Permissions.STAFF_LIST, "/staff list", "List staff members\nAlso shows time since\nlast login.");
        printSyntax(cs, Permissions.STAFF_MODIFY, "/staff + <player>", "Add staff\nMark a player as staff.");
        printSyntax(cs, Permissions.STAFF_MODIFY, "/staff - <player>", "Add staff\nUnmark a player as staff.");
        printSyntax(cs, Permissions.INSTRUCTORS_LIST, "/instr list", "List instructors\nAlso shows time since\nlast login and\ntheir skill tiers.");
        printSyntax(cs, Permissions.INSTRUCTORS_MODIFY, "/instr + <player>", "Add instructor\nMark a player as instructor.");
        printSyntax(cs, Permissions.INSTRUCTORS_MODIFY, "/instr - <player>", "Add instructor\nUnmark a player as instructor.");
        if (Options.Get().ChatSystem.IsEnabled) {
            printSyntax(cs, Permissions.IGNORE, "/msg <player>", "Send a private\nmessage.");
            printSyntax(cs, Permissions.IGNORE, "/r", "Reply to a private\nmessage.");
            printSyntax(cs, Permissions.IGNORE, "/ignore + <player>", "Add player name to\nyour ignore list.");
            printSyntax(cs, Permissions.IGNORE, "/ignore - <player>", "Remove player name from\nyour ignore list.");
            printSyntax(cs, Permissions.IGNORE, "/ignore *", "Ignore everyone.");
            printSyntax(cs, Permissions.IGNORE, "/ignore !*", "Clear your ignore list.\n(Ignore no one)");
            printSyntax(cs, Permissions.IGNORE, "/ignore ?", "Show the people in\nyour ignore list.");
            printSyntax(cs, "/ch <channel>", "Set your active chat\nchannel to speak on.");
            printSyntax(cs, "/ch = <channel>", "Set your active chat\nchannel to speak on.");
            printSyntax(cs, Permissions.CHANNEL_LIST, "/ch -L", "List active chat channels.");
            printSyntax(cs, Permissions.CHANNEL_INFO, "/ch -P [player]", "Show channel info for\nthe selected player,\nor for yourself if\nthe player name is not\ngiven.");
            printSyntax(cs, Permissions.CHANNEL_INFO, "/ch -I [channel]", "Show channel info for\nthe selected channel, or\nfor your current channel\nif the channel name is not\ngiven.");
            printSyntax(cs, Permissions.CHANNEL_STICKIES, "/ch + <channel>", "Add a sticky channel.\nSticky channels are\nchannels you listen \nto even if you are not\tspeaking on them.");
            printSyntax(cs, Permissions.CHANNEL_STICKIES, "/ch - <channel>", "Remove a sticky channel.\nSticky channels are\nchannels you listen \nto even if you are not\tspeaking on them.");
            printSyntax(cs, Permissions.CHAT_COLOR_BASIC, "/chatcolor &2", "Set your default chat\ncolor to §2dark green.");
            printSyntax(cs, Permissions.CHAT_COLOR_FORMATTNG, "/chatcolor &L", "Set your default chat\nformat to be §lbold.");
            printSyntax(cs, "/chatcolor &2&n", "Set your default chat\nformat to be formatted\nand colored.");

            //congratulate
            //scold
            printSyntax(cs, Permissions.CHANNEL_GLOBAL, "/g <message>", "Broadcast a global message.");

            printSyntax(cs, Permissions.MEE, "/me <action message>", "Don't use this on the\nmain channels.");
            printSyntax(cs, Permissions.MUTE, "/mute <player>", "Mute for 5 minutes.");
            printSyntax(cs, Permissions.MUTE, "/unmute <player>", "Unmute the player.");
            printSyntax(cs, Permissions.MUTE, "/mute <player> -1", "Mute until the server reboots.");
            printSyntax(cs, Permissions.MUTE, "/mutelist", "List all players that are muted right now.");
            printSyntax(cs, Permissions.MUTE, "/softmute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages.");
            printSyntax(cs, Permissions.MUTE, "/smute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages.");
        }

        if (Options.Get().RepSystem.IsEnabled) {
            printSyntax(cs, Permissions.REP_NATURAL, "/rep <player>", "See skillsaw info about\nthe selected player.");
            printSyntax(cs, Permissions.REP_NATURAL, "/rep <player> <amount> <reason>",
                "Give someone some rep for\n"
                + "the good job they've done.\n"
                + "The amount you can give is\n"
                + "capped by your own repping\n"
                + "power and their rep level.");
            printSyntax(true, cs, Permissions.REP_FIX, "/xrep <player> <amount> <reason>", "Fix their rep. Has no limits\non the amount given or\ntaken. DO NOT ABUSE THIS.");
            printSyntax(true, cs, Permissions.REP_FIX, "/srep <player> <amount> <reason>", "Give staff rep. Staff rep\nis about staff-ish behavior\npeople may be showing.");
            printSyntax(true, cs, Permissions.REP_NOTE, "/note <player> <message>", "Add a note about a player.\nPlayers are not notified about\nreceiving new notes. This is\na private thing only staff\nwill see.");
        }

        if (Options.Get().ReviewList.IsEnabled) {
            cs.sendMessage(C.C_MENU_CONTENT + "/review this");
            cs.sendMessage(C.C_MENU_CONTENT + "/review list");
            cs.sendMessage(C.C_MENU_CONTENT + "/review tp [name]");
        }

        cs.sendMessage(C.C_DIV_LINE);
    }

    private void printSyntax(boolean hideIfNoPermission, BungeePlayer cs, Permissions permRequired, String cmdSyntax, String hoverText) {
        if (Permissions.USER_HAS_PERMISSION(cs, permRequired, false)) {
            BaseComponent[] txt = CText.hoverText(C.C_MENU_CONTENT + cmdSyntax, hoverText);
            cs.sendMessage(txt);
        } else if (!hideIfNoPermission) {
            cs.sendMessage(C.C_MENU_CONTENT + "§c" + cmdSyntax);
        }
    }

    private void printSyntax(BungeePlayer cs, Permissions permRequired, String cmdSyntax, String hoverText) {
        printSyntax(false, cs, permRequired, cmdSyntax, hoverText);
    }

    private void printSyntax(BungeePlayer cs, String cmdSyntax, String hoverText) {
        BaseComponent[] txt = CText.hoverText(C.C_MENU_CONTENT + cmdSyntax, hoverText);
        cs.sendMessage(txt);
    }

    public void broadcast(BaseComponent[] text) {
        getProxy().broadcast(text);
    }

    public ExpireMap<String, TPRequest> getTeleportRequests() {
        return teleportRequests;
    }

    @Override
    public void runTaskLater(Runnable runnable, long ticks) {
        ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(this, runnable, ticks / 20, TimeUnit.SECONDS);
    }

    @Override
    public LuckPermsApi getLuckPermsAPI() {
        try {
            LuckPermsApi api = LuckPerms.getApi();
            return api;
        } catch (IllegalStateException ex) {
            Logger.getLogger("Skillsaw3").log(Level.WARNING, "Luckperms API is not available yet, but something tried to get an instance of it.");
        }
        return null;
    }
}
