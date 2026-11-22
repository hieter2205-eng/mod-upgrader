package com.upgrader;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = UpgraderMod.MODID, value = Side.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(UpgraderMod.UPGRADER_BLOCK), 0,
                new ModelResourceLocation(UpgraderMod.UPGRADER_BLOCK.getRegistryName(), "inventory"));
    }
}
