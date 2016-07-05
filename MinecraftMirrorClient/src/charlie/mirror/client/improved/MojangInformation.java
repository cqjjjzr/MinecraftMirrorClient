package charlie.mirror.client.improved;

import java.net.URI;

public class MojangInformation extends MirrorInformation {
    public MojangInformation(){
        this.setName("Mojang");
        this.setAssetsMirrorPrefix(URI.create("http://resources.download.minecraft.net/"));
        this.setLibrariesMirrorPrefix(URI.create("https://libraries.minecraft.net/"));
        this.setVersionMirrorPrefix(URI.create("https://launcher.mojang.com/mc/game/"));
        this.setLibrariesOriginalPrefix(URI.create("https://libraries.minecraft.net/"));
        this.setVersionOriginalPrefix(URI.create("https://launcher.mojang.com/mc/game/"));
    }
}
