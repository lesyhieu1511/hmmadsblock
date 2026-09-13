package com.lesyhieu.mcshadblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Lightweight heartbeat intended to keep hosting wrappers that watch server
 * activity from treating an otherwise healthy Minecraft process as idle.
 *
 * This does not and cannot prevent an external process supervisor from
 * suspending the JVM based solely on player count or host-side policy.
 */
public final class AntiSleep {
    private static final int DEFAULT_INTERVAL_SECONDS = 60;
    private static final int TICKS_PER_SECOND = 20;

    private static long ticks;
    private static long intervalTicks;
    private static boolean enabled;

    private AntiSleep() {
    }

    public static void register() {
        enabled = Boolean.parseBoolean(System.getProperty("hmmadsblock.antisleep", "true"));
        int seconds = getIntervalSeconds();
        intervalTicks = Math.max(TICKS_PER_SECOND, (long) seconds * TICKS_PER_SECOND);

        if (!enabled) {
            MCServerHostAdBlock.LOGGER.info("AntiSleep disabled via -Dhmmadsblock.antisleep=false");
            return;
        }

        ServerTickEvents.END_SERVER_TICK.register(AntiSleep::onServerTick);
        MCServerHostAdBlock.LOGGER.info("AntiSleep enabled (heartbeat every {}s).", seconds);
    }

    private static void onServerTick(MinecraftServer server) {
        ticks++;
        if (ticks < intervalTicks) {
            return;
        }

        ticks = 0;
        int players = server.getPlayerManager().getPlayerList().size();
        MCServerHostAdBlock.LOGGER.info("AntiSleep heartbeat: server active ({} player(s)).", players);
    }

    private static int getIntervalSeconds() {
        try {
            return Math.max(5, Integer.parseInt(
                    System.getProperty("hmmadsblock.antisleep.interval", String.valueOf(DEFAULT_INTERVAL_SECONDS))));
        } catch (NumberFormatException ignored) {
            return DEFAULT_INTERVAL_SECONDS;
        }
    }
}
