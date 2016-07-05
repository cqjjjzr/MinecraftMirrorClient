package charlie.mirror.client;

import java.net.URI;

public class MojangInformation extends MirrorInformation {
    public MojangInformation(){
        this.setName("Mojang");
        this.setAssetsPattern(URI.create("http://resources.download.minecraft.net/{0}/{1}"));
        this.setLibrariesPattern(URI.create("https://libraries.minecraft.net/{0}/{1}"));
        this.setVersionPattern(URI.create("https://launcher.mojang.com/mc/game/{0}/{1}/{2}/{3}"));
    }
}
