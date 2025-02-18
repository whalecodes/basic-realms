package codes.whale.realms.basic;

import codes.whale.realms.basic.realms.RealmModule;
import codes.whale.realms.basic.worlds.WorldModule;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.aquamines.commons.api.plugins.AquaCommonsBootstrapper;
import net.aquamines.commons.api.plugins.AquaPlugin;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.utilities.settings.PluginSettings;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class RealmsPlugin extends AquaPlugin {

    @Getter private final PluginSettings settings = new PluginSettings(this, "realms", "realmsplugin");

    @Getter private final AquaCommonsBootstrapper bootstrapper = new AquaCommonsBootstrapper(this, "aqcommons");

    @Getter private final RealmModule realmModule = new RealmModule();
    @Getter private final WorldModule worldModule = new WorldModule();

    @Getter private final List<PluginModule> modules = ImmutableList.of(
            realmModule,
            worldModule
    );

    @Override
    public void uponEnable() {
        bootstrapper.ready();
    }

    @Override
    public void uponDisable() {

    }

    @Override
    public void uponReload() {

    }

}
