package com.myminecraft.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 2022 mob-vote loser: a living statue. Give it an item (right-click) and it holds it; right-click with an empty hand to
 * take it back. It randomly freezes into a statue for a while and wakes up when you come close.
 */
public class TuffGolem extends GenericAnimal {
    private int statue;

    public TuffGolem(EntityType<? extends GenericAnimal> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack mine = getMainHandItem();
        if (mine.isEmpty() && !held.isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, held.split(1));
            setDropChance(EquipmentSlot.MAINHAND, 1.0f);
            return InteractionResult.SUCCESS;
        }
        if (!mine.isEmpty() && held.isEmpty()) {
            player.setItemInHand(hand, mine);
            setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server)) return;
        boolean check = tickCount % 10 == 0;               // player lookups are throttled to twice a second
        if (statue > 0) {
            statue--;
            if (check && server.getNearestPlayer(this, 3.0) != null) statue = 0;
        } else if (check && random.nextInt(50) == 0 && server.getNearestPlayer(this, 7.0) == null) {
            statue = 200 + random.nextInt(400);
        }
        setNoAi(statue > 0);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        statue = 0;
        setNoAi(false);
        return super.hurtServer(level, source, amount);
    }
}
