package com.myminecraft.animal;

import com.myminecraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** 2020 mob-vote loser: a yellow cow covered in buttercups. Plants buttercups as it wanders; shear it for more. */
public class Moobloom extends GenericAnimal {
    private int regrow;

    public Moobloom(EntityType<? extends GenericAnimal> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && regrow <= 0 && !isBaby()) {
            if (level() instanceof ServerLevel server) {
                int n = 2 + random.nextInt(2);
                for (int i = 0; i < n; i++) {
                    spawnAtLocation(server, new ItemStack(ModBlocks.BUTTERCUP.asItem()));
                }
                regrow = 6000;
                server.playSound(null, blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0f, 1.0f);
                stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide()) return;
        if (regrow > 0) regrow--;
        if (!isBaby() && random.nextInt(900) == 0) {
            BlockPos p = blockPosition();
            if (level().getBlockState(p).isAir() && level().getBlockState(p.below()).is(BlockTags.DIRT)) {
                level().setBlock(p, ModBlocks.BUTTERCUP.defaultBlockState(), 3);
            }
        }
    }
}
