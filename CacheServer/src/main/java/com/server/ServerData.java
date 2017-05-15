package com.server;

public class ServerData {
    private String hostName;
    private int pingTime;
    private int port;

    ServerData(String hostName, int pingTime, int port) {
        this.hostName = hostName;
        this.pingTime = pingTime;
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPingTime() {
        return pingTime;
    }

    public void setPingTime(int pingTime) {
        this.pingTime = pingTime;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
