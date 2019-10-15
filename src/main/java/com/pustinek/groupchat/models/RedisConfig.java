package com.pustinek.groupchat.models;

public class RedisConfig {
    private String ip;
    private int port;
    private String username;
    private String password;
    private String server;
    private String messageChannel;

    public RedisConfig(String ip, int port, String username, String password, String server, String messageChannel) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.server = server;
        this.messageChannel = messageChannel;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMessageChannel() {
        return messageChannel;
    }

    public String getServer() {
        return server;
    }
}
