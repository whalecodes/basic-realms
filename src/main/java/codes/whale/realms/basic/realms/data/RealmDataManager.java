package codes.whale.realms.basic.realms.data;

import codes.whale.realms.basic.realms.PlayerRealm;
import com.google.gson.JsonObject;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.features.data.DataStorage;
import net.aquamines.commons.api.plugins.features.data.utilities.typed.PlayerDataManager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RealmDataManager extends PlayerDataManager<JsonObject, PlayerRealm> {

    public RealmDataManager(@NotNull PluginModule module, @NotNull String identifier, @NotNull DataStorage<JsonObject, UUID, PlayerRealm> storage) {
        super(module, identifier, storage);
    }

}
