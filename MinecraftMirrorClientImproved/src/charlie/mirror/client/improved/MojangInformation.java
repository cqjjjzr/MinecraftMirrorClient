package charlie.mirror.client.improved;

import java.net.URI;

public class MojangInformation extends MirrorInformation {
    public MojangInformation(){
        this.setName("Mojang");
        this.setAssetsPattern("http://resources.download.minecraft.net/{0}/{1}");
        this.setLibrariesPattern("https://libraries.minecraft.net/{0}/{1}");
        this.setVersionPattern("https://launcher.mojang.com/mc/game/{0}/{1}/{2}/{3}");
    }
}
