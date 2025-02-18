package codes.whale.realms.basic.realms;

import codes.whale.realms.basic.realms.features.RealmMemberManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerRealm {

    @Getter private final UUID identifier;
    @Getter private final long createdAt;
    @Getter private final RealmMemberManager memberManager;

    public PlayerRealm(@NotNull UUID identifier) {
        this.identifier = identifier;
        this.createdAt = System.currentTimeMillis();
        this.memberManager = new RealmMemberManager();
    }

    public PlayerRealm(@NotNull UUID identifier, long createdAt, @NotNull RealmMemberManager memberManager) {
        this.identifier = identifier;
        this.createdAt = createdAt;
        this.memberManager = memberManager;
    }

}
