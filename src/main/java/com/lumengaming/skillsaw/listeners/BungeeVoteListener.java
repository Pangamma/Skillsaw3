package com.lumengaming.skillsaw.listeners;


import com.google.gson.Gson;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.BH;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Taylor
 */
public class BungeeVoteListener implements Listener {

	private final BungeeMain plugin;
    private final boolean isChatEnabled;
    private final Gson gson;

	public BungeeVoteListener(BungeeMain plug){
		this.plugin = plug;
        this.isChatEnabled = Options.Get().ChatSystem.IsEnabled;
        this.gson = new Gson();
	}

	@EventHandler
	public void onVote(final VotifierEvent e){
        if (e.getVote() == null) return;
        Vote vote = e.getVote();
        plugin.getApi().logVote(vote.getUsername(),  vote.getAddress(), vote.getServiceName(), () -> {
            plugin.broadcast("§d"+vote.getUsername()+" voted for the server on "+vote.getServiceName()+"!");
        });
    }
    
}
