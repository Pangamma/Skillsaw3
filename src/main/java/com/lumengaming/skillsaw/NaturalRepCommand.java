/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.bungee.utility.PERMISSION;
import com.lumengaming.skillsaw.bungee.models.BungeeCommandSender;
import com.lumengaming.skillsaw.common.*;
import com.lumengaming.skillsaw.models.XLocation;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author prota
 */
public class NaturalRepCommand extends Command {
    private final BungeeMain plugin;
    XLocation lastLoc = null;

    public NaturalRepCommand(BungeeMain aThis) {
		super("naturalrep2", "staffchat.send", "rep2", "nrep2");
        this.plugin = aThis;
    }

    @Override
    public void execute(CommandSender cs, String[] strings) {
        BungeeCommandSender csw = new BungeeCommandSender(cs);
        if (PERMISSION.USER_HAS_PERMISSION(csw, PERMISSION.REP_NATURAL, true) || true){
            if (csw.isPlayer()){
                ProxiedPlayer p = csw.getPlayer();
//                plugin.getSender().doLevelUpEffect(p, "FISH ASSHOLES", "Hello?", (x) -> {
//                    ProxyServer.getInstance().broadcast("Panus");
//                });
                
                plugin.getSender().doHmmmEffect(p, (x) -> {
                    ProxyServer.getInstance().broadcast("Hmmm");
                });
                
                plugin.getSender().getPlayerLocation(p, (x) -> {
                    ProxyServer.getInstance().broadcast(x.toString());
                });
//                plugin.getSender().playSoundForPlayer(p, Constants.SND_VILLAGER_HMMM, (b) -> {});
//                plugin.getSender().getPlayerLocation(p, (loc) -> {
//                    if (lastLoc == null) lastLoc = loc;
//                    plugin.getSender().setLocation(p, lastLoc, (b) -> {
//                        lastLoc = loc;
//                        if (b == false) lastLoc = null;
//                    });
//                });
//                try (ByteArrayOutputStream b = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(b)) {
//                    out.writeUTF(Constants.CHANNEL_CONSOLE_COMMAND);
//                    String cmd = "playsound minecraft:entity.villager.ambient player "+p.getName()+" ~ ~ ~ 1 1 1 ";
//                    out.writeUTF(cmd);
////                    out.writeUTF(p.getName());
////                    out.writeUTF(p.getServer().getInfo().getName());
//                    p.getServer().sendData("BungeeCord", b.toByteArray());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}
