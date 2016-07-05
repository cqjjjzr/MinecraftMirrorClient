package charlie.mirror.client;

import java.net.URI;
import java.text.MessageFormat;

/**
 * A tool class to handle mojang stuff.
 * @author Charlie Jiang
 */
public class MojangMirror {
    private static final URI VERSION_ROOT = URI.create("https://launcher.mojang.com/mc/game/");
    private static final URI LIBRARIES_ROOT = URI.create("https://libraries.minecraft.net/");

    /**
     * Parse official library URL.
     * @param officialURI The official URL.
     * @return Array index 0: Library path. Index 1: Library filename.
     */
    public static String[] parseLibrary(URI officialURI){
        URI endpoint = LIBRARIES_ROOT.relativize(officialURI);
        String[] fragments = endpoint.getPath().split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fragments.length - 1; i++) {
            builder.append(fragments[i]);
            builder.append('/');
        }
        return new String[]{builder.substring(0, builder.length() - 1), fragments[fragments.length - 1]};
    }

    /**
     * Parse official version URL.
     * @param officialURI The official URL.
     * @return Array index 0: Version. Index 1: Category. Index 2:SHA-1, Index 3: Filename.
     */
    public static String[] parseVersion(URI officialURI){
        URI endpoint = LIBRARIES_ROOT.relativize(officialURI);
        String[] fragments = endpoint.getPath().substring(1).split("/");
        return new String[]{fragments[0], fragments[1], fragments[2], fragments[3]};
    }

    public static String filter(String mirrorPattern, String[] inf){
        return MessageFormat.format(mirrorPattern, (Object[]) inf);
    }

    public static String getAsset(String assetsPattern, String assetHash){
        return MessageFormat.format(assetsPattern, assetHash.substring(0, 2), assetHash);
    }

    public static void main(String[] args) {
        System.out.println(filter("http://bmclapi2.bangbang93.com/version/{0}/{1}", parseVersion(URI.create("https://launcher.mojang.com/mc/game/1.7.10/client/e80d9b3bf5085002218d4be59e668bac718abbc6/client.jar"))));
        //System.out.println(Arrays.asList(parseVersion(URI.create("https://launcher.mojang.com/mc/game/1.7.10/client/e80d9b3bf5085002218d4be59e668bac718abbc6/client.jar"))));
        System.out.println(filter("http://bmclapi2.bangbang93.com/libraries/{0}/{1}", parseLibrary(URI.create("https://libraries.minecraft.net/com/mojang/netty/1.6/netty-1.6.jar"))));
        System.out.println(getAsset("http://bmclapi2.bangbang93.com/objects/{0}/{1}", "48f7e1bb098abd36b9760cca27b9d4391a23de26"));
    }
}
