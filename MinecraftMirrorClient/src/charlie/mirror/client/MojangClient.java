package charlie.mirror.client;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class MojangClient {
    private MirrorList list;
    private List<String> errorURL = new LinkedList<>();
    private List<String> errorMirror = new LinkedList<>();

    public MojangClient(MirrorList list){
        this.list = list;
    }

    public String getVersion(String offcialURL){
        for(MirrorInformation information : list){
            if(information.getVersionURL() != null && errorMirror.contains(information.getName())){
                String url = MojangMirror.getVersion(information, MojangMirror.parseVersion(URI.create(offcialURL))).toString();
                if(errorURL.contains(url)) continue;
                return url;
            }
        }
        return null;
    }

    public String getAssets(String hash){
        for(MirrorInformation information : list){
            if(information.getAssetsURL() != null && errorMirror.contains(information.getName())){
                String url = MojangMirror.getAsset(information, hash).toString();
                if(errorURL.contains(url)) continue;
                return url;
            }
        }
        return null;
    }

    public String getLibraries(String offcialURL){
        for(MirrorInformation information : list){
            if(information.getLibrariesURL() != null && errorMirror.contains(information.getName())){
                String url = MojangMirror.getLibrary(information, MojangMirror.parseLibrary(URI.create(offcialURL))).toString();
                if(errorURL.contains(url)) continue;
                return url;
            }
        }
        return null;
    }

    public void addErrorURL(String url){
        if(!errorURL.contains(url))
            errorURL.add(url);
    }

    public void addErrorMirror(String mirror){
        if(!errorMirror.contains(mirror))
            errorMirror.add(mirror);
    }
}
