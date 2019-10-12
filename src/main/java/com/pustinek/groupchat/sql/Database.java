package com.pustinek.groupchat.sql;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Callback;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

public abstract class Database {
    Main plugin;
    HikariDataSource dataSource;

    String tableGroups;
    String tableInvites;


    protected Database(Main plugin) {
        this.plugin = plugin;
    }


    abstract HikariDataSource getDataSource();

    abstract String getQueryCreateTableGroups();

    abstract String getQueryCreateTableInvites();


    public void connect(final Callback<Integer> callback) {

        this.tableGroups = Main.getConfigManager().getDatabaseConfig().getTablePrefix() + "groups";
        this.tableInvites = Main.getConfigManager().getDatabaseConfig().getTablePrefix() + "invites";

        Main.debug(tableGroups);
        new BukkitRunnable() {

            @Override
            public void run() {
                disconnect();

                try {
                    dataSource = getDataSource();
                } catch (Exception e) {
                    callback.onError(e);
                    Main.error(e);
                    return;
                }

                if (dataSource == null) {
                    Exception e = new IllegalStateException("Data source is null");
                    callback.onError(e);
                    Main.error(e);
                    return;
                }

                try (Connection con = dataSource.getConnection()) {
                    //Create group table
                    try (Statement s = con.createStatement()) {

                        s.executeUpdate(getQueryCreateTableGroups());
                    }
                    //Create invites table
                    try (Statement s = con.createStatement()) {
                        s.executeUpdate(getQueryCreateTableInvites());
                    }

                    try (Statement s = con.createStatement()) {
                        ResultSet rs = s.executeQuery("SELECT COUNT(id) FROM " + tableGroups);
                        if (rs.next()) {
                            int count = rs.getInt(1);

                            Main.debug("Initialized database with " + count + " entries");

                            if (callback != null) {
                                callback.callSyncResult(count);
                            }
                        } else {
                            throw new SQLException("Count result has no entries");

                        }
                    }
                } catch (SQLException e) {
                    if (callback != null) {
                        callback.callSyncError(e);
                    }

                    Main.error("Failed to initialize or connect to database");
                    Main.debug("Failed to initialize or connect to database");
                    Main.error(e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }


    public void addGroup(final Group group) {
        final String query = "REPLACE INTO " + tableGroups + "(id, name, owner, members, options) VALUES(?,?,?,?,?)";

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection con = dataSource.getConnection();
                     PreparedStatement ps = con.prepareStatement(query)
                ) {
                    int i = 0;
                    ps.setString(i + 1, group.getId().toString());
                    ps.setString(i + 2, group.getName());
                    ps.setString(i + 3, group.getOwner().toString());
                    ps.setString(i + 4, group.getMembersAsString());
                    ps.setString(i + 5, group.getOptionsAsString());
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    Main.error("Failed to add group to database");
                    Main.error(ex);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void addInvite(final UUID invtee, final UUID inviter, final UUID groupID, final Callback<Integer> callback) {
        final String query = "REPLACE INTO " + tableInvites + "(invitee, inviter, groupID) VALUES(?,?,?)";

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection con = dataSource.getConnection();
                     PreparedStatement ps = con.prepareStatement(query)
                ) {
                    int i = 0;
                    ps.setString(i + 1, invtee.toString());
                    ps.setString(i + 2, inviter.toString());
                    ps.setString(i + 3, groupID.toString());
                    ps.executeUpdate();

                    if (callback != null) {
                        callback.callSyncResult(1);
                    }
                } catch (SQLException ex) {
                    if (callback != null) {
                        callback.callSyncError(ex);
                    }
                    Main.error("Failed to add invite to database");
                    Main.error(ex);
                }


            }
        }.runTaskAsynchronously(plugin);

    }


    /**
     * Remove a group from the database
     *
     * @param group Group to remove
     */
    public void removeGroup(final Group group, Callback<UUID> callback) {
        new BukkitRunnable() {

            @Override
            public void run() {
                try (Connection con = dataSource.getConnection();
                     PreparedStatement ps = con.prepareStatement("DELETE FROM " + tableGroups + " WHERE id = ?")) {

                    ps.setString(1, group.getId().toString());
                    ps.executeUpdate();

                    Main.debug("Removing group from database (#" + group.getId() + ")");

                    if (callback != null) {
                        callback.callSyncResult(group.getId());
                    }
                } catch (SQLException ex) {
                    if (callback != null) {
                        callback.callSyncError(ex);
                    }
                    Main.error("Failed to add group to database");
                    Main.error(ex);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Get all groups from the database
     *
     * @param callback Callback that - if succeeded - returns a read-only
     *                 collection of all groups (as
     *                 {@code Collection<Group>})
     */
    public void getGroups(final Callback<Collection<Group>> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<Group> groups = new ArrayList<>();

                try (Connection con = dataSource.getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tableGroups + "")) {

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String id = rs.getString("id");

                        String name = rs.getString("name");
                        UUID owner = UUID.fromString(rs.getString("owner"));
                        String membersString = rs.getString("members");
                        List<String> members = Arrays.asList(rs.getString("members").split(","));
                        ArrayList<UUID> membersUUIDList = new ArrayList<>();
                        members.forEach(member -> membersUUIDList.add(UUID.fromString(member)));
                        String options = rs.getString("options");
                        Group group = new Group(UUID.fromString(id), owner, name, membersUUIDList, options);

                        groups.add(group);
                    }

                    if (callback != null) {
                        callback.callSyncResult(Collections.unmodifiableCollection(groups));
                    }

                } catch (SQLException ex) {
                    if (callback != null) {
                        callback.callSyncError(ex);
                    }
                    Main.error("Failed to add group to database");
                    Main.error(ex);
                }

            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Update group prefix
     *
     * @param callback Callback that - if succeeded - return the UUID of the changed group (as
     *                 {@code Collection<UUID>})
     */
    public void updateGroupOptions(Group newGroup, final Callback<UUID> callback) {
        new BukkitRunnable() {

            @Override
            public void run() {


                try (Connection con = dataSource.getConnection();
                     PreparedStatement ps = con.prepareStatement("UPDATE " + tableGroups
                             + " SET options = ? "
                             + "WHERE id = ?")) {

                    ps.setString(1, newGroup.getOptionsAsString());
                    ps.setString(2, newGroup.getId().toString());

                    ps.executeUpdate();

                    if (callback != null) {
                        callback.callSyncResult(newGroup.getId());
                    }

                } catch (SQLException ex) {
                    if (callback != null) {
                        callback.callSyncError(ex);
                    }
                    Main.error("Failed to update group options");
                    Main.error(ex);
                }
            }
        }.runTaskAsynchronously(plugin);

    }


    public void getInvites(final Callback<HashMap<UUID, UUID>> callback) {
        HashMap<UUID, UUID> invitesMap = new HashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection con = dataSource.getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tableInvites + "")) {

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        UUID invitee = UUID.fromString(rs.getString("invitee"));
                        UUID groupID = UUID.fromString(rs.getString("groupID"));

                        /*invited player UUID, group UUID*/
                        invitesMap.put(invitee, groupID);
                    }

                    if (callback != null) {
                        callback.callSyncResult(invitesMap);
                    }
                } catch (SQLException ex) {
                    if (callback != null) {
                        callback.callSyncError(ex);
                    }
                    Main.error("Failed to add group to database");
                    Main.error(ex);
                }
            }
        }.runTaskAsynchronously(plugin);


    }

    private void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public enum DatabaseType {
        SQLite("SQLite"),
        MySQL("MySQL");

        private String value;

        private DatabaseType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

}




