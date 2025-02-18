package codes.whale.realms.basic.worlds.auxilary;

import lombok.Getter;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.features.auxilary.configs.AquaConfiguration;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldsConfiguration extends AquaConfiguration {

    @Getter private String realmTemplatePath = "template.polar";
    @Getter private String spawnLocationString = "0,0,0,0,0";

    public WorldsConfiguration(@NotNull PluginModule module, @NotNull String name) {
        super(module, name);
    }

    @Override
    protected void read() {
        realmTemplatePath = expectString("realm-template-path", realmTemplatePath);
        spawnLocationString = expectString("spawn-location", spawnLocationString);
    }

    public @NotNull Location getSpawnLocation(@NotNull World world) {
        String[] split = spawnLocationString.split(",");
        if (split.length != 5)
            return world.getSpawnLocation();

        try {
            return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
        } catch (Exception e) {
            return world.getSpawnLocation();
        }
    }

}
