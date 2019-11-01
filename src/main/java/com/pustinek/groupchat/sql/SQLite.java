package com.pustinek.groupchat.sql;

import com.pustinek.groupchat.Main;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database {
    public SQLite(Main plugin) {
        super(plugin);
    }

    @Override
    HikariDataSource getDataSource() {
        Main.debug("Get data source called");
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Main.error("Failed to Initialize SQLite driver");
            Main.debug("Failed to Initialize SQLite driver");
            Main.error(e);
            return null;
        }

        File folder = plugin.getDataFolder();
        File dbFile = new File(folder, "groups.db");

        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException ex) {
                Main.error("Failed to create database file");
                Main.debug("Failed to create database file");
                Main.error(ex);
                return null;
            }
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:sqlite:" + dbFile));
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }


    /**
     * Vacuums the database to reduce file size
     *
     * @param async Whether the call should be executed asynchronously
     */
    public void vacuum(boolean async) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection con = dataSource.getConnection();
                     Statement s = con.createStatement()) {
                    s.executeUpdate("VACUUM");

                    plugin.debug("Vacuumed SQLite database");
                } catch (final SQLException ex) {
                    Main.error("Failed to vacuum database");
                    Main.debug("Failed to vacuum database");
                    Main.debug(ex.getMessage());
                }
            }
        };

        if (async) {
            runnable.runTaskAsynchronously(plugin);
        } else {
            runnable.run();
        }
    }


    @Override
    String getQueryCreateTableGroups() {
        return "CREATE TABLE IF NOT EXISTS " + tableGroups + " ("
                + "id VARCHAR(36) PRIMARY KEY NOT NULL,"
                + "name TINYTEXT NOT NULL,"
                + "owner VARCHAR(36) NOT NULL,"
                + "members TEXT NOT NULL,"
                + "options TINYTEXT NOT NULL)";
    }

    @Override
    String getQueryCreateTableInvites() {
        return "CREATE TABLE IF NOT EXISTS " + tableInvites + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "invitee VARCHAR(36) NOT NULL,"
                + "inviteeUsername TINYTEXT NOT NULL,"
                + "inviter VARCHAR(36) NOT NULL,"
                + "groupID VARCHAR(36) NOT NULL,"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
    }
}
