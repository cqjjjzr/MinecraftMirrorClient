package charlie.mirror.server;

import java.util.prefs.Preferences;

public class ConfigManager {
    private Preferences preferences;
    public void init(){
        preferences = Preferences.userNodeForPackage(this.getClass());
    }

    public String getHttpRoot(){
        return preferences.get("HttpRoot", "D:\\httproot");
    }

    public int getReadTimeout(){
        return preferences.getInt("ReadTimeOut", 1000000);
    }

    public int getConnectTimeout(){
        return preferences.getInt("ConnectTimeOut", 1000000);
    }

    public String getUserAgent(){
        return preferences.get("UserAgent", "Minecraft Mirror " + MinecraftMirror.VERSION);
    }

    public int getMaxQueue(){
        return preferences.getInt("MaxQueue", 50);
    }

    public int getIntervalHour(){
        return preferences.getInt("IntervalHour", 1);
    }

    public int getRemoteNamingPort(){
        return preferences.getInt("RemoteNamingPort", 20100);
    }

    public int getRemoteDataPort(){
        return preferences.getInt("RemoteDataPort", 20200);
    }
}
