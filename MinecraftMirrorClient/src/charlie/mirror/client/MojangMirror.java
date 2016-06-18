package charlie.mirror.client;

import java.net.URI;

public class MojangMirror {
    private static final URI VERSION_ROOT = URI.create("https://launcher.mojang.com/mc/game/");
    private static final URI LIBRARIES_ROOT = URI.create("https://libraries.minecraft.net/");

    public static URI getVersion(MirrorInformation information, String versionEndpoint){
        return information.getVersionURL().resolve(versionEndpoint);
    }

    public static URI getLibrary(MirrorInformation information, String libEndpoint){
        return information.getLibrariesURL().resolve(libEndpoint);
    }

    public static URI getAsset(MirrorInformation information, String assetHash){
        return information.getAssetsURL().resolve(assetHash.substring(0, 2) + "/").resolve(assetHash);
    }

    public static String parseVersion(URI officialURI){
        return VERSION_ROOT.relativize(officialURI).toString();
    }

    public static String parseLibrary(URI officialURI){
        return LIBRARIES_ROOT.relativize(officialURI).toString();
    }
}
