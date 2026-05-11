package net.vanillaoutsider.betterdogs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.util.WolfCommandHelper;

import java.util.Collection;

/**
 * Brigadier command tree for /betterdogs debug logic.
 * Enforces permissions and argument parsing.
 */
public class BetterDogsCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("betterdogs")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("debug")
                        .then(Commands.literal("personality")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .executes(context -> {
                                                    Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                    String type = StringArgumentType.getString(context, "type");
                                                    return WolfCommandHelper.setPersonality(context.getSource(), targets, type);
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("action")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("actionType", StringArgumentType.word())
                                                .executes(context -> {
                                                    Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                    String actionType = StringArgumentType.getString(context, "actionType");
                                                    return WolfCommandHelper.executeAction(context.getSource(), targets, actionType);
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("territory")
                                .executes(context -> WolfCommandHelper.spawnTerritoryScenario(context.getSource()))
                        )
                )
        );
    }
}
