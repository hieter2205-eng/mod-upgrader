package com.upgrader;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

public class UpgraderBlock extends Block {

    private static List<Item> ladder;

    private static final String[] WIN_MSGS = {
            "\u00a7aАПГРЕЙД! Ты везунчик!", "\u00a7aАпгрейдер доволен!", "\u00a7aЛетс гоу! Прокачка!",
            "\u00a7aКазино сегодня щедрое!", "\u00a7aЭто был не рандом, это скилл!"};
    private static final String[] FAIL_MSGS = {
            "\u00a7cАпгрейдер подавился!", "\u00a7cМолния съела твой предмет!", "\u00a7cШанс был... но не твой.",
            "\u00a7cF в чат.", "\u00a7cКазино всегда в плюсе!", "\u00a7cНу почти получилось!"};

    public UpgraderBlock() {
        super(Material.ROCK, MapColor.PURPLE);
        setRegistryName(UpgraderMod.MODID, "upgrader");
       setTranslationKey(UpgraderMod.MODID + ".upgrader");
        setCreativeTab(CreativeTabs.MISC);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.GLASS);
        setLightLevel(0.6F);
    }

    // Лестница апгрейдов (создаётся при первом использовании)
    private static List<Item> getLadder() {
        if (ladder == null) {
            ladder = new ArrayList<Item>();
            ladder.add(Item.getItemFromBlock(Blocks.DIRT));
            ladder.add(Item.getItemFromBlock(Blocks.PLANKS));
            ladder.add(Item.getItemFromBlock(Blocks.COBBLESTONE));
            ladder.add(Items.COAL);
            ladder.add(Items.IRON_INGOT);
            ladder.add(Items.GOLD_INGOT);
            ladder.add(Items.DIAMOND);
            ladder.add(Items.EMERALD);
            ladder.add(Items.GOLDEN_APPLE);
            ladder.add(Items.NETHER_STAR);
            ladder.add(Items.ELYTRA);
            ladder.add(Item.getItemFromBlock(Blocks.DRAGON_EGG));
        }
        return ladder;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        List<Item> lad = getLadder();
        ItemStack stack = player.getHeldItem(hand);
        int idx = stack.isEmpty() || stack.getMetadata() != 0 ? -1 : lad.indexOf(stack.getItem());

        if (idx < 0) {
            msg(player, "\u00a7cАпгрейдер такое не ест! Дай землю, доски, камень...");
            return true;
        }
        if (idx == lad.size() - 1) {
            msg(player, "\u00a7dВыше уже некуда. Ты победил игру.");
            return true;
        }

        boolean creative = player.capabilities.isCreativeMode;
        if (!creative) stack.shrink(1);

        double chance = Math.max(0.10, 0.85 - idx * 0.07);
        double x = pos.getX() + 0.5, y = pos.getY() + 1.1, z = pos.getZ() + 0.5;
        WorldServer ws = (WorldServer) world;
        String pct = " \u00a77(шанс был " + (int) (chance * 100) + "%)";

        if (world.rand.nextDouble() < chance) {
            Item next = lad.get(idx + 1);
            ItemStack result = new ItemStack(next);
            if (stack.isEmpty() || creative) {
                // новый предмет сразу появляется в руке
                player.setHeldItem(hand, result);
            } else if (!player.inventory.addItemStackToInventory(result)) {
                player.dropItem(result, false);
            }
            ws.spawnParticle(EnumParticleTypes.TOTEM, x, y, z, 40, 0.3, 0.5, 0.3, 0.3);
            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            msg(player, WIN_MSGS[world.rand.nextInt(WIN_MSGS.length)] + pct);

            if (next == Item.getItemFromBlock(Blocks.DRAGON_EGG)) {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(
                        new TextComponentString("\u00a76\u00a7l" + player.getName()
                                + " выбил ЯЙЦО ДРАКОНА в Апгрейдере!!!"));
            }
        } else {
            world.addWeatherEffect(new EntityLightningBolt(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, true));
            ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 30, 0.3, 0.3, 0.3, 0.05);
            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            msg(player, FAIL_MSGS[world.rand.nextInt(FAIL_MSGS.length)] + pct);
        }
        return true;
    }

    private static void msg(EntityPlayer player, String text) {
        player.sendStatusMessage(new TextComponentString(text), true);
    }
}
