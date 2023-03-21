package org.pytorch.demo.objectdetection;

public class WifiData {
    private String Ssid;
    private String Bssid;
    private int SignalStrength;
    public void setBssid(String bssid) {
        this.Bssid = bssid;
    }

    public String getBssid() {
        return Bssid;
    }

    public void setSsid(String ssid) {
        this.Ssid = ssid;
    }

    public String getSsid() {
        return Ssid;
    }
    public void setSignalStrength(int signalStrength) {
        this.SignalStrength = signalStrength;
    }
    public int getSignalStrength() {
        return SignalStrength;
    }

    // constructors, getters, and setters
}

