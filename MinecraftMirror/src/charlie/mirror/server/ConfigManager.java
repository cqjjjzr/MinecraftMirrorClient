package charlie.mirror.server;

import java.util.prefs.Preferences;

public class ConfigManager {
    Preferences preferences;
    public void init(){
        preferences = Preferences.userNodeForPackage(this.getClass());
    }

    public String getHttpRoot(){
        return preferences.get("HttpRoot", "D:\\httproot");
    }
}
