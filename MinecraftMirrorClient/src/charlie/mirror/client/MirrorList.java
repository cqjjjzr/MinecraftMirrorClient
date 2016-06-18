package charlie.mirror.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MirrorList implements Iterable<MirrorInformation> {
    private List<MirrorInformation> mirrors = new LinkedList<>();

    public MirrorList(){
        mirrors.add(new MojangInformation());
    }

    public void add(String json){
        JSONObject rootObj = new JSONObject(json);
        JSONArray array = rootObj.getJSONArray("mirrors");
        MirrorInformation information = new MirrorInformation();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            switch (obj.getString("name")){
                case "versionURL":information.setVersionURL(URI.create(obj.getString("rootURL"))); break;
                case "librariesURL":information.setLibrariesURL(URI.create(obj.getString("rootURL"))); break;
                case "assetsURL":information.setAssetsURL(URI.create(obj.getString("rootURL")));
            }
        }
        information.setName(rootObj.getString("name"));
        mirrors.add(0, information);
    }

    public void remove(String name){
        for (MirrorInformation mirror : mirrors) {
            if(mirror.getName().equals(name)) mirrors.remove(mirror);
        }
    }

    public MirrorInformation get(String name){
        for (MirrorInformation mirror : mirrors) {
            if(mirror.getName().equals(name)) return mirror;
        }
        return null;
    }

    @Override
    public Iterator<MirrorInformation> iterator() {
        return mirrors.iterator();
    }
}
