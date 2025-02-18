package codes.whale.realms.basic.realms.data;

import codes.whale.realms.basic.realms.PlayerRealm;
import codes.whale.realms.basic.realms.features.RealmMemberManager;
import com.google.gson.JsonObject;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.features.data.features.encoder.types.PlayerDataEncoder;
import net.aquamines.commons.api.utilities.data.JsonUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RealmDataEncoder extends PlayerDataEncoder<JsonObject, PlayerRealm> {

    public RealmDataEncoder(@NotNull PluginModule module, @NotNull String name) {
        super(module, name);
    }

    @Override
    public @NotNull PlayerRealm deserializeValue(@NotNull JsonObject object, @NotNull String key) {
        UUID identifier = JsonUtilities.getUUID(object, "identifier");
        if (identifier == null)
            identifier = UUID.fromString(key.replace(".json", ""));

        long createdAt = JsonUtilities.getLong(object, "createdAt", System.currentTimeMillis());

        JsonObject memberManagerObject = JsonUtilities.getJsonObject(object, "members");
        RealmMemberManager memberManager = RealmMemberManager.fromJsonObject(memberManagerObject);

        return new PlayerRealm(identifier, createdAt, memberManager);
    }

    @Override
    public @NotNull JsonObject serializeValue(@NotNull PlayerRealm data, @NotNull String key) {
        JsonObject object = new JsonObject();

        object.addProperty("identifier", data.getIdentifier().toString());
        object.addProperty("createdAt", data.getCreatedAt());

        RealmMemberManager memberManager = data.getMemberManager();
        JsonObject memberManagerObject = RealmMemberManager.toJsonObject(memberManager);
        object.add("members", memberManagerObject);

        return object;
    }

    @Override
    public @Nullable PlayerRealm createDefault(@NotNull UUID identifier) {
        return new PlayerRealm(identifier);
    }

}
