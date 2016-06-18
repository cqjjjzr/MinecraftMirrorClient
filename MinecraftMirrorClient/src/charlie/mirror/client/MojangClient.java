package charlie.mirror.client;

import java.net.URI;

public class MojangClient {
    private MirrorList list;

    public MojangClient(MirrorList list){
        this.list = list;
    }

    public String getVersion(String offcialURL){
        for(MirrorInformation information : list){
            if(information.getVersionURL() != null){
                return MojangMirror.getVersion(information, MojangMirror.parseVersion(URI.create(offcialURL))).toString();
            }
        }
        return null;
    }

    public String getAssets(String hash){
        for(MirrorInformation information : list){
            if(information.getAssetsURL() != null){
                return MojangMirror.getAsset(information, hash).toString();
            }
        }
        return null;
    }

    public String getLibraries(String offcialURL){
        for(MirrorInformation information : list){
            if(information.getLibrariesURL() != null){
                return MojangMirror.getLibrary(information, MojangMirror.parseLibrary(URI.create(offcialURL))).toString();
            }
        }
        return null;
    }
}
