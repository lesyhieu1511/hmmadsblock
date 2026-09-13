package com.lesyhieu.mcshadblock;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCServerHostAdBlock implements ModInitializer {
    public static final String MOD_ID = "hmmadsblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("HmmAdBlock initialized.");
        AntiSleep.register();
    }
}
