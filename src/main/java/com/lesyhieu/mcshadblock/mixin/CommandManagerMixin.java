package com.lesyhieu.mcshadblock.mixin;

import com.lesyhieu.mcshadblock.MCServerHostAdBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandManagerMixin {
    @Inject(method = "performPrefixedCommand", at = @At("HEAD"), cancellable = true)
    private void blockKnownAd(CommandSourceStack source, String command, CallbackInfo ci) {
        String normalized = command.toLowerCase(java.util.Locale.ROOT);

        if (normalized.startsWith("tellraw ") &&
                (normalized.contains("mcserverhost.com") ||
                 (normalized.contains("hosted by") && normalized.contains("free minecraft hosting")))) {
            MCServerHostAdBlock.LOGGER.info("Blocked MCServerHost promotional tellraw.");
            ci.cancel();
        }
    }
}
