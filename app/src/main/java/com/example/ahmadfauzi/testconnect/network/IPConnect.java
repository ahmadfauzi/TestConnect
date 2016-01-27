package com.example.ahmadfauzi.testconnect.network;

/**
 * Created by Ahmad Fauzi on 12/14/2015.
 */
public class IPConnect {
    public int id;
    public String ip;

    public IPConnect(){

    }

    public IPConnect(String ip){
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
