package com.lumengaming.skillsaw.spigot;

import com.google.gson.Gson;
import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.bungee.models.RestUUIDLookup;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Taylor Love (Pangamma)
 */
public class STATIC {

    private static InputStream get(String label, InputStream in) {
        if (in != null) {
            System.err.println("null != " + label);
        }else{
            System.err.println("null == " + label);
        }
        return in;
    }


    /**
     * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGHHHHHHHHHHHHHHHHHHHHHH
     * FUCK THIS SHIT. GOD FUCKING DAMMIT. FUCK THIS. FUCK YOU. FUCK IT. FUCK 
     * EVERYTHING. GOD. SIX GOD DAMN HOURS ON THIS. WASTED. GOD FUCKING DAMMIT 
     * AAAAAAAAAAAAAAAAAAAAAARRRRRRRRRRRRRRRRRRRRRRRRRRRRRCCGHGHGHGHGHGHGHHGHGHGHHGHGHGHGHGHHGHGHGH!!!!!!!!!!!!!!!!!!!
     * 
     * (Me finding this later: "Ahhhhhh. The screams of learning. *sips coffee*")
     * @param fileName
     * @return 
     */
    public static String getContentsOfInternalFile(SpigotMain plugin, String fileName) {
        try {
            InputStream in = plugin.getResource(fileName);
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
            String script = s.hasNext() ? s.next() : "";
            return script;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //</editor-fold>

    /**
     * returns null if display name or regular name are not found. within online
     * players list.
     */
    public static Player getPlayer(String name) {
        name = name.toLowerCase();
        Player p = Bukkit.getPlayer(name);
        if (p != null && p.isOnline()) {
            return p;
        }
        for (Player n : Bukkit.getOnlinePlayers()) {
            String nick = ChatColor.stripColor(n.getDisplayName()).toLowerCase();
            if (nick.contains(name)) {
                return n;
            }
        }
        return null;
    }

    /**
     * [0] days [1] hours [2] minutes [3] seconds [4] millis
     *
     * @param ms
     */
    public static long[] getTimeParts(long ms) {
        long[] t = new long[5];
        t[0] = TimeUnit.MILLISECONDS.toDays(ms);
        t[1] = TimeUnit.MILLISECONDS.toHours(ms - TimeUnit.DAYS.toMillis(t[0]));
        t[2] = TimeUnit.MILLISECONDS.toMinutes(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]));
        t[3] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]));
        t[4] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]) - TimeUnit.SECONDS.toMillis(t[3]));
        return t;
    }

    /**
     * '1d 5h 23m 22s'
     *
     * @param ms
     * @return
     */
    public static String getTimePartsString(long ms) {
        
        long[] timeParts = getTimeParts(ms);
        String s = "";
        s += timeParts[0] + "d ";
        s += timeParts[1] + "h ";
        s += timeParts[2] + "m ";
        s += timeParts[3] + "s";
        return s;
    }

    @Deprecated
    /**
     * returns null if display name or regular name are not found. *
     */
    public static OfflinePlayer getOfflinePlayer(String name) {
        name = name.toLowerCase();
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op != null && op.hasPlayedBefore()) {
            return op;
        }
        OfflinePlayer[] list = Bukkit.getOfflinePlayers();
        for (int i = 0; i < list.length; i++) {
            op = list[i];
            if (op.getName().toLowerCase().startsWith(name)) {
                return op;
            }
        }
        return null;
    }

    /**
     * Safely encodes any utf16 chars as \\u00FF
     *
     * @param s
     * @return
     */
    public static String makeSafe(String s) {
        StringBuilder sb = new StringBuilder();
        if (s != null) {
            for (char c : s.toCharArray()) {
                if (c > '\u00FF') {
                    String format = String.format("%1X", (int) c);
                    while(format.length() < 4){ format = "0" + format;}
                    sb.append("\\u").append(format);
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
    private static final Pattern unicodePattern = Pattern.compile(".*(\\\\u[A-Fa-f0-9]{4}).*");

    public static String makeUnsafe(String s) {

        Matcher matcher = unicodePattern.matcher(s);

        char utf16Char = '\0';    // null 
        String toReplace = "";
        while (matcher.matches()) {
            toReplace = matcher.group(1);
            utf16Char = (char) Integer.parseInt(toReplace.substring(2), 16);
            s = s.replace(toReplace, "" + utf16Char);
            matcher = unicodePattern.matcher(s);
        }
        return s;
    }

    @Deprecated
    /**
     * @deprecated
     */
    public static boolean isSafeUTF8(String input) {
        input = input
                .replace("]", "")
                .replace("[", "")
                .replace(")", "")
                .replace("(", "")
                .replace("}", "")
                .replace("{", "")
                .replace("?", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("<", "")
                .replace(">", "")
                .replace(".", "")
                .replace(",", "")
                .replace(":", "");
        return input.matches("(?i)(\\d|\\w|\\s|[!@#$%^§&*_\\-+=])*");
    }

    @Deprecated
    /**
     * use this method instead: getFullNameIfOnlinePlayer returns null if no
     * player is found with X name *
     */
    public static String getFullNameOfPlayer(String partialName) {
        Player p = getPlayer(partialName);
        if (p != null) {
            return p.getName();
        }
        OfflinePlayer op = getOfflinePlayer(partialName);
        if (op != null) {
            return op.getName();
        }
        return null;
    }

    /**
     * Searches online players by display name, username. If not findable,
     * defaults to using the partial name given.
     */
    public static String getFullNameIfOnlinePlayer(String partialName) {
        Player p = getPlayer(partialName);
        if (p != null) {
            return p.getName();
        } else {
            return partialName;
        }
    }

    public static UUID getUUID(String username) {
        return getUUID(username, true);
    }

    /**
     * Returns null on failure *
     */
    public static UUID getUUID(String username, boolean fetchFromLocalIfAvailable) {
        if (username == null) {
            return null;
        }
        UUID uuid = null;
        if (fetchFromLocalIfAvailable) {
            Player p = STATIC.getPlayer(username);
            if (p != null) {
                uuid = p.getUniqueId();
            }
            if (uuid == null) {
                OfflinePlayer op = STATIC.getOfflinePlayer(username);
                if (op != null) {
                    uuid = op.getUniqueId();
                }
            }
        }
        try {
            if (uuid == null) {
                String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", "minecraft");

                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Gson gson = new Gson();
                RestUUIDLookup lookup = gson.fromJson(response.toString(), RestUUIDLookup.class);
                String id = lookup.id;
                String uuidStr = (id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
                uuid = UUID.fromString(uuidStr);
            }
        } catch (ProtocolException ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uuid;
    }

    /**
     * Returns null on failure *
     */
    public static String getUsername(UUID uuid) {
        return getUsername(uuid, true);
    }

    /**
     * Returns null on failure *
     */
    public static String getUsername(UUID uuid, boolean fetchFromLocalIfAvailable) {
        String username = null;
        if (fetchFromLocalIfAvailable) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                username = p.getName();
            } else {
                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op != null) {
                    username = op.getName();
                }
            }
        }
        if (username == null) {
            ArrayList<String> names = getNameHistory(uuid);
            if (!names.isEmpty()) {
                username = names.get(names.size() - 1);
            }
        }
        return username;
    }

    public static ArrayList<String> getNameHistory(UUID uuid) {
        ArrayList<String> output = new ArrayList<String>();
        if (uuid == null) {
            return output;
        }
        try {
            String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
            System.out.println(url);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "minecraft");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonStr = response.toString();
            Gson gson = new Gson();
            ArrayList<RestUUIDLookup> json = new ArrayList<>();
            json = gson.fromJson(jsonStr, json.getClass());
            for (int i = 0; i < json.size(); i++) {
                output.add(json.get(i).name);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
    /**
     * Removes \r, splits by \n. *
     */
    public static ArrayList<String> readListFromString(String s) {
        ArrayList<String> output = new ArrayList<String>();
        if (s != null) {
            String[] split = s.replace("\r", "").split("\n");
            for (String line : split) {
                if (!line.trim().isEmpty()) {
                    output.add(line.trim());
                }
            }
        }
        return output;
    }

    /**
     * Removes \r, splits by \n. *
     */
    public static String toStringFromList(ArrayList<String> list) {
        String output = "";
        if (list != null) {
            for (String s : list) {
                output += s + "\n";
            }
        }
        return output;
    }
}
