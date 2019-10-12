package com.pustinek.groupchat.sql;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends Database {
    public MySQL(Main plugin) {
        super(plugin);
    }

    @Override
    HikariDataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        DatabaseConfig dbConfig = Main.getConfigManager().getDatabaseConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s/%s?autoReconnect=true&%s",
                dbConfig.getAddress(), dbConfig.getDatabase(), dbConfig.getOptions()));
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        return new HikariDataSource(config);
    }

    /**
     * Sends an asynchronous ping to the database
     */
    public void ping() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection con = dataSource.getConnection();
                     Statement s = con.createStatement()) {
                    Main.debug("Pinging to MySQL server...");
                    s.execute("/* ping */ SELECT 1");
                } catch (SQLException ex) {
                    Main.error("Failed to ping to MySQL server. Trying to reconnect...");
                    Main.debug("Failed to ping to MySQL server. Trying to reconnect...");
                    connect(null);
                }
            }
        }.runTaskAsynchronously(plugin);
    }


    @Override
    String getQueryCreateTableGroups() {

        return "CREATE TABLE IF NOT EXISTS " + tableInvites + " ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "name TINYTEXT NOT NULL,"
                + "owner TINYTEXT NOT NULL,"
                + "members TINYTEXT NOT NULL,"
                + "options TINYTEXT NOT NULL)";
    }

    @Override
    String getQueryCreateTableInvites() {
        return "CREATE TABLE IF NOT EXISTS " + tableGroups + " ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "invitee TINYTEXT NOT NULL,"
                + "inviter TINYTEXT NOT NULL,"
                + "groupID TINYTEXT NOT NULL,"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
    }
}
