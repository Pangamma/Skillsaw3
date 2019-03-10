///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.lumengaming.skillsaw.service;
//
//import com.lumengaming.skillsaw.ISkillsaw;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import me.lucko.luckperms.api.LuckPermsApi;
//import me.lucko.luckperms.api.User;
//import me.lucko.luckperms.api.caching.UserData;
//import me.lucko.luckperms.api.context.ContextCalculator;
//import me.lucko.luckperms.api.context.ContextManager;
//import me.lucko.luckperms.api.context.MutableContextSet;
//import me.lucko.luckperms.api.manager.UserManager;
//import net.md_5.bungee.api.connection.ProxiedPlayer;
//import org.bukkit.entity.Player;
//import org.bukkit.metadata.MetadataValue;
//
///**
// *
// * @author prota
// */
//public class PermissionsService {
//    
//    private final ISkillsaw plugin;
//    public PermissionsService(ISkillsaw plug){
//        this.plugin = plug;
//    }
//    public void test(){
//        LuckPermsApi api = this.plugin.getLuckPermsAPI();
//        if (api == null) return;
//        ContextManager cm = api.getContextManager();
//        cm.registerCalculator(new ContextCalculator<Player>(){
//            @Override
//            public MutableContextSet giveApplicableContext(Player p, MutableContextSet accumulator) {
//                List<MetadataValue> metadata = p.getMetadata("");
//                for(MetadataValue v : metadata){
//                    v.getOwningPlugin();
//                }
//            }
//        });
//        
//        UserManager um = api.getUserManager();
//        CompletableFuture<User> lpUFuture = um.loadUser((UUID)null);
//        Map<String, String> map = new HashMap<>();
//        map.put("region", "something");
//        MutableContextSet skillsawContexts = MutableContextSet.fromMap(map);
//        lpUFuture.thenAcceptAsync(lpU -> {
//            UserData cd = lpU.getCachedData();
//            // Need to somehow set the user's context so that their context cache will be:
//            // "SS3_RedstoneT1" = true   (Is there a way to specify an int and then do a less/greater than op?)
//            // "SS3_RedstoneT2" = true
//            // "SS3_RedstoneT3" = true
//            // "SS3_RedstoneT4" = true
//            
//            // (And then I want the cache to be good to go until I update the cache again)
//            
//            // Now modify the user here.
//            um.saveUser(lpU);
//        });
//    }
//}
