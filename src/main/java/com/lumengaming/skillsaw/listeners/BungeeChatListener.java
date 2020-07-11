package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.utility.SimRef;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Taylor
 */
public class BungeeChatListener implements Listener {

	private final BungeeMain plugin;
    private final boolean isChatEnabled;

	public BungeeChatListener(BungeeMain plug){
		this.plugin = plug;
        this.isChatEnabled = Options.Get().ChatSystem.IsEnabled;
	}
    
    private boolean _onChatHelper(final ChatEvent e, SimRef<String> outChannel, SimRef<String> outMessage){
        
        if (e.isCancelled()) return false;
		if (!(e.getSender() instanceof ProxiedPlayer)){
			return false;
		}
        
		ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
        if (!isChatEnabled) return false;
        
        BungeePlayer cs = new BungeePlayer(sender);
        
        User user = plugin.getApi().getUser(sender.getUniqueId());
        
        if (user == null) {
            sender.sendMessage(CText.legacy(C.ERROR_TRY_AGAIN_LATER_CHAT));
            return false;
        }
        
        outMessage.val(e.getMessage());
        outChannel.val(user.getSpeakingChannel());
        
        if (e.isCommand() && !outMessage.val().toLowerCase().startsWith("/ch:")){
            return false;
        }
        
        e.setCancelled(true);
        if (outMessage.val().toLowerCase().startsWith("/ch:")){
            outChannel.val(outMessage.val().split(" ")[0].trim().replace("/ch:",""));
            outMessage.val(outMessage.val().substring(outChannel.val().length()+"/ch:".length()).trim());
        }
        
        boolean hasColorBasic = Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_BASIC, false);
        boolean hasColorFormatting = Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_FORMATTNG, false);
        boolean hasColorBlack = Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_BLACK, false);
        outMessage.val(SharedUtility.removeColorCodes(outMessage.val(), hasColorFormatting, hasColorBasic, hasColorBlack, hasColorFormatting));
        
        this.doNamePingIfNamed(outChannel.val(), user.getName(), outMessage.val());
        
        HashSet<String> locales = plugin.getApi().getOnlineUsersLocales(outChannel.val());
        this.sendMessageToChannelAndFormatIt(user, null, null, outMessage.val(), outChannel.val().toLowerCase());
        
        if (!locales.isEmpty()){
            final String outMsg = outMessage.val();
            final String outCh = outChannel.val();
            plugin.runTaskAsynchronously(() -> {
                for(String locale : locales){
                    SharedUtility.translateToLocale(outMsg, locale,  (txt) -> {
                        plugin.runTask(()->{
                            this.sendMessageToChannelAndFormatIt(user, locale, outMsg, txt, outCh.toLowerCase());
                        });
                    });
                }
            });
        }
        
        return true;
    }
    
	@EventHandler(priority = 1)
	public void onChat(final ChatEvent e){
        SimRef<String> channel = new SimRef("1");
        SimRef<String> message = new SimRef(e.getMessage());
        
        boolean wasSent = _onChatHelper(e, channel, message);
		if (e.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            boolean isCommand = e.isCommand() && !message.val().startsWith("/ch:");
            this.plugin.getApi().logMessage(p.getName(),p.getUniqueId(),p.getServer().getInfo().getName(), channel.val(), message.val(), isCommand);
		}else{
            boolean isCommand = e.isCommand() && !message.val().startsWith("/ch:");
            this.plugin.getApi().logMessage("Console", UUID.fromString("db42fed2-5dbe-46ab-9f37-b8339baad38c") ,"Proxy", channel.val(), message.val(), isCommand);
        }
    }
    
    private void doNamePingIfNamed(String ch, String senderName, String rawMessage) {
        rawMessage = ChatColor.stripColor(rawMessage.replace('&', '§'));
        ArrayList<User> onlineUsersReadOnly = plugin.getDataService().getOnlineUsersReadOnly();
        for (User u : onlineUsersReadOnly) {
            if (u.isListeningOnChannel(ch)) {
                if (!u.isIgnoringPlayer(senderName)) {
                    if (u.p() != null && u.p().isPlayer() && u.p().isValid()) {
                        if (rawMessage.toLowerCase().contains(u.getName().toLowerCase()) 
                            || rawMessage.toLowerCase().contains(ChatColor.stripColor(u.getDisplayName()).toLowerCase())) {
                            Object up = u.getRawPlayer();
                            if (up != null){
                                plugin.getSender().doNameMentionedEffect((ProxiedPlayer) u.getRawPlayer(), (b) -> {
                                });
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void sendMessageToChannelAndFormatIt(final User u, String locale, String originalMessage, String msg, final String p_channel) {
        if (u == null) return;
        if (msg == null || msg.isEmpty()) return;
        if (p_channel == null || p_channel.isEmpty()) return;
//        if (plugin.getApi().isMuted(u.getUniqueId())) return; // handled in send command
        
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Channel">
        BaseComponent[] channel = new ComponentBuilder("[").color(ChatColor.WHITE)
                .append(p_channel).color(ChatColor.AQUA)
                .append("]").color(ChatColor.WHITE)
                .create();

        CText.applyEvent(channel, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ch:" + p_channel + " "));
        CText.applyEvent(channel, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("§bChat Channel")));
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Rep Level">
//        BaseComponent[] rep = new ComponentBuilder("[").color(ChatColor.WHITE).create();
//        rep = CText.merge(rep, CText.legacy("§c" + u.getRepLevel()));
//        rep = CText.merge(rep, CText.legacy("§f]"));
//        CText.applyEvent(rep, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("§cReputation level")));
//        CText.applyEvent(rep, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rep " + u.getName() + " "));
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Title">
        BaseComponent[] title = new ComponentBuilder("[").color(ChatColor.WHITE).create();
        title = CText.merge(title, CText.legacy(u.getCurrentTitle().getShortTitle()));
        title = CText.merge(title, CText.legacy("§f] "));
        CText.applyEvent(title, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(u.getCurrentTitle().getLongTitle())));
        CText.applyEvent(title, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/title " + u.getName() + " list"));
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Nickname">
        BaseComponent[] msgSep =  originalMessage == null ? CText.legacy("§f : ") : CText.hoverText("§f : ", originalMessage);
        BaseComponent[] name = CText.merge(u.getNameForChat(), msgSep);
        //</editor-fold>
        
        // NEEDS MUCH WORK.
        // TODO: Full ./ support. 
        // TODO: Red name hilight
        //<editor-fold defaultstate="collapsed" desc="Message">
        String chatColor = u.getChatColor();
        if (chatColor == null || chatColor.isEmpty()) {
            chatColor = ChatColor.GRAY.toString();
        } else {
            chatColor = chatColor.replace('&', '§');
        }
        boolean isColorOkay = u.p() != null && Permissions.USER_HAS_PERMISSION(u.p(), Permissions.CHAT_COLOR_BASIC, false);
        boolean isFormatOkay = u.p() != null && Permissions.USER_HAS_PERMISSION(u.p(), Permissions.CHAT_COLOR_FORMATTNG, false);
        boolean isBlackOkay = u.p() != null && Permissions.USER_HAS_PERMISSION(u.p(), Permissions.CHAT_COLOR_BLACK, false);
        msg = msg.replace('&', '§');
        msg = SharedUtility.removeColorCodes(msg, isFormatOkay, isColorOkay, isBlackOkay, isFormatOkay);
        
        
        BaseComponent[] message = null;
        
        if (msg.contains("./")){
            int n = msg.indexOf("./");
            message = CText.merge(
                CText.legacy(chatColor + msg.substring(0, n)),
                CText.hoverTextSuggest(chatColor + msg.substring(n,msg.length()), "Click to copy", msg.substring(n+1,msg.length()))
            );
        }else{
            message = CText.legacy(chatColor + msg);
        }
        //</editor-fold>
        
        BaseComponent[] output = channel;
        output = CText.merge(output, title);
        output = CText.merge(output, name);
        output = CText.merge(output, message);
        
        //<editor-fold defaultstate="collapsed" desc="STAFF / Instructor">
        if (u.isStaff() || u.isInstructor()) {
            String strGroups = "§2Special §2Group(s):";
            if (u.isStaff()) {
                strGroups += "\n" + "§aStaff";
            }
            if (u.isInstructor()) {
                strGroups += "\n" + "§aInstructor";
            }
            BaseComponent[] specialGroups = null;
            if (u.isStaff() && u.isInstructor()) {
                specialGroups = CText.hoverText("✪", strGroups);
            } else if (u.isStaff()) {
                specialGroups = CText.hoverText("§a✪", strGroups);
            } else if (u.isInstructor()) {
                specialGroups = CText.hoverText("§2✪", strGroups);
            }
            output = CText.merge(specialGroups, output);
        }
        //</editor-fold>
        
        plugin.getDataService().sendMessageToChannel(u.getName(), locale, p_channel, output);
    }
	
	
    
//	@EventHandler
//	public void onSlog(final ChatEvent e){
//		if (!(e.getSender() instanceof ProxiedPlayer)){
//			return;
//		}
//		ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
//		final String server = sender.getServer().getInfo().getName();
//		final String username = sender.getName();
//		final UUID uuid = sender.getUniqueId();
//		final boolean isCommand = e.isCommand() || e.getMessage().startsWith("/");
//		if (e.isCommand() && (e.getMessage().contains("/ss purge")) && sender.getName().equalsIgnoreCase("Pangamma")){
//			sender.sendMessage("Purging old message logs now.");
//            e.setCancelled(true);
//            this.plugin.getService().purgeOldMessages(500000);
//		}
//		
//	}
	
}
