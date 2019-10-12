package com.pustinek.groupchat.models;

public class DatabaseConfig {
    private String driver;
    private String address;
    private String database;
    private String username;
    private String password;
    private String tablePrefix;
    private String options;
    private Integer ping_interval;

    public DatabaseConfig(String driver, String address, String database, String username, String password, String tablePrefix, String options, Integer ping_interval) {
        this.driver = driver;
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
        this.tablePrefix = tablePrefix;
        this.options = options;
        this.ping_interval = ping_interval;
    }


    public String getDriver() {
        return driver;
    }

    public String getAddress() {
        return address;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getOptions() {
        return options;
    }

    public Integer getPing_interval() {
        return ping_interval;
    }
}
