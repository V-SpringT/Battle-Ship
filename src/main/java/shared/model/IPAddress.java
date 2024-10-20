/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.model;

import java.io.Serializable;
 
import java.io.Serializable;

public class IPAddress implements Serializable{
    private String host;
    private int port;
     
    public IPAddress() {
        super();
    }
 
    public IPAddress(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }
 
    public String getHost() {
        return host;
    }
 
    public void setHost(String host) {
        this.host = host;
    }
 
    public int getPort() {
        return port;
    }
 
    public void setPort(int port) {
        this.port = port;
    }
}

