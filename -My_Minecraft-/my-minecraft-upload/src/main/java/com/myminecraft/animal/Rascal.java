package com.myminecraft.animal;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

/**
 * 2022 mob-vote loser: a cave creature that plays hide and seek. It runs away when it sees you; walk up to it three
 * times (it hides again after every find) and it hands over a reward.
 */
public class Rascal extends GenericAnimal {
    private static final ItemLike[] REWARDS = {Items.COAL, Items.COAL, Items.IRON_NUGGET, Items.GOLD_NUGGET, Items.REDSTONE,
            Items.LAPIS_LAZULI, Items.IRON_INGOT, Items.GOLD_INGOT, Items.EMERALD, Items.COPPER_INGOT, Items.AMETHYST_SHARD};
    private int found;
    private int cooldown;

    public Rascal(EntityType<? extends GenericAnimal> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 8.0f, 1.1, 1.6));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server)) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (tickCount % 5 != 0) return;
        Player p = server.getNearestPlayer(this, 2.5);
        if (p != null && hasLineOfSight(p)) {
            found++;
            cooldown = 100;
            p.displayClientMessage(Component.literal("Rascal found: " + found + "/3"), true);
            if (found >= 3) {
                found = 0;
                int count = 1 + random.nextInt(3);
                ItemLike reward = random.nextInt(12) == 0 ? Items.DIAMOND : REWARDS[random.nextInt(REWARDS.length)];
                spawnAtLocation(server, new ItemStack(reward, count));
            }
            hideAgain();
        }
    }

    private void hideAgain() {
        for (int i = 0; i < 12; i++) {
            double a = random.nextDouble() * Math.PI * 2;
            double d = 8 + random.nextInt(8);
            if (randomTeleport(getX() + Math.cos(a) * d, getY(), getZ() + Math.sin(a) * d, true)) {
                return;
            }
        }
    }
}
