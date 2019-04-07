package com.lumengaming.skillsaw.service;

import com.google.gson.Gson;
import com.lumengaming.skillsaw.ISkillsaw;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.models.BooleanAnswer;
import com.lumengaming.skillsaw.models.GlobalStatsView;
import com.lumengaming.skillsaw.models.RepLogEntry;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.SlogSettings;
import com.lumengaming.skillsaw.models.Title;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.UserStatsView;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.spigot.STATIC;
import java.io.BufferedReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author Taylor
 */
public class MySqlDataRepository implements IDataRepository {

    private final String username;
    private final String password;
    private final String database;
    private final int port;
    private final String host;
    private Connection connection;
    private final ISkillsaw plugin;
    private final boolean isReadOnly;

    public MySqlDataRepository(ISkillsaw p_plugin, String p_Host, int p_Port,
        String p_Username, String p_Password, String p_Database,
        boolean p_isReadOnly) {
        this.plugin = p_plugin;
        this.host = p_Host;
        this.port = p_Port;
        this.username = p_Username;
        this.password = p_Password;
        this.database = p_Database;
        this.isReadOnly = p_isReadOnly;
    }

    @Override
    public boolean onEnable() {
        return initTables();	// connect happens within init tables.
    }

    /**
     * Call to ensure a connection can be formed and is ready. *
     */
    private boolean connect() {
        try {
            if (this.connection == null) {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=false", this.username, this.password);
            } else if (this.connection != null && !this.connection.isValid(3)) {
                try {
                    this.connection.close();
                } catch (Exception ex) {
                    System.err.println(ex);
                }
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=false", this.username, this.password);
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean onDisable() {
        return this.disconnect();
    }

    @Override
    public ArrayList<User> getUsersByIP(String ipv4) {
    
        ArrayList<User> output = new ArrayList<>();
        String q = "SELECT * FROM `skillsaw_users` WHERE `ipv4` LIKE ? ORDER BY `last_played` DESC limit 1000";
        try {
            if (connect()) {
                PreparedStatement ps = this.connection.prepareStatement(q);
                ps.setString(1, ipv4);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    User u = readUser(rs);
                    if (u != null) {
                        output.add(u);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output; 
    }

    //<editor-fold defaultstate="collapsed" desc="Script RUnner">
    /*
     * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
     * from the iBATIS Apache project. Only removed dependency on Resource class
     * and a constructor 
     * GPSHansl, 06.08.2015: regex for delimiter, rearrange comment/delimiter detection, remove some ide warnings.
     */
    /*
     *  Copyright 2004 Clinton Begin
     *
     *  Licensed under the Apache License, Version 2.0 (the "License");
     *  you may not use this file except in compliance with the License.
     *  You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     *  Unless required by applicable law or agreed to in writing, software
     *  distributed under the License is distributed on an "AS IS" BASIS,
     *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *  See the License for the specific language governing permissions and
     *  limitations under the License.
     */
    /**
     * Tool to run database scripts
     */
    protected static class ScriptRunner {

        private static final String DEFAULT_DELIMITER = ";";
        /**
         * regex to detect delimiter. ignores spaces, allows delimiter in comment, allows an equals-sign
         */
        protected static final Pattern delimP = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);

        private final Connection connection;

        private final boolean stopOnError;
        private final boolean autoCommit;

        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        private PrintWriter logWriter = null; // new PrintWriter(System.out);
        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        private PrintWriter errorLogWriter = null; //new PrintWriter(System.err);

        private String delimiter = DEFAULT_DELIMITER;
        private boolean fullLineDelimiter = false;

        /**
         * Default constructor
         */
        public ScriptRunner(Connection connection, boolean autoCommit,
            boolean stopOnError) {
            this.connection = connection;
            this.autoCommit = autoCommit;
            this.stopOnError = stopOnError;
        }

        public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
            this.delimiter = delimiter;
            this.fullLineDelimiter = fullLineDelimiter;
        }

        /**
         * Setter for logWriter property
         *
         * @param logWriter - the new value of the logWriter property
         */
        public void setLogWriter(PrintWriter logWriter) {
            this.logWriter = logWriter;
        }

        /**
         * Setter for errorLogWriter property
         *
         * @param errorLogWriter - the new value of the errorLogWriter property
         */
        public void setErrorLogWriter(PrintWriter errorLogWriter) {
            this.errorLogWriter = errorLogWriter;
        }

        /**
         * Runs an SQL script (read in using the Reader parameter)
         *
         * @param reader - the source of the script
         */
        public void runScript(Reader reader) throws IOException, SQLException {
            try {
                boolean originalAutoCommit = connection.getAutoCommit();
                try {
                    if (originalAutoCommit != this.autoCommit) {
                        connection.setAutoCommit(this.autoCommit);
                    }
                    runScript(connection, reader);
                } finally {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (IOException | SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error running script.  Cause: " + e, e);
            }
        }

        /**
         * Runs an SQL script (read in using the Reader parameter) using the connection passed in
         *
         * @param conn - the connection to use for the script
         * @param reader - the source of the script
         * @throws SQLException if any SQL errors occur
         * @throws IOException if there is an error reading from the Reader
         */
        private void runScript(Connection conn, Reader reader) throws IOException,
            SQLException {
            StringBuffer command = null;
            try {
                LineNumberReader lineReader = new LineNumberReader(reader);
                String line;
                while ((line = lineReader.readLine()) != null) {
                    if (command == null) {
                        command = new StringBuffer();
                    }
                    String trimmedLine = line.trim();
                    final Matcher delimMatch = delimP.matcher(trimmedLine);
                    if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("//")) {
                        // Do nothing
                    } else if (delimMatch.matches()) {
                        setDelimiter(delimMatch.group(2), false);
                    } else if (trimmedLine.startsWith("--")) {
                        println(trimmedLine);
                    } else if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("--")) {
                        // Do nothing
                    } else if (!fullLineDelimiter
                        && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter
                        && trimmedLine.equals(getDelimiter())) {
                        command.append(line.substring(0, line
                            .lastIndexOf(getDelimiter())));
                        command.append(" ");
                        this.execCommand(conn, command, lineReader);
                        command = null;
                    } else {
                        command.append(line);
                        command.append("\n");
                    }
                }
                if (command != null) {
                    this.execCommand(conn, command, lineReader);
                }
                if (!autoCommit) {
                    conn.commit();
                }
            } catch (Exception e) {
                throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
            } finally {
                conn.rollback();
                flush();
            }
        }

        private void execCommand(Connection conn, StringBuffer command,
            LineNumberReader lineReader) throws SQLException {
            Statement statement = conn.createStatement();

            println(command);

            boolean hasResults = false;
            try {
                hasResults = statement.execute(command.toString());
            } catch (SQLException e) {
                final String errText = String.format("Error executing '%s' (line %d): %s", command, lineReader.getLineNumber(), e.getMessage());
                if (stopOnError) {
                    throw new SQLException(errText, e);
                } else {
                    println(errText);
                }
            }

            if (autoCommit && !conn.getAutoCommit()) {
                conn.commit();
            }

            ResultSet rs = statement.getResultSet();
            if (hasResults && rs != null) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    String name = md.getColumnLabel(i);
                    print(name + "\t");
                }
                println("");
                while (rs.next()) {
                    for (int i = 1; i <= cols; i++) {
                        String value = rs.getString(i);
                        print(value + "\t");
                    }
                    println("");
                }
            }

            try {
                statement.close();
            } catch (Exception e) {
                // Ignore to workaround a bug in Jakarta DBCP
            }
        }

        private String getDelimiter() {
            return delimiter;
        }

        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        private void print(Object o) {
            if (logWriter != null) {
                System.out.print(o);
            }
        }

        private void println(Object o) {
            if (logWriter != null) {
                logWriter.println(o);
            }
        }

        private void printlnError(Object o) {
            if (errorLogWriter != null) {
                errorLogWriter.println(o);
            }
        }

        private void flush() {
            if (logWriter != null) {
                logWriter.flush();
            }
            if (errorLogWriter != null) {
                errorLogWriter.flush();
            }
        }
    }
    //</editor-fold>

    private boolean initTables() {
//        if (connect()) {
//            if (!isReadOnly) {
//                try {
//                    try {
//                        MySqlDataRepository.ScriptRunner runner = new MySqlDataRepository.ScriptRunner(connection, false, true);
//                        String q0 = STATIC.getContentsOfInternalFile(plugin,"createMySQL.sql");
//                        runner.runScript(new StringReader(q0));
//                    } catch (IOException ex) {
//                        Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
//                    }
////                    
//                    String q = "SHOW COLUMNS FROM `skillsaw_users` WHERE Field LIKE 's\\_%'";
//                    PreparedStatement ps = connection.prepareStatement(q);
//                    ResultSet rs = ps.executeQuery();
//                    ArrayList<SkillType> typesFromDb = new ArrayList<>();
//                    while (rs.next()) {
//                        typesFromDb.add(
//                                new SkillType(
//                                        rs.getString("Field").replaceFirst("s_", ""),
//                                        "dummy",
//                                        rs.getInt("Default"), -1, -1, -1, "dummy", "dummy")
//                        );
//                    }
//                    ArrayList<SkillType> sts = Options.Get().getSkillTypes();
//
//                    for (SkillType st : sts) {
//                        if (st.getKey().matches("([a-zA-Z0-9_]*)")) {
//                            SkillType dbSt = null;
//                            for (SkillType dbT : typesFromDb) {
//                                if (dbT.getKey().equalsIgnoreCase(st.getKey())) {
//                                    dbSt = dbT;
//                                    break;
//                                }
//                            }
//                            if (dbSt != null) {
//                                if (dbSt.getDefLevel() != st.getDefLevel()) {
//                                    System.out.println("SkillSaw: Changing default level for " + st.getKey() + " skill type.");
//                                    ps = connection.prepareStatement("ALTER TABLE `skillsaw_users`	CHANGE COLUMN `s_" + dbSt.getKey() + "` `s_" + dbSt.getKey() + "` INT(11) NOT NULL DEFAULT '" + st.getDefLevel() + "';");
//                                    ps.execute();
//                                } else {
//                                    // Everything matches. No need to change anything.
//                                }
//                            } else {
//                                System.out.println("SkillSaw: Adding " + st.getKey() + " skill type column to MySQL db.");
//                                ps = connection.prepareStatement("ALTER TABLE `skillsaw_users` ADD COLUMN `s_" + st.getKey() + "` INT(11) NOT NULL DEFAULT '" + st.getDefLevel() + "'");
//                                ps.execute();
//                            }
//                        } else {
//                            throw new IllegalArgumentException("Key for skill types must match this regex: ([a-zA-Z0-9_]*)");
//                        }
//                    }
//                    return true;
//                } catch (SQLException ex) {
//                    Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } else {
//                return true; // read only. 
//            }
//        }
//        return false;
        return true;
    }

    private boolean disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    @Override
    public User getUser(UUID uniqueId) {
        User output = null;
        String q = "SELECT * FROM `skillsaw_users` WHERE `uuid` = ? limit 1";
        try {
            if (connect()) {
                PreparedStatement ps = this.connection.prepareStatement(q);
                ps.setString(1, uniqueId.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return readUser(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    @Override
    public void createUser(User user) {
        if (connect() && !isReadOnly) {
            try {
                String q = "INSERT INTO `skillsaw_users` "
                    + "(`uuid`, `username`,`display_name`, `ipv4`, `current_title`, `custom_titles`,"
                    + "	`chat_color`,`rep_level`,`natural_rep`,`staff_rep`,`last_played`,"
                    + "	`first_played`,`speaking_channel`,`sticky_channels`,`ignored_players`,`activity_score`,"
                    + "	`last_ping_time`, `last_ping_host`"
                    ;
                for (SkillType st : Options.Get().getSkillTypes()) {
                    q += ",`s_" + st.getKey() + "`";
                }
                q += ") VALUES (?,?,?,?,?,?   ,?,?,?,?,?    ,?,?,?,?,?    ,?,?";
                for (SkillType st : Options.Get().getSkillTypes()) {
                    q += ",?";
                }
                q += ")";
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, user.getUuid().toString());
                ps.setString(i++, user.getName());
                ps.setString(i++, user.getDisplayName().replace('§', '&'));
                ps.setString(i++, user.getIpv4());
                ps.setString(i++, user.getCurrentTitle().toString());
                ArrayList<Title> customTitlesReadOnly = user.getCustomTitlesReadOnly();
                String cTitlesStr = "";
                for (Title t : customTitlesReadOnly) {
                    cTitlesStr += t.toString() + "\n";
                }
                ps.setString(i++, cTitlesStr);
                ps.setString(i++, user.getChatColor().replace('§', '&'));
                ps.setInt(i++, user.getRepLevel());
                ps.setDouble(i++, user.getNaturalRep());
                ps.setDouble(i++, user.getStaffRep());
                ps.setLong(i++, user.getLastPlayed());
                ps.setLong(i++, user.getFirstPlayed());
                ps.setString(i++, user.getSpeakingChannel());
                ps.setString(i++, String.join("\n", user.getStickyChannels()));
                ps.setString(i++, String.join("\n", user.getIgnored()));
                ps.setInt(i++, 0); // activityScore
                ps.setLong(i++, 0); // lastPingTime
                ps.setString(i++, null); // lastPingHost

                for (SkillType st : Options.Get().getSkillTypes()) {
                    ps.setInt(i++, user.getSkill(st));
                }
                ps.execute();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean saveUser(User u) {

        String q = "UPDATE `skillsaw_users` SET "
            + "`username`=?, `display_name`=?, `ipv4`=?, `current_title`=?,"
            + "`custom_titles` = ?,`chat_color` = ?, `rep_level` = ?,"
            + "`natural_rep` = ?, `staff_rep`= ?,`last_played` = ?,"
            + "`first_played` = ?,`speaking_channel` = ?, `sticky_channels` = ?,"
            + "`ignored_players` = ?, is_staff = ?, is_instructor = ?, `activity_score` = ?,"
            + "`tpalock` = ?, `slog_settings` = ?, `last_ping_host` = ?, `last_ping_time` = ?";

        for (SkillType st : Options.Get().getSkillTypes()) {
            q += ",`s_" + st.getKey() + "` = " + u.getSkill(st);
        }
        q += " WHERE `uuid` = ?";
        try {
            if (connect() && !isReadOnly) {
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, u.getName());
                ps.setString(i++, u.getDisplayName().replace('§', '&'));
                ps.setString(i++, u.getIpv4().replace('§', '&'));
                ps.setString(i++, STATIC.makeSafe(u.getCurrentTitle().toString()));

                ArrayList<Title> customTitlesReadOnly = u.getCustomTitlesReadOnly();
                String cTitlesStr = "";
                for (Title t : customTitlesReadOnly) {
                    cTitlesStr += t.toString() + "\n";
                }
                ps.setString(i++, STATIC.makeSafe(cTitlesStr));
                ps.setString(i++, u.getChatColor().replace('§', '&'));
                ps.setInt(i++, u.getRepLevel());
                ps.setDouble(i++, u.getNaturalRep());
                ps.setDouble(i++, u.getStaffRep());
                ps.setLong(i++, u.getLastPlayed());
                ps.setLong(i++, u.getFirstPlayed());
                ps.setString(i++, u.getSpeakingChannel());
                ps.setString(i++, String.join("\n", u.getStickyChannels()));
                ps.setString(i++, String.join("\n", u.getIgnored()));
                ps.setInt(i++, u.isStaff() ? 1 : 0);
                ps.setInt(i++, u.isInstructor() ? 1 : 0);
                ps.setInt(i++, u.getActivityScore());
                ps.setString(i++, u.getTpaLockState().getShortLabel());
                ps.setString(i++, new Gson().toJson(u.getSlogSettings()));
                ps.setString(i++, u.getLastPingHost());
                ps.setLong(i++, u.getLastPingTime());

                ps.setString(i++, u.getUuid().toString());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Returns max of 50 rows where is_staff = 1.
     *
     * @return
     */
    @Override
    public ArrayList<User> getStaff() {
        ArrayList<User> output = new ArrayList<>();
        String q = "SELECT * FROM `skillsaw_users` WHERE `is_staff` = 1 ORDER BY last_played DESC limit 50";
        try {
            if (connect()) {
                PreparedStatement ps = this.connection.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    User u = readUser(rs);
                    if (u != null) {
                        output.add(u);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    /**
     * Returns max of 50 rows where is_staff = 1.
     *
     * @return
     */
    @Override
    public ArrayList<User> getInstructors() {
        ArrayList<User> output = new ArrayList<>();
        String q = "SELECT * FROM `skillsaw_users` WHERE `is_instructor` = 1 ORDER BY last_played DESC limit 50";
        try {
            if (connect()) {
                PreparedStatement ps = this.connection.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    User u = readUser(rs);
                    if (u != null) {
                        output.add(u);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    /**
     * Maximum of 1000 results. If more than that many are returned, something probably went wrong with the query.
     *
     * @param username
     * @return
     */
    @Override
    public ArrayList<User> getUsers(String username) {
        ArrayList<User> output = new ArrayList<>();
        String q = "SELECT * FROM `skillsaw_users` WHERE `username` LIKE ? limit 1000";
        try {
            if (connect()) {
                PreparedStatement ps = this.connection.prepareStatement(q);
                ps.setString(1, "%" + username + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    User u = readUser(rs);
                    if (u != null) {
                        output.add(u);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    /**
     * Expects that RS has been primed before calling. "rs.next()". Returns null on failure, also throws exception into
     * console.
     */
    private User readUser(ResultSet rs) {
        User u = null;
        try {
            ArrayList<SkillType> skillTypes = Options.Get().getSkillTypes();
            HashMap<SkillType, Integer> skills = new HashMap<>();
            for (SkillType st : skillTypes) {
                skills.put(st, rs.getInt("s_" + st.getKey()));
            }
            String customTitlesStr = rs.getString("custom_titles");
            String[] titleStrings = customTitlesStr.replace('&', '§').split("\n");
            ArrayList<Title> customTitles = new ArrayList<>();

            for (String titleStr : titleStrings) {
                Title tmp = Title.fromString(STATIC.makeUnsafe(titleStr));
                if (tmp != null) {
                    customTitles.add(tmp);
                }
            }

            boolean rsIsStaff = rs.getInt("is_staff") == 1;
            boolean rsIsInstructor = rs.getInt("is_instructor") == 1;
            UUID rsUuid = UUID.fromString(rs.getString("uuid"));
            String rsUsername = rs.getString("username");
            String rsDispName = rs.getString("display_name").replace('&', '§');
            long rsLPlayed = rs.getLong("last_played");
            long rsPlayed = rs.getLong("first_played");
            double rsNRep = rs.getDouble("natural_rep");
            double rsSRep = rs.getDouble("staff_rep");
            int rsActivityScore = rs.getInt("activity_score");
            Title rsCurTitle = Title.fromString(STATIC.makeUnsafe(rs.getString("current_title")).replace('&', '§'));
            String rsChatcolor = rs.getString("chat_color").replace('&', '§');
            String rsIpv4 = rs.getString("ipv4");
            String rsSpeakingChannel = rs.getString("speaking_channel");
            CopyOnWriteArraySet<String> rsStickie = new CopyOnWriteArraySet<>(readListFromString(rs.getString("sticky_channels")));
            CopyOnWriteArraySet<String> rsIgnored = new CopyOnWriteArraySet<>(readListFromString(rs.getString("ignored_players")));
            BooleanAnswer rsTpaLock = BooleanAnswer.fromArg(rs.getString("tpalock"));
            String rsSlogSettingsJson = rs.getString("slog_settings");
            String rsLastPingHost = rs.getString("last_ping_host");
            Long rsLastPingTime = rs.getLong("last_ping_time");

            SlogSettings rsSlogSettings = new SlogSettings();
            try {
                rsSlogSettings = new Gson().fromJson(rsSlogSettingsJson, SlogSettings.class);
            } catch (Exception ex) {
                rsSlogSettings = new SlogSettings();
            }

            u = new User(this.plugin, rsUuid, rsUsername, rsDispName, rsLPlayed, rsPlayed, rsNRep, rsSRep,
                skills, customTitles, rsCurTitle, rsChatcolor, rsIpv4, rsSpeakingChannel,
                rsStickie, rsIgnored, rsIsStaff, rsIsInstructor, rsActivityScore, rsTpaLock, rsSlogSettings,
                rsLastPingTime, rsLastPingHost);
            
            u._dbKey = rs.getInt("user_id");
            
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }


    @Override
    public GlobalStatsView getGlobalStats() {
        GlobalStatsView u = new GlobalStatsView();
        if (connect()) {
            try {
                {
                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where (`a`.`time_online` > cast((now() - interval 7 day) as datetime));";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.G_ActivityPerWeek = rs.getInt("c_minutes") / 12;
                    }
                }
                {
                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where (`a`.`time_online` > cast((now() - interval 14 day) as datetime));";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.G_ActivityPerTwoWeeks = rs.getInt("c_minutes") / 12;
                    }
                }
                {
                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where (`a`.`time_online` > cast((now() - interval 30 day) as datetime));";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.G_ActivityPerMonth = rs.getInt("c_minutes") / 12;
                    }
                }
                {
                    String q = "SELECT COUNT(*) as `c_votes` FROM votes `a` where (`a`.`time` > cast((now() - interval 7 day) as datetime));";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.G_VotesPerWeek = rs.getInt("c_votes");
                    }
                }
                {
                    String q = "SELECT COUNT(*) as `c_votes` FROM votes `a` where (`a`.`time` > cast((now() - interval 30 day) as datetime));";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.G_VotesPerMonth = rs.getInt("c_votes");
                    }
                }
                {
                    String q = "SELECT COUNT(*) as `c_votes` FROM votes `a` where (`a`.`time` > cast((now() - interval 25 hour) as datetime));";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.G_VotesPerDay = rs.getInt("c_votes");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return u;
    }
        
    @Override
    public UserStatsView getUserStats(UUID uuid) {
        UserStatsView u = new UserStatsView();
        if (connect()) {
            try {
                User user = this.getUser(uuid);
                if (user == null) return null;
                
                u.uuid = uuid;
                u.username = user.getName();
                u.U_Redstone = user.getSkill(SkillType.Redstone);
                u.U_Organics = user.getSkill(SkillType.Organics);
                u.U_PixelArt = user.getSkill(SkillType.PixelArt);
                u.U_Architecture = user.getSkill(SkillType.Architecture);
                u.U_Terraforming = user.getSkill(SkillType.Terraforming);
                u.U_Vehicles = user.getSkill(SkillType.Vehicles);
                u.U_RepLevel = user.getRepLevel();
                
                int[] skills = new int[]{u.U_Redstone, u.U_Organics, u.U_PixelArt, u.U_Architecture, u.U_Terraforming, u.U_Vehicles};
                for(int sn : skills){
                    if (sn > u.U_MaxSkill) u.U_MaxSkill = sn;
                    u.U_SkillSum += sn;
                }
                
                u.U_StaffRep = user.getStaffRep();
                u.U_NRep = user.getNaturalRep();
                u.U_IsInstructor = user.isInstructor();
                u.U_IsStaff = user.isStaff();
                
                // SELECT user_id, COUNT(*) as `c_votes`,CAST(a.`time` AS DATE) as `date` FROM votes `a` where (`a`.`time` > cast((now() - interval 25 hour) as datetime)) GROUP BY user_id, CAST(a.`time` AS DATE)
                {
                    int i = 1;
                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where (`a`.`time_online` > cast((now() - interval 7 day) as datetime)) AND `user_id` = ?;";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.setInt(i++, user._dbKey);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.U_ActivityPerWeek = rs.getInt("c_minutes") / 12;
                    }
                }
                
                u.U_ActivityPerTwoWeeks= user.getActivityScore();
//                {
//                    int i = 1;
//                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where (`a`.`time_online` > cast((now() - interval 14 day) as datetime)) AND `user_id` = ?;";
//                    PreparedStatement ps = connection.prepareStatement(q);
//                    ps.setInt(i++, user._dbKey);
//                    ResultSet rs = ps.executeQuery();
//                    if (rs.next()){
//                        u.U_ActivityPerTwoWeeks = rs.getInt("c_minutes") / 12;
//                    }
//                }
                {
                    int i = 1;
                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where (`a`.`time_online` > cast((now() - interval 30 day) as datetime)) AND `user_id` = ?;";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.setInt(i++, user._dbKey);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.U_ActivityPerMonth = rs.getInt("c_minutes") / 12;
                    }
                }
                {
                    int i = 1;
                    String q = "SELECT SUM(`a`.minutes) as `c_minutes` FROM activity_log `a` where `user_id` = ?;";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.setInt(i++, user._dbKey);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.U_ActivityTotal = rs.getInt("c_minutes") / 12;
                    }
                }
                {
                    String q = "SELECT COUNT(*) as `c_votes` FROM votes `a` where (`a`.`time` > cast((now() - interval 7 day) as datetime)) AND `user_id` = ?;";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.setInt(1, user._dbKey);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.U_VotesPerWeek = rs.getInt("c_votes");
                    }
                }
                {
                    String q = "SELECT COUNT(*) as `c_votes` FROM votes `a` where (`a`.`time` > cast((now() - interval 30 day) as datetime)) AND `user_id` = ?;";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.setInt(1, user._dbKey);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.U_VotesPerMonth = rs.getInt("c_votes");
                    }
                }
                {
                    String q = "SELECT COUNT(*) as `c_votes` FROM votes `a` where (`a`.`time` > cast((now() - interval 25 hour) as datetime)) AND `user_id` = ?;";
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.setInt(1, user._dbKey);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        u.U_VotesPerDay = rs.getInt("c_votes");
                    }
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return u;
    }
    
    @Override
    public void logVote(String username, String userIP, String serviceName) {
        String q = "INSERT INTO `votes` (`username`, `user_id`, `ipv4`, `service_name`) "
            + "VALUES (?,(SELECT `user_id` FROM `skillsaw_users` WHERE `username` = ? limit 1), ? ,?);";
     if (connect() && !isReadOnly) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, username);
                ps.setString(i++, username);
                ps.setString(i++, userIP);
                ps.setString(i++, serviceName);
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to connect to the DB. Could not log vote");
        }
    }

    @Override
    public void logRep(User issuer, User target, double amount, RepType repType, String reason) {
        String q = "INSERT INTO `replog` (`rep_type`,  `issuer_id`,  `target_id`,  `issuer_name`,  `target_name`,  `amount`,  `reason`) "
            + "VALUES (?,(SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),(SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),?,?,?,?);";
        if (connect() && !isReadOnly) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setInt(i++, repType.toInt());
                ps.setString(i++, issuer.getUuid().toString());
                ps.setString(i++, target.getUuid().toString());
                ps.setString(i++, issuer.getName());
                ps.setString(i++, target.getName());
                ps.setDouble(i++, User.round(amount));
                ps.setString(i++, reason);
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to connect to the DB. Could not log rep.");
        }
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned) {
        ArrayList<RepLogEntry> output = new ArrayList<>();
        String q
            = "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
            + "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
            + "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
            + "WHERE `rep_type` = " + type.toInt()
            + " ORDER BY r.`id` DESC "
            + " limit " + maxResultsReturned;
        if (connect()) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    RepLogEntry e = readRepLogEntry(rs);
                    if (e != null) {
                        output.add(e);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return output;
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate) {
        ArrayList<RepLogEntry> output = new ArrayList<>();
        String q
            = "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
            + "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
            + "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
            + "WHERE `rep_type` = " + type.toInt() + " AND t.uuid = ? AND r.time >= ? "
            + " ORDER BY r.`id` DESC "
            + " limit " + maxResultsReturned;
        if (connect()) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                ps.setString(1, targetUuid.toString());
                ps.setTimestamp(2, new Timestamp(minLogDate));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    RepLogEntry e = readRepLogEntry(rs);
                    if (e != null) {
                        output.add(e);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return output;
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate) {
        ArrayList<RepLogEntry> output = new ArrayList<>();
        String q
            = "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
            + "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
            + "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
            + "WHERE t.uuid = ? AND r.time >= ? "
            + " ORDER BY r.`id` DESC "
            + " limit " + maxResultsReturned;
        if (connect()) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                ps.setString(1, targetUuid.toString());
                ps.setTimestamp(2, new Timestamp(minLogDate));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    RepLogEntry e = readRepLogEntry(rs);
                    if (e != null) {
                        output.add(e);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return output;
    }

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate) {

        ArrayList<RepLogEntry> output = new ArrayList<>();
        String q
            = "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
            + "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
            + "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
            + "WHERE `rep_type` = " + type.toInt() + " AND i.uuid = ? AND r.time >= ? "
            + " ORDER BY r.`id` DESC "
            + " limit " + maxResultsReturned;
        if (connect()) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                ps.setString(1, issuerUuid.toString());
                ps.setTimestamp(2, new Timestamp(minLogDate));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    RepLogEntry e = readRepLogEntry(rs);
                    if (e != null) {
                        output.add(e);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return output;
    }

    /**
     * Expects rs is pre-primed. *
     */
    private RepLogEntry readRepLogEntry(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Timestamp time = rs.getTimestamp("time");
        String reason = rs.getString("reason");
        double amount = rs.getDouble("amount");
        RepType type = RepType.fromInt(rs.getInt("rep_type"));
        String iName = rs.getString("issuer_name");
        String tName = rs.getString("target_name");
        UUID iUUID = UUID.fromString(rs.getString("issuer_uuid"));
        UUID tUUID = UUID.fromString(rs.getString("target_uuid"));
        RepLogEntry e = new RepLogEntry(id, type, iName, iUUID, tName, tUUID, time, amount, reason);
        return e;
    }

    public void logPromotion(User issuer, User target, SkillType st, int oLevel, int nLevel, Location l) {
        String q = "INSERT INTO `promo_log` (`skill_type`,  `issuer_id`,  `target_id`,  `issuer_name`,  `target_name`,  `olevel`,`nlevel`,  `location`) "
            + "VALUES (?,IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),?,?,?,?,?);";
        if (connect() && !isReadOnly) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, st.getKey());
                ps.setString(i++, issuer.getUuid().toString());
                ps.setString(i++, target.getUuid().toString());
                ps.setString(i++, issuer.getName());
                ps.setString(i++, target.getName());
                ps.setInt(i++, oLevel);
                ps.setInt(i++, nLevel);
                ps.setString(i++, l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getWorld().getName());
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to connect to the DB. Could not log rep.");
        }
    }

    /**
     * Removes \r, splits by \n. *
     */
    private ArrayList<String> readListFromString(String s) {
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
    private String toStringFromList(ArrayList<String> list) {
        String output = "";
        if (list != null) {
            for (String s : list) {
                output += s + "\n";
            }
        }
        return output;
    }

    @Override
    public void getActivityScore(UUID uuid, boolean excludeAfk) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logActivity(UUID uuid, String serverName, boolean isAfk) {
        if (connect() && !isReadOnly) {
            try {
                String q = "INSERT INTO `skillsaw`.`activity_log` (`user_id`, `uuid`, `server`,`is_afk`) VALUES (IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),?, ?,?);";
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, uuid.toString());
                ps.setString(i++, uuid.toString());
                ps.setString(i++, serverName);
                ps.setInt(i++, isAfk ? 1 : 0);
                ps.execute();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void logMessage(UUID uuid, String p_username, String serverName, String channel, String message, boolean isCommand) {

        if (connect() && !isReadOnly) {
            try {
                String q = "INSERT INTO `messages` (`server`, `username`,`uuid`,`channel`, `message`,`is_command`) VALUES (?,?,?,?,?,?);";
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, serverName);
                ps.setString(i++, p_username);
                ps.setString(i++, uuid.toString());
                ps.setString(i++, channel);
                ps.setString(i++, message);
                ps.setInt(i++, isCommand ? 1 : 0);
                ps.execute();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void logPromotion(User issuer, User target, SkillType st, int oLevel, int nLevel, XLocation l) {

        String q = "INSERT INTO `promo_log` (`skill_type`,  `issuer_id`,  `target_id`,  `issuer_name`,  `target_name`,  `olevel`,`nlevel`,  `location`) "
            + "VALUES (?,IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),?,?,?,?,?);";
        if (connect() && !isReadOnly) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                ps.setString(i++, st.getKey());
                ps.setString(i++, issuer.getUuid().toString());
                ps.setString(i++, target.getUuid().toString());
                ps.setString(i++, issuer.getName());
                ps.setString(i++, target.getName());
                ps.setInt(i++, oLevel);
                ps.setInt(i++, nLevel);
                ps.setString(i++, l.toTeleportCommand());
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to connect to the DB. Could not log rep.");
        }
    }

    public HashMap<UUID, Integer> getUpdatedActivityScores(Set<UUID> set) {
        HashMap<UUID, Integer> map = new HashMap<>();
        if (set == null || set.isEmpty()) {
            return new HashMap<>();
        }
        List<String> qMarks = set.stream().filter(x -> x != null).map(x -> "?").collect(Collectors.toList());
        String q = "SELECT `uuid`, `activity_score` FROM skillsaw_users WHERE `uuid` IN (" + String.join(",", qMarks) + ")";
        if (connect()) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                int i = 1;
                for (UUID uuid : set) {
                    ps.setString(i++, uuid.toString());
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String uuidStr = rs.getString("uuid");
                    UUID uuid = UUID.fromString(uuidStr);
                    int cnt = rs.getInt("activity_score");
                    map.put(uuid, cnt);
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to connect to the DB. Could not update counts");
        }
        return map;
    }

    @Override
    public void refreshActivityScoresCache() {
        String q = "UPDATE "
            + "`skillsaw_users` `u` LEFT JOIN "
            + "(SELECT user_id, (round(SUM(`minutes`)/12,0)) as `c_active` FROM activity_log `a` "
            + "where (`a`.`time_online` > cast((now() - interval 14 day) as datetime)) "
            + "GROUP BY `user_id`) as `a` ON `a`.user_id = `u`.user_id "
            + "SET `u`.activity_score = IFNULL(`a`.c_active,0)  WHERE `u`.activity_score != IFNULL(`a`.c_active, 0); ";

        if (connect() && !isReadOnly) {
            try {
                PreparedStatement ps = connection.prepareStatement(q);
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to connect to the DB. Could not update activity scores rep.");
        }
    }

    @Override
    public void purgeOldMessages(int numToKeep) {
        if (connect() && !this.isReadOnly) {
            try {
                String q = "SELECT MAX(`id`) as `id` FROM `messages`;";
                PreparedStatement ps = connection.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int maxId = rs.getInt("id");
                    ps = connection.prepareStatement("DELETE FROM `messages` WHERE `id` < (" + (maxId - 500000) + ")");
                    ps.execute();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
