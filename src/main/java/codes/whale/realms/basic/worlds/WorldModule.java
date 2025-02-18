package codes.whale.realms.basic.worlds;

import codes.whale.realms.basic.worlds.auxilary.WorldsConfiguration;
import live.minehub.polarpaper.*;
import lombok.Getter;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.utilities.components.ModuleComponentRegistry;
import net.aquamines.commons.api.plugins.utilities.settings.ModuleSettings;
import net.aquamines.commons.api.utilities.data.FileUtilities;
import net.aquamines.commons.api.utilities.text.Components;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WorldModule extends PluginModule {

    @Getter private static WorldModule module;
    @Getter private final ModuleSettings settings = new ModuleSettings(this, "worlds");

    @Getter private final WorldsConfiguration configuration = new WorldsConfiguration(this, "config");

    @Getter private final Map<UUID, World> playerWorlds = new HashMap<>();
    @Getter private final Map<UUID, UUID> worldOwnerMap = new HashMap<>();

    @Override
    protected void prepare(@NotNull ModuleComponentRegistry components) {
        module = this;
        components.add(
            configuration
        );

        FileUtilities.createDirectory(getStoragePath());
    }

    private Path getStoragePath() {
        return getPath().resolve("storage");
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        long start = System.currentTimeMillis();
        Player player = event.getPlayer();
        UUID identifier = player.getUniqueId();

        Path worldPath = getStoragePath().resolve("realm-" + identifier + ".polar");

        if (!Files.exists(worldPath)) {
            createNewWorld(player);
            long end = System.currentTimeMillis();
            getLogger().log("Took " + (end - start) + "ms to create new world for player: " + player.getName());
            return;
        }

        PolarWorld polarWorld = readPlayerWorld(worldPath);
        if (polarWorld == null) {
            getLogger().error("Player world is null: " + player.getName());
            return;
        }

        String worldName = "realm-" + identifier;
        Polar.loadWorld(polarWorld, worldName);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            getLogger().error("World for player is null after loading: " + player.getName());
            return;
        }

        long end = System.currentTimeMillis();
        getLogger().log("Took " + (end - start) + "ms to load world for player: " + player.getName());
        playerWorlds.put(identifier, world);
        worldOwnerMap.put(world.getUID(), identifier);
        Components.send(player, "<yellow>Your realm has been loaded! Use <gold>/realm</gold> to go there.");
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID identifier = player.getUniqueId();
        World world = playerWorlds.get(identifier);
        if (world == null) {
            getLogger().error("World for player is null on quit: " + player.getName());
            return;
        }

        getLogger().log("Saving world " + world.getName() + "...");
        long start = System.currentTimeMillis();
        saveWorld(world).thenAccept(saved -> {
            if (!saved) {
                getLogger().error("Error trying to save world for player: " + player.getName());
                return;
            }

            Bukkit.getScheduler().runTask(getPlugin(), ()-> {
                playerWorlds.remove(identifier);
                worldOwnerMap.remove(world.getUID());
                Bukkit.unloadWorld(world, false);
                long end = System.currentTimeMillis();
                getLogger().log("Took " + (end - start) + "ms to save world for player: " + player.getName());
            });
        });
    }

    private void createNewWorld(@NotNull Player player) {
        PolarWorld templateWorld = readTemplate();
        if (templateWorld == null) {
            getLogger().error("Template world for player is null: " + player.getName());
            return;
        }

        String worldName = "realm-" + player.getUniqueId();
        Polar.loadWorld(templateWorld, worldName);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            getLogger().error("World for player is null after creation: " + player.getName());
            return;
        }

        playerWorlds.put(player.getUniqueId(), world);
        worldOwnerMap.put(world.getUID(), player.getUniqueId());
        Components.send(player, "<yellow>Your realm has been created! Use <gold>/realm</gold> to go there.");
    }

    private @Nullable PolarWorld readTemplate() {
        Path templatePath = getPath().resolve(configuration.getRealmTemplatePath());
        if (!Files.exists(templatePath)) {
            getLogger().debug("No default template for realms at: " + templatePath);
            return null;
        }

        try {
            byte[] worldData = Files.readAllBytes(templatePath);
            return PolarReader.read(worldData);
        } catch (IOException ioe) {
            getLogger().error("Error trying to read default template for realms at: " + templatePath);
            ioe.printStackTrace();
            return null;
        }
    }

    private @Nullable PolarWorld readPlayerWorld(@NotNull Path path) {
        try {
            byte[] worldData = Files.readAllBytes(path);
            return PolarReader.read(worldData);
        } catch (IOException ioe) {
            getLogger().error("Error trying to read player world at: " + path);
            ioe.printStackTrace();
            return null;
        }
    }

    private CompletableFuture<Boolean> saveWorld(@NotNull World world) {
        boolean created = FileUtilities.createDirectory(getStoragePath());
        if (!created) {
            getLogger().error("Error trying to create storage directory for worlds.");
            return CompletableFuture.completedFuture(false);
        }

        PolarWorld polarWorld = PolarWorld.fromWorld(world);
        if (polarWorld == null) {
            getLogger().error("Polar world is null for world: " + world.getName());
            return CompletableFuture.completedFuture(false);
        }
        if (!(world.getGenerator() instanceof PolarGenerator polarGenerator)) {
            getLogger().error("World generator is not PolarGenerator for world: " + world.getName());
            return CompletableFuture.completedFuture(false);
        }

        CompletableFuture<Void> future = Polar.updateWorld(world, polarWorld, polarGenerator, ChunkSelector.all(), 0, 0);
        CompletableFuture<Boolean> applied = future.thenApply(v -> {
            Path worldPath = getStoragePath().resolve(world.getName() + ".polar");
            byte[] data = PolarWriter.write(polarWorld);
            try {
                Files.write(worldPath, data);
                return true;
            } catch (IOException ioe) {
                getLogger().error("Error trying to save world at: " + worldPath);
                ioe.printStackTrace();
                return false;
            }
        });

        return applied;
    }

    @Override
    public void uponReload() {

    }

    @Override
    public void beforeEnable() {

    }

    @Override
    public void beforeDisable() {
        getLogger().log("Detected disable; manually saving player worlds...");
        getPlayerWorlds().forEach((identifier, world)-> {
            long start = System.currentTimeMillis();
            saveWorld(world).thenAccept(saved -> {
                if (!saved) {
                    getLogger().error("Error trying to save world for player: " + identifier);
                    return;
                }

                Bukkit.getScheduler().runTask(getPlugin(), ()-> {
                    playerWorlds.remove(identifier);
                    worldOwnerMap.remove(world.getUID());
                    Bukkit.unloadWorld(world, false);
                    long end = System.currentTimeMillis();
                    getLogger().log("Took " + (end - start) + "ms to save world for player: " + identifier);
                });
            });
        });
    }

}
