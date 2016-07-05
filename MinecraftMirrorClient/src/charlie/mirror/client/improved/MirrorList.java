package charlie.mirror.client.improved;

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
        if(rootObj.getInt("version") != 1) throw new IllegalArgumentException("Bad version:" + rootObj.getInt("version"));
        JSONArray array = rootObj.getJSONArray("mirrors");
        MirrorInformation information = new MirrorInformation();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            switch (obj.getString("name")){
                case "versionsURL":information.setVersionMirrorPrefix(URI.create(obj.getString("mirrorPrefix")));
                    information.setVersionOriginalPrefix(URI.create(obj.getString("officialPrefix"))); break;
                case "librariesURL":information.setLibrariesMirrorPrefix(URI.create(obj.getString("mirrorPrefix")));
                    information.setLibrariesOriginalPrefix(URI.create(obj.getString("officialPrefix"))); break;
                case "assetsURL":information.setAssetsMirrorPrefix(URI.create(obj.getString("mirrorPrefix")));
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
        mirrors.stream().filter(mirror -> mirror.getName().equals(name)).forEach(mirror -> mirrors.remove(mirror));
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
