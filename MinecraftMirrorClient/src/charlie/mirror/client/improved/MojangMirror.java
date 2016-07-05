package charlie.mirror.client.improved;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

/**
 * A tool class to handle mojang stuff.
 * @author Charlie Jiang
 */
public class MojangMirror {
    public static String getVersion(MirrorInformation information, String originalURL){
        return information.getVersionMirrorPrefix().resolve(information.getVersionOriginalPrefix().relativize(URI.create(originalURL))).toString();
    }

    public static String getLibrary(MirrorInformation information, String originalURL){
        return information.getLibrariesMirrorPrefix().resolve(information.getLibrariesOriginalPrefix().relativize(URI.create(originalURL))).toString();
    }


    public static String getAsset(MirrorInformation information, String assetHash){
        return information.getAssetsMirrorPrefix().resolve(assetHash.substring(0, 2) + "/") .resolve(assetHash).toString();
    }

    public static void main(String[] args) {
        String json = "{\"version\":1, \n" +
                "\t\"name\":\"BMCLAPI\",\n" +
                "\t\"mirrors\":[\n" +
                "\t\t{\"name\":\"assetsURL\", \"mirrorPrefix\":\"http://bmclapi2.bangbang93.com/objects/\"},\n" +
                "\t\t{\"name\":\"librariesURL\", \"officialPrefix\":\"https://libraries.minecraft.net/\", \"mirrorPrefix\":\"http://bmclapi2.bangbang93.com/libraries/\"},\n" +
                "\t\t{\"name\":\"versionsURL\", \"officialPrefix\":\"https://launcher.mojang.com/mc/game/\", \"mirrorPrefix\":\"http://bmclapi2.bangbang93.com/mc/game/\"}\n" +
                "\t]\n" +
                "}";
        JSONObject rootObj = new JSONObject(json);
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
        System.out.println(getAsset(information, "48f7e1bb098abd36b9760cca27b9d4391a23de26"));
        System.out.println(getLibrary(information, "https://libraries.minecraft.net/com/mojang/netty/1.6/netty-1.6.jar"));
        System.out.println(getVersion(information, "https://launcher.mojang.com/mc/game/1.7.10/client/e80d9b3bf5085002218d4be59e668bac718abbc6/client.jar"));
    }
}
