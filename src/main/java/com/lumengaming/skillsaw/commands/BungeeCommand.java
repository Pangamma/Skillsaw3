/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.CommandSyntax;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author prota
 */
public abstract class BungeeCommand extends Command {

    protected static ArrayList<CommandSyntax> syntaxList = new ArrayList<>();
    
    protected final BungeeMain plugin;

    public BungeeCommand(BungeeMain plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender csw, String[] args) {
        BungeePlayer cs = new BungeePlayer(csw);
        
        try{
            this.execute(cs, args);
        }catch(ArrayIndexOutOfBoundsException | NumberFormatException ex){
            printHelp2(cs);
        }
    }

    public abstract void execute(BungeePlayer cs, String[] args);
    
    /**
     *
     * @param requiredPermission
     * @param hideIfNoPermission
     * @param isSubCommand
     * @param syntax
     * @param hoverText
     */
    protected void addSyntax(Permissions requiredPermission, boolean hideIfNoPermission, boolean isSubCommand, String syntax, String hoverText){
        BaseComponent[] txtSuccess;
        BaseComponent[] txtRed;
        if (hoverText == null){
            txtSuccess = CText.hoverTextSuggest("§7"+syntax, hoverText, syntax);
            txtRed = CText.hoverTextSuggest("§c"+syntax, hoverText, syntax);
        }else{
            txtSuccess = CText.hoverTextSuggest("§7"+syntax, "Click to copy", syntax);
            txtRed = CText.hoverTextSuggest("§c"+syntax, "Click to copy", syntax);
        }
        
        CommandSyntax cmd = new CommandSyntax(this.getName(), hideIfNoPermission, isSubCommand, requiredPermission, txtSuccess, txtRed);
        syntaxList.add(cmd);
    }

    public static ArrayList<CommandSyntax> getSyntaxList(boolean includeSubcommands) {
        ArrayList<CommandSyntax> output = new ArrayList<>();
        if (!includeSubcommands){
            for(CommandSyntax tx : syntaxList){
                if (!tx.isSubcommandSyntax()){
                    output.add(tx);
                }
            }
        }else{
            output.addAll(syntaxList);
        }
        return output;
    }
    
    public static ArrayList<CommandSyntax> getSyntaxList(String cmdName, boolean includeSubcommands) {
        ArrayList<CommandSyntax> output = new ArrayList<>();
        for(CommandSyntax tx : syntaxList){
            
            if (tx.isSubcommandSyntax() && !includeSubcommands){
                continue;
            }
            
            if (!tx.getCommandName().equalsIgnoreCase(cmdName)){
                continue;
            }
            
            output.add(tx);
        }
        return output;
    }
    
    public void printHelp2(IPlayer p){
        ArrayList<CommandSyntax> syntaxes = getSyntaxList(this.getName(), true);
        for(int i = 0; i < syntaxes.size(); i++){
            CommandSyntax syntax = syntaxes.get(i);
            BaseComponent[] txt = syntax.getErrorSyntax(p);
            if (txt != null){
                p.sendMessage(txt);
            }
        }
    }
        
}
