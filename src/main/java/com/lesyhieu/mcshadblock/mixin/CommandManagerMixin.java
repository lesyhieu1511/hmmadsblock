package com.lesyhieu.mcshadblock.mixin;

import com.lesyhieu.mcshadblock.MCServerHostAdBlock;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Inject(method = "executeWithPrefix", at = @At("HEAD"), cancellable = true)
    private static void blockKnownAd(ServerCommandSource source, String command, CallbackInfoReturnable<Integer> cir) {
        String normalized = command.toLowerCase(java.util.Locale.ROOT);

        if (normalized.startsWith("tellraw ") &&
                (normalized.contains("mcserverhost.com") ||
                 (normalized.contains("hosted by") && normalized.contains("free minecraft hosting")))) {
            MCServerHostAdBlock.LOGGER.info("Blocked MCServerHost promotional tellraw.");
            cir.setReturnValue(0);
        }
    }
}
