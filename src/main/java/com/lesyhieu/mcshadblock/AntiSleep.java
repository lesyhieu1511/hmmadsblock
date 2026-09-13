package com.lesyhieu.mcshadblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Freezes the vanilla server tick loop while nobody is online and unfreezes it
 * as soon as a player joins. This is equivalent to using /tick freeze and
 * /tick unfreeze, but is handled directly through the server tick manager.
 */
public final class AntiSleep {
    private static boolean enabled;

    private AntiSleep() {
    }

    public static void register() {
        enabled = Boolean.parseBoolean(
                System.getProperty("hmmadsblock.antisleep", "true"));

        if (!enabled) {
            MCServerHostAdBlock.LOGGER.info(
                    "AntiSleep disabled via -Dhmmadsblock.antisleep=false");
            return;
        }

        ServerLifecycleEvents.SERVER_STARTED.register(AntiSleep::onServerStarted);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> unfreeze(server));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (server.getPlayerList().getPlayers().isEmpty()) {
                freeze(server);
            }
        });

        MCServerHostAdBlock.LOGGER.info(
                "AntiSleep enabled: freeze server ticks when no players are online.");
    }

    private static void onServerStarted(MinecraftServer server) {
        if (server.getPlayerList().getPlayers().isEmpty()) {
            freeze(server);
        }
    }

    private static void freeze(MinecraftServer server) {
        if (!server.tickManager().isFrozen()) {
            server.tickManager().setFrozen(true);
            MCServerHostAdBlock.LOGGER.info(
                    "AntiSleep: no players online, server ticks frozen.");
        }
    }

    private static void unfreeze(MinecraftServer server) {
        if (server.tickManager().isFrozen()) {
            server.tickManager().setFrozen(false);
            MCServerHostAdBlock.LOGGER.info(
                    "AntiSleep: player joined, server ticks unfrozen.");
        }
    }
}
