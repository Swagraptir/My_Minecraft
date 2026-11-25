package com.myminecraft;

import com.myminecraft.animal.AnimalSpecs;
import com.myminecraft.illager.IllagerAiUpgrade;
import com.myminecraft.registry.ModBlockEntities;
import com.myminecraft.registry.ModBlocks;
import com.myminecraft.registry.ModEntities;
import com.myminecraft.registry.ModItems;
import com.myminecraft.registry.ModSpawns;
import com.myminecraft.registry.ModStructures;
import com.myminecraft.worldgen.MonumentBarnacles;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Server + common entrypoint. Order matters: blocks -> block entities -> entities -> items -> world content. */
public class MyMinecraft implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Ids.MOD_ID);

    @Override
    public void onInitialize() {
        Compat.load();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModItems.register();
        ModSpawns.register();
        ModStructures.register();
        IllagerAiUpgrade.register();
        MonumentBarnacles.register();
        LOGGER.info("My Minecraft loaded: {} furniture blocks, {} animals", ModBlocks.FURNITURE.size(), AnimalSpecs.ALL.size());
    }
}
