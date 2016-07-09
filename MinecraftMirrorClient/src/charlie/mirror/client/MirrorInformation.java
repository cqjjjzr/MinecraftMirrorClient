package charlie.mirror.client;

import java.net.URI;

/**
 * A class to store mirror information.
 * @author Charlie Jiang
 */
public class MirrorInformation {
    private String name;

    private URI versionMirrorPrefix = null;
    private URI librariesMirrorPrefix = null;
    private URI assetsMirrorPrefix = null;
    private URI versionOriginalPrefix = null;
    private URI librariesOriginalPrefix = null;

    public MirrorInformation(){}

    public MirrorInformation(URI versionMirrorPrefix, URI librariesMirrorPrefix, URI assetsMirrorPrefix){
        this.versionMirrorPrefix = versionMirrorPrefix;
        this.librariesMirrorPrefix = librariesMirrorPrefix;
        this.assetsMirrorPrefix = assetsMirrorPrefix;
    }

    public URI getAssetsMirrorPrefix() {
        return assetsMirrorPrefix;
    }

    public void setAssetsMirrorPrefix(URI assetsMirrorPrefix) {
        this.assetsMirrorPrefix = assetsMirrorPrefix;
    }

    public URI getVersionMirrorPrefix() {
        return versionMirrorPrefix;
    }

    public void setVersionMirrorPrefix(URI versionMirrorPrefix) {
        this.versionMirrorPrefix = versionMirrorPrefix;
    }

    public URI getLibrariesMirrorPrefix() {
        return librariesMirrorPrefix;
    }

    public void setLibrariesMirrorPrefix(URI librariesMirrorPrefix) {
        this.librariesMirrorPrefix = librariesMirrorPrefix;
    }

    public URI getLibrariesOriginalPrefix() {
        return librariesOriginalPrefix;
    }

    public void setLibrariesOriginalPrefix(URI librariesOriginalPrefix) {
        this.librariesOriginalPrefix = librariesOriginalPrefix;
    }

    public URI getVersionOriginalPrefix() {
        return versionOriginalPrefix;
    }

    public void setVersionOriginalPrefix(URI versionOriginalPrefix) {
        this.versionOriginalPrefix = versionOriginalPrefix;
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
