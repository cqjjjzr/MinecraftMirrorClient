package charlie.mirror.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A class to store mirrors.
 * @author Charlie Jiang
 */
public class MirrorList implements Iterable<MirrorInformation> {
    private List<MirrorInformation> mirrors = new LinkedList<>();

    public MirrorList(){
        mirrors.add(new MojangInformation());
    }

    /**
     * Add a mirror from a json.
     * @param json The mirror json.
     */
    public void add(String json){
        JSONObject rootObj = new JSONObject(json);
        JSONArray array = rootObj.getJSONArray("mirrors");
        MirrorInformation information = new MirrorInformation();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            switch (obj.getString("name")){
                case "versionsURL":information.setVersionURL(URI.create(obj.getString("rootURL"))); break;
                case "librariesURL":information.setLibrariesURL(URI.create(obj.getString("rootURL"))); break;
                case "assetsURL":information.setAssetsURL(URI.create(obj.getString("rootURL")));
            }
        }
        information.setName(rootObj.getString("name"));
        mirrors.add(0, information);
    }

    /**
     * Remove a mirror from the list with a name.
     * @param name The name of the mirror.
     */
    public void remove(String name){
        for (MirrorInformation mirror : mirrors) {
            if(mirror.getName().equals(name)) mirrors.remove(mirror);
        }
    }

    /**
     * Get the size of the list.
     * @return The size.
     */
    public int size(){
        return mirrors.size();
    }

    /**
     * Get a mirror from a name.
     * @param name The name.
     * @return The mirror.
     */
    public MirrorInformation get(String name){
        for (MirrorInformation mirror : mirrors) {
            if(mirror.getName().equals(name)) return mirror;
        }
        return null;
    }

    public MirrorInformation elementAt(int pos){
        return mirrors.get(pos);
    }

    @Override
    public Iterator<MirrorInformation> iterator() {
        return mirrors.iterator();
    }
}
