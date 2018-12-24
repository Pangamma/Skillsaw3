package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.Options.StringOptions;
import com.lumengaming.skillsaw.models.RepLogEntry;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.PERMISSION;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;

public class NaturalRepCommand extends IRepCommand{

    public NaturalRepCommand(BungeeMain plugin){
        super(plugin, "naturalrep", "rep", "nrep");
    }

    @Override
    protected boolean doRep(IPlayer cs, final User target, double amount, final String reason){
        DataService ds = plugin.getDataService();
        StringOptions strings = Options.Get().Strings;
        
        if (!(cs.isPlayer())){
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
            return false;
        }
        
        final User issuer = ds.getUserBestOnlineMatch(cs.getName());
        if (issuer == null){
            cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
            return true;
        }
        
        // Permissions?
        if (PERMISSION.USER_HAS_PERMISSION(cs, PERMISSION.REP_NATURAL,false) || issuer.getRepLevel() >= 2){
            if (issuer.getName().equalsIgnoreCase(target.getName()) && issuer.getName().equalsIgnoreCase("Pangamma")){
                cs.sendMessage("§cYou cannot rep yourself.");
                return false;
            }
            
            cs.sendMessage("§7Processing...");
            
            long timePeriodCutoff = System.currentTimeMillis() - (Options.Get().RepSystem.HoursPerTimePeriod *3600000);
            int maxReps = Options.Get().RepSystem.MaxNaturalRepsPerTimePeriod;
            
            ds.getLogEntriesByIssuer(RepType.NaturalRep,issuer.getUuid(),2000,timePeriodCutoff,
            (ArrayList<RepLogEntry> logEntries) -> {
                
                RepLogEntry oldest = null;
                RepLogEntry newest = null;
                RepLogEntry targeted = null;
                
                for(int i = 0; i < logEntries.size();i++){
                    RepLogEntry e = logEntries.get(i);
                    if (e.getTargetName().equalsIgnoreCase(target.getName())){
                        targeted = e;
                    }
                    
                    if (oldest == null){
                        oldest = e;
                    }else if (e.getTime().before(oldest.getTime())){
                        oldest = e;
                    }
                    
                    if (newest == null){
                        newest = e;
                    }else if (e.getTime().after(newest.getTime())){
                        newest = e;
                    }
                }
                
                if (targeted != null && !PERMISSION.USER_HAS_PERMISSION(cs, PERMISSION.REP_NATURAL_INF,false)){
                    long timeToWait = targeted.getTime().getTime() - timePeriodCutoff;
                    String timeStr = SharedUtility.getTimePartsString(timeToWait);
                    cs.sendMessage("§cYou must wait §4"+timeStr+"§c before you can rep this person again. Can only rep someone once within a §4"+Options.Get().RepSystem.HoursPerTimePeriod+"§c hour time period.");
                }else if (logEntries.size() >= maxReps && !PERMISSION.USER_HAS_PERMISSION(cs, PERMISSION.REP_NATURAL_INF,false)){
                    long timeToWait = oldest.getTime().getTime() - timePeriodCutoff;
                    String timeStr = SharedUtility.getTimePartsString(timeToWait);
                    cs.sendMessage("§cYou must wait §4"+timeStr+"§c before you can rep again. Limit of §4"+maxReps+"§c per §4"+Options.Get().RepSystem.HoursPerTimePeriod+"§c hour time period.");
                }else{
                    final double fAmount = target.addNaturalRep(amount, issuer);
                    double reward = getRewardForRepping(fAmount);
                    issuer.addNaturalRep(reward,issuer);
                    
                    plugin.getDataService().saveUser(target);
                    plugin.getDataService().saveUser(issuer);
                    
                    {
                        String format = strings.nRepTargetMessage_012;
                        format = strings.compileMessageFormat(format, cs.getName(),  target.getName(), fAmount, reason);
                        target.sendMessage(format);
                    }
                    
                    {
                        String format = strings.nRepIssuerMessage_012;
                        format = strings.compileMessageFormat(format, cs.getName(),  target.getName(), fAmount, reason);
                        cs.sendMessage(format);
                        cs.sendMessage("§aYou've also been awarded "+reward + " rep points for your contribution. :)");
                    }
                    
                    dh.logRep(issuer,target,fAmount, RepType.NaturalRep, reason);
                }
            });
        }else{
            cs.sendMessage(PERMISSION.TELL_USER_PERMISSION_THEY_LACK(PERMISSION.REP_NATURAL));
            return false;
        }
        return true;
    
    
    }
    
    //<editor-fold defaultstate="collapsed" desc="reward for repping">
    /** Rounded to a decimal place. **/
    public double getRewardForRepping(double initialAmount){
        double n = initialAmount / 10;
        if (n < 0){
            n *= 2;
            if (n < -10){
                n = -10;
            }
        }
        else if (n > 0 && n < 0.1){
            n = 0.1;
        }
        else if (n > 5){
            n = 5;
        }
        return User.round(n);
    }
    //</editor-fold>

    @Override
    protected void printHelp(IPlayer cs) {
        cs.sendMessage("§c/rep <target>");
        cs.sendMessage("§c/rep <target> <amount> <reason>");
    }

}
