package com.myminecraft.registry;

import com.myminecraft.Compat;
import com.myminecraft.Ids;
import com.myminecraft.furniture.TotemOfFreezing;
import com.myminecraft.vehicle.VehicleItem;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Block items, vehicle items, spawn eggs and the creative tab. */
public final class ModItems {
    public static final List<Item> ALL = new ArrayList<>();
    public static Item BIKE;
    public static Item CAR;
    public static Item CRAB_CLAW;
    public static Item CLAM_PEARL;
    public static Item TOTEM_OF_FREEZING;

    private static <I extends Item> I register(String name, java.util.function.Function<Item.Properties, I> factory, boolean blockPrefix) {
        ResourceKey<Item> key = Ids.itemKey(name);
        Item.Properties props = new Item.Properties().setId(key);
        if (blockPrefix) props = props.useBlockDescriptionPrefix();
        I item = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(props));
        ALL.add(item);
        return item;
    }

    private static void blockItem(String name, Block block) {
        register(name, p -> new BlockItem(block, p), true);
    }

    private static void egg(String name, EntityType<?> type) {
        ResourceKey<Item> key = Ids.itemKey(name + "_spawn_egg");
        Item item = Registry.register(BuiltInRegistries.ITEM, key, new Item(new Item.Properties().setId(key).spawnEgg(type)));
        ALL.add(item);
    }

    public static void register() {
        ModBlocks.FURNITURE.forEach(ModItems::blockItem);
        blockItem("pool_tile", ModBlocks.POOL_TILE);
        blockItem("pool_tile_white", ModBlocks.POOL_TILE_WHITE);
        blockItem("buttercup", ModBlocks.BUTTERCUP);
        blockItem("clam", ModBlocks.CLAM);
        blockItem("underwater_torch", ModBlocks.UNDERWATER_TORCH);
        CLAM_PEARL = register("clam_pearl", Item::new, false);
        TOTEM_OF_FREEZING = register("totem_of_freezing", p -> new TotemOfFreezing(p.durability(16)), false);
        CRAB_CLAW = register("crab_claw", p -> new Item(p.attributes(ItemAttributeModifiers.builder()
                .add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(Ids.of("crab_claw_block_reach"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(Ids.of("crab_claw_block_reach_off"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
                .build())), false);

        BIKE = register("bike", p -> new VehicleItem(p, () -> ModEntities.BIKE), false);
        CAR = register("car", p -> new VehicleItem(p, () -> ModEntities.CAR), false);

        ModEntities.ANIMALS.forEach(ModItems::egg);
        egg("iceologer", ModEntities.ICEOLOGER);
        egg("knight", ModEntities.KNIGHT);
        egg("archer_guard", ModEntities.ARCHER_GUARD);

        ResourceKey<CreativeModeTab> tabKey = Ids.key(Registries.CREATIVE_MODE_TAB, "main");
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup." + Ids.MOD_ID))
                .icon(() -> new ItemStack(ModBlocks.FURNITURE.get("red_sofa")))
                .displayItems((params, out) -> ALL.forEach(item -> {
                    if (!Compat.hidden(BuiltInRegistries.ITEM.getKey(item).getPath())) out.accept(item);
                }))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, tab);
    }

    private ModItems() {}
}
