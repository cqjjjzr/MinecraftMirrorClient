package charlie.mirror.client;

import java.net.URI;

public class MirrorInformation {
    private String name;

    private URI versionURL = null;
    private URI librariesURL = null;
    private URI assetsURL = null;

    public MirrorInformation(){}

    public MirrorInformation(URI versionURL, URI librariesURL, URI assetsURL){
        this.versionURL = versionURL;
        this.librariesURL = librariesURL;
        this.assetsURL = assetsURL;
    }

    public URI getAssetsURL() {
        return assetsURL;
    }

    public void setAssetsURL(URI assetsURL) {
        this.assetsURL = assetsURL;
    }

    public URI getVersionURL() {
        return versionURL;
    }

    public void setVersionURL(URI versionURL) {
        this.versionURL = versionURL;
    }

    public URI getLibrariesURL() {
        return librariesURL;
    }

    public void setLibrariesURL(URI librariesURL) {
        this.librariesURL = librariesURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
