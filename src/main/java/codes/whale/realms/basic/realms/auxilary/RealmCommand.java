package codes.whale.realms.basic.realms.auxilary;

import codes.whale.realms.basic.realms.PlayerRealm;
import codes.whale.realms.basic.realms.RealmModule;
import codes.whale.realms.basic.worlds.WorldModule;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.aquamines.commons.api.features.tokens.defaults.CommonLanguage;
import net.aquamines.commons.api.plugins.PluginModule;
import net.aquamines.commons.api.plugins.features.auxilary.commands.AquaCommand;
import net.aquamines.commons.api.toolkits.labels.Label;
import net.aquamines.commons.api.utilities.text.Components;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RealmCommand extends AquaCommand {

    public RealmCommand(@NotNull RealmModule module, @NotNull String identifier) {
        super(module, identifier);
        addPlayerExecutor((player, arguments)-> {
            WorldModule worldModule = WorldModule.getModule();
            World world = worldModule.getPlayerWorlds().get(player.getUniqueId());
            if (world == null) {
                Components.send(player, "<red>Your world has not been loaded yet; please wait.");
                return;
            }

            Location spawnLocation = worldModule.getConfiguration().getSpawnLocation(world);
            player.teleport(spawnLocation);
            Label label = new Label("<yellow>Teleporting you to your realm...");
            Components.send(player, label);
        });
        addSubCommand("trust", trustCommand -> {
            trustCommand
                    .addArguments(new PlayerArgument("player"))
                    .addPlayerExecutor((player, arguments)-> {
                        Player target = (Player) arguments.get("player");
                        if (target == null) {
                            Components.send(player, CommonLanguage.ERROR_WITH_ARGUMENT, "player");
                            return;
                        }

                        PlayerRealm realm = module.getDataManager().get(player.getUniqueId());
                        if (realm == null) {
                            Components.send(player, CommonLanguage.DATA_NOT_FOUND);
                            return;
                        }

                        realm.getMemberManager().setTrusted(target.getUniqueId(), true);
                        Label label = new Label("<yellow>You gave <gold>%player%</gold> access to your plot.");
                        Components.send(player, label, target);
                    });
        });
        addSubCommand("untrust", untrustCommand -> {
            untrustCommand
                    .addArguments(new PlayerArgument("player"))
                    .addPlayerExecutor((player, arguments)-> {
                        Player target = (Player) arguments.get("player");
                        if (target == null) {
                            Components.send(player, CommonLanguage.ERROR_WITH_ARGUMENT, "player");
                            return;
                        }

                        PlayerRealm realm = module.getDataManager().get(player.getUniqueId());
                        if (realm == null) {
                            Components.send(player, CommonLanguage.DATA_NOT_FOUND);
                            return;
                        }

                        realm.getMemberManager().setTrusted(target.getUniqueId(), false);
                        Label label = new Label("<yellow>You removed <gold>%player%</gold> access to your plot.");
                        Components.send(player, label, target);
                    });
        });
        addSubCommand("visit", visitCommand -> {
            visitCommand
                    .addArguments(new PlayerArgument("player"))
                    .addPlayerExecutor((player, arguments)-> {
                        Player target = (Player) arguments.get("player");
                        if (target == null) {
                            Components.send(player, CommonLanguage.ERROR_WITH_ARGUMENT, "player");
                            return;
                        }

                        if (!target.isOnline()) {
                            Components.send(player, CommonLanguage.ERROR_WITH_ARGUMENT, "player (offline)");
                            return;
                        }

                        if (target.equals(player)) {
                            Components.send(player, CommonLanguage.ERROR_WITH_ARGUMENT, "player (yourself)");
                            return;
                        }

                        WorldModule worldModule = WorldModule.getModule();
                        World world = worldModule.getPlayerWorlds().get(target.getUniqueId());
                        if (world == null) {
                            Components.send(player, "<red>Player world has not been loaded yet; please wait.");
                            return;
                        }

                        Location spawnLocation = worldModule.getConfiguration().getSpawnLocation(world);
                        player.teleport(spawnLocation);
                        Label label = new Label("<yellow>You teleported to <gold>%player%</gold>'s plot.");
                        Components.send(player, label, target);

                        Label notifyLabel = new Label("<yellow>%player%</yellow> teleported to your plot.");
                        Components.send(target, notifyLabel, player);
                    });
        });
    }

}
