package charlie.mirror.client;

import java.net.URI;

/**
 * A class to store mirror information.
 * @author Charlie Jiang
 */
public class MirrorInformation {
    private String name;

    private URI versionPattern = null;
    private URI librariesPattern = null;
    private URI assetsPattern = null;

    public MirrorInformation(){}

    public MirrorInformation(URI versionPattern, URI librariesPattern, URI assetsPattern){
        this.versionPattern = versionPattern;
        this.librariesPattern = librariesPattern;
        this.assetsPattern = assetsPattern;
    }

    public URI getAssetsPattern() {
        return assetsPattern;
    }

    public void setAssetsPattern(URI assetsPattern) {
        this.assetsPattern = assetsPattern;
    }

    public URI getVersionPattern() {
        return versionPattern;
    }

    public void setVersionPattern(URI versionPattern) {
        this.versionPattern = versionPattern;
    }

    public URI getLibrariesPattern() {
        return librariesPattern;
    }

    public void setLibrariesPattern(URI librariesPattern) {
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
