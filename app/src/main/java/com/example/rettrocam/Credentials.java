package com.example.rettrocam;

public class Credentials {
    String ipaddress, username,password;

    public Credentials(String ipaddress, String username, String password) {
        this.ipaddress = ipaddress;
        this.username = username;
        this.password = password;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
