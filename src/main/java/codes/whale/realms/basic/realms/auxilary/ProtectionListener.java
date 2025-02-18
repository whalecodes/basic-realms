package codes.whale.realms.basic.realms.auxilary;

import codes.whale.realms.basic.realms.PlayerRealm;
import codes.whale.realms.basic.realms.RealmModule;
import codes.whale.realms.basic.realms.features.RealmMemberManager;
import codes.whale.realms.basic.worlds.WorldModule;
import lombok.Getter;
import net.aquamines.commons.api.utilities.text.Components;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ProtectionListener implements Listener {

    @Getter private final RealmModule module;

    public ProtectionListener(@NotNull RealmModule module) {
        this.module = module;
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        handleBlockEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        handleBlockEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        handleBlockEvent(event.getPlayer(), block, event);
    }

    private <T extends BlockEvent & Cancellable> void handleBlockEvent(@NotNull Player player, @NotNull T event) {
        handleBlockEvent(player, event.getBlock(), event);
    }

    private void handleBlockEvent(@NotNull Player player, @NotNull Block block, @NotNull Cancellable event) {
        WorldModule worldModule = WorldModule.getModule();

        World world = block.getWorld();

        UUID realmOwner = worldModule.getWorldOwnerMap().get(world.getUID());
        if (realmOwner == null)
            return; // non-realm world

        if (realmOwner.equals(player.getUniqueId()))
            return; // owner can place blocks

        PlayerRealm realmData = module.getDataManager().get(realmOwner);
        if (realmData == null || !realmData.getMemberManager().isTrusted(player.getUniqueId()))
            cancelEvent(player, event);
    }

    private void cancelEvent(@NotNull Player player, @NotNull Cancellable event) {
        Components.send(player, "<red>You do not have access to this realm!");
        event.setCancelled(true);
    }

}
