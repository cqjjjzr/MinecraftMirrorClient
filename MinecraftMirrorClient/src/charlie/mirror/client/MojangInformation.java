package charlie.mirror.client;

import java.net.URI;

public class MojangInformation extends MirrorInformation {
    public MojangInformation(){
        this.setName("Mojang");
        this.setAssetsURL(URI.create("https://assets.minecraft.net/"));
        this.setLibrariesURL(URI.create("https://libraries.minecraft.net/"));
        this.setVersionURL(URI.create("https://launcher.mojang.com/mc/game/"));
    }
}
