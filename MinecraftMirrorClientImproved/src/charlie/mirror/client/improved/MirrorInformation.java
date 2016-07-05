package charlie.mirror.client.improved;

/**
 * A class to store mirror information.
 * @author Charlie Jiang
 */
public class MirrorInformation {
    private String name;

    private String versionPattern = null;
    private String librariesPattern = null;
    private String assetsPattern = null;

    public MirrorInformation(){}

    public MirrorInformation(String versionPattern, String librariesPattern, String assetsPattern){
        this.versionPattern = versionPattern;
        this.librariesPattern = librariesPattern;
        this.assetsPattern = assetsPattern;
    }

    public String getAssetsPattern() {
        return assetsPattern;
    }

    public void setAssetsPattern(String assetsPattern) {
        this.assetsPattern = assetsPattern;
    }

    public String getVersionPattern() {
        return versionPattern;
    }

    public void setVersionPattern(String versionPattern) {
        this.versionPattern = versionPattern;
    }

    public String getLibrariesPattern() {
        return librariesPattern;
    }

    public void setLibrariesPattern(String librariesPattern) {
        this.librariesPattern = librariesPattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
