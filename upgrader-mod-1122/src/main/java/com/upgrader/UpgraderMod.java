package com.upgrader;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = UpgraderMod.MODID, name = "Апгрейдер", version = "1.0.0", acceptedMinecraftVersions = "[1.12.2]")
@Mod.EventBusSubscriber
public class UpgraderMod {
    public static final String MODID = "upgrader";

    public static final Block UPGRADER_BLOCK = new UpgraderBlock();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(UPGRADER_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(UPGRADER_BLOCK).setRegistryName(UPGRADER_BLOCK.getRegistryName()));
    }
}
