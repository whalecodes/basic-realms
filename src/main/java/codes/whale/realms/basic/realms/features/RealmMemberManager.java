package codes.whale.realms.basic.realms.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.aquamines.commons.api.utilities.data.JsonUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RealmMemberManager {

    private final Set<UUID> trusted = new HashSet<>();
    private final Set<UUID> blocked = new HashSet<>();

    public RealmMemberManager(@NotNull Collection<UUID> trusted, @NotNull Collection<UUID> blocked) {
        this.trusted.addAll(trusted);
        this.blocked.addAll(blocked);
    }

    public RealmMemberManager() {

    }

    public boolean isTrusted(@NotNull UUID identifier) {
        return trusted.contains(identifier);
    }

    public boolean isBlocked(@NotNull UUID identifier) {
        return blocked.contains(identifier);
    }

    public void setTrusted(@NotNull UUID identifier, boolean trusted) {
        if (trusted)
            this.trusted.add(identifier);
        else
            this.trusted.remove(identifier);
    }

    public void setBlocked(@NotNull UUID identifier, boolean blocked) {
        if (blocked)
            this.blocked.add(identifier);
        else
            this.blocked.remove(identifier);
    }

    public void forEachTrusted(@NotNull Consumer<UUID> consumer) {
        for (UUID uuid : trusted) {
            consumer.accept(uuid);
        }
    }

    public void forEachBlocked(@NotNull Consumer<UUID> consumer) {
        for (UUID uuid : blocked) {
            consumer.accept(uuid);
        }
    }

    public int getTrustedSize() {
        return trusted.size();
    }

    public int getBlockedSize() {
        return blocked.size();
    }

    public static @NotNull JsonObject toJsonObject(@NotNull RealmMemberManager memberManager) {
        JsonObject object = new JsonObject();

        JsonArray trustedArray = new JsonArray();
        memberManager.forEachTrusted(uuid -> trustedArray.add(uuid.toString()));

        JsonArray blockedArray = new JsonArray();
        memberManager.forEachBlocked(uuid -> blockedArray.add(uuid.toString()));

        return object;
    }

    public static @NotNull RealmMemberManager fromJsonObject(@Nullable JsonObject object) {
        if (object == null)
            return new RealmMemberManager();

        JsonArray trustedArray = JsonUtilities.getJsonArray(object, "trusted");
        JsonArray blockedArray = JsonUtilities.getJsonArray(object, "blocked");

        List<UUID> trusted = new LinkedList<>();
        List<UUID> blocked = new LinkedList<>();

        if (trustedArray != null) {
            for (int i = 0; i < trustedArray.size(); i++) {
                trusted.add(UUID.fromString(trustedArray.get(i).getAsString()));
            }
        }

        if (blockedArray != null) {
            for (int i = 0; i < blockedArray.size(); i++) {
                blocked.add(UUID.fromString(blockedArray.get(i).getAsString()));
            }
        }

        return new RealmMemberManager(trusted, blocked);
    }

}
