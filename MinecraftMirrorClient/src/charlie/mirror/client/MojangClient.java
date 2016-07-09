package charlie.mirror.client;

import java.util.LinkedList;
import java.util.List;

/**
 * A class to let launcher parse offcial urls of Mojang.
 */
public class MojangClient {
    private MirrorList list;
    private List<String> errorURL = new LinkedList<>();
    private List<String> errorMirror = new LinkedList<>();

    public MojangClient(MirrorList list){
        this.list = list;
    }

    /**
     * Get a mirror URL from a offcial URL.
     * @param offcialURL The offcial URL.
     * @return The mirror URL.
     */
    public String getVersion(String offcialURL){
        for(MirrorInformation information : list){
            if(information.getVersionMirrorPrefix() != null && !errorMirror.contains(information.getName())){
                String url = MojangMirror.getVersion(information, offcialURL);
                if(errorURL.contains(url)) continue;
                return url;
            }
        }
        return null;
    }

    /**
     * Get a mirror URL from a asset hash.
     * @param hash The asset hash.
     * @return The mirror URL.
     */
    public String getAssets(String hash){
        for(MirrorInformation information : list){
            if(information.getAssetsMirrorPrefix() != null && !errorMirror.contains(information.getName())){
                String url = MojangMirror.getAsset(information, hash);
                if(errorURL.contains(url)) continue;
                return url;
            }
        }
        return null;
    }

    /**
     * Get a mirror library URL from a offcial URL.
     * @param officialURL The offcial URL.
     * @return The mirror URL.
     */
    public String getLibraries(String officialURL){
        for(MirrorInformation information : list){
            if(information.getLibrariesMirrorPrefix() != null && !errorMirror.contains(information.getName())){
                String url = MojangMirror.getLibrary(information, officialURL);
                if(errorURL.contains(url)) continue;
                return url;
            }
        }
        return null;
    }

    /**
     * Tag a URL as a error URL.
     * @param url The error URL.
     */
    public void addErrorURL(String url){
        if(!errorURL.contains(url))
            errorURL.add(url);
    }

    /**
     * Tag a mirror as a error mirror.
     * @param mirror The name of the URL.
     */
    public void addErrorMirror(String mirror){
        if(!errorMirror.contains(mirror))
            errorMirror.add(mirror);
    }
}
