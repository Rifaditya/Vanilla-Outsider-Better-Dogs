// Verified against: BetterDogsCommand.java (26.1.2+)
package net.vanillaoutsider.betterdogs.command;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Collection;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.util.WolfCommandHelper;

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
                .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                .then(Commands.literal("debug")
                        .then(Commands.literal("personality")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .executes(context -> {
                                                    if (!isDebugEnabled(context.getSource())) return 0;
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
                                                    if (!isDebugEnabled(context.getSource())) return 0;
                                                    Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                    String actionType = StringArgumentType.getString(context, "actionType");
                                                    return WolfCommandHelper.executeAction(context.getSource(), targets, actionType, null);
                                                })
                                                .then(Commands.argument("secondaryTarget", EntityArgument.entity())
                                                        .executes(context -> {
                                                            if (!isDebugEnabled(context.getSource())) return 0;
                                                            Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                            String actionType = StringArgumentType.getString(context, "actionType");
                                                            Entity secondaryTarget = EntityArgument.getEntity(context, "secondaryTarget");
                                                            return WolfCommandHelper.executeAction(context.getSource(), targets, actionType, secondaryTarget);
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("territory")
                                .executes(context -> {
                                    if (!isDebugEnabled(context.getSource())) return 0;
                                    return WolfCommandHelper.spawnTerritoryScenario(context.getSource());
                                })
                        )
                )
        );
    }

    private static boolean isDebugEnabled(CommandSourceStack source) {
        if (!DynamicGameRuleManager.getBoolean(source.getLevel(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_DEBUGGING)) {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Better Dogs debugging is currently disabled. Enable it via GameRules: /gamerule vanilla-outsider-better-dogs:bd_debugging true"));
            return false;
        }
        return true;
    }
}
