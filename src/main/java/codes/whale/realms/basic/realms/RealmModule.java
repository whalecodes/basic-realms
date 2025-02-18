package codes.whale.realms.basic.realms;

import codes.whale.realms.basic.realms.auxilary.ProtectionListener;
import codes.whale.realms.basic.realms.auxilary.RealmCommand;
import codes.whale.realms.basic.realms.data.RealmDataEncoder;
import codes.whale.realms.basic.realms.data.RealmDataManager;
import lombok.Getter;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.features.data.DataManager;
import net.aquamines.commons.api.plugins.utilities.components.ModuleComponentRegistry;
import net.aquamines.commons.api.plugins.utilities.settings.ModuleSettings;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class RealmModule extends PluginModule {

    @Getter private static RealmModule module;
    @Getter private final ModuleSettings settings = new ModuleSettings(this, "realms");

    @Getter private final RealmDataManager dataManager = DataManager.builder(this)
            .regularJsonAdapter()
            .encoder(RealmDataEncoder::new)
            .cold(RealmDataManager::new);

    @Override
    protected void prepare(@NotNull ModuleComponentRegistry components) {
        module = this;
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(this), getPlugin());

        components.add(
                dataManager,
                new RealmCommand(this, "realm")
        );
    }

    @Override
    public void uponReload() {

    }

    @Override
    public void beforeEnable() {

    }

    @Override
    public void beforeDisable() {

    }

}
