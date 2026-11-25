package com.myminecraft.animal;

import com.myminecraft.animal.AnimalSpec.Behavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * One entity class for all 24 animals. Everything that differs (size, AI, diet, flying) comes from {@link AnimalSpec}.
 * NOTE: Mob's constructor calls createNavigation()/registerGoals() BEFORE our fields exist, so the spec is looked up
 * lazily from the entity type's registry name instead of being relied on from the constructor.
 */
public class GenericAnimal extends Animal {
    private AnimalSpec spec; // intentionally no initializer (see class comment)

    public GenericAnimal(EntityType<? extends GenericAnimal> type, Level level, AnimalSpec spec) {
        super(type, level);
        this.spec = spec;
    }

    public AnimalSpec spec() {
        if (spec == null) {
            spec = AnimalSpec.byName(BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath());
        }
        return spec;
    }

    public static AttributeSupplier.Builder createAttributes(AnimalSpec s) {
        AttributeSupplier.Builder b = Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, s.health)
                .add(Attributes.MOVEMENT_SPEED, s.speed)
                .add(Attributes.ATTACK_DAMAGE, Math.max(1.0, s.damage))
                .add(Attributes.FOLLOW_RANGE, 24.0);
        if (s.flying) {
            b.add(Attributes.FLYING_SPEED, s.speed * 2.6);
        }
        return b;
    }

    // ------------------------------------------------------------------------------------- navigation / goals
    @Override
    protected PathNavigation createNavigation(Level level) {
        if (spec().flying) {
            this.moveControl = new FlyingMoveControl(this, 10, true);
            FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
            nav.setCanOpenDoors(false);
            nav.setCanFloat(true);
            return nav;
        }
        return super.createNavigation(level);
    }

    @Override
    protected void registerGoals() {
        AnimalSpec s = spec();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        switch (s.behavior) {
            case PREY -> this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
            case NEUTRAL, PREDATOR -> {
                this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
                this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            }
            default -> { }
        }
        if (s.behavior == Behavior.PREDATOR) {
            this.targetSelector.addGoal(2, new HuntGoal<>(this, Chicken.class));
            this.targetSelector.addGoal(2, new HuntGoal<>(this, Rabbit.class));
            if (s.bw >= 10 && !s.flying) {
                this.targetSelector.addGoal(3, new HuntGoal<>(this, Pig.class));
                this.targetSelector.addGoal(3, new HuntGoal<>(this, Cow.class));
            }
        }
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, s.flying ? new WaterAvoidingRandomFlyingGoal(this, 1.0)
                : new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // ------------------------------------------------------------------------------------- food / breeding
    @Override
    public boolean isFood(ItemStack stack) {
        return switch (spec().diet) {
            case HERBIVORE -> stack.is(Items.WHEAT) || stack.is(Items.APPLE) || stack.is(Items.CARROT) || stack.is(Items.SWEET_BERRIES);
            case CARNIVORE -> stack.is(Items.BEEF) || stack.is(Items.PORKCHOP) || stack.is(Items.MUTTON)
                    || stack.is(Items.CHICKEN) || stack.is(Items.RABBIT) || stack.is(Items.COD) || stack.is(Items.SALMON);
            case OMNIVORE -> stack.is(Items.SWEET_BERRIES) || stack.is(Items.SALMON) || stack.is(Items.COD)
                    || stack.is(Items.HONEYCOMB) || stack.is(Items.APPLE);
            case FISH -> stack.is(Items.COD) || stack.is(Items.SALMON);
            case SEEDS -> stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS)
                    || stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS);
        };
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return getType().create(level, EntitySpawnReason.BREEDING);
    }

    // ------------------------------------------------------------------------------------- sounds
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return spec().ambient;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return spec().hurt;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return spec().death;
    }

    // ------------------------------------------------------------------------------------- flying quirks
    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return !spec().flying && super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    protected void checkFallDamage(double heightDiff, boolean onGround, BlockState state, BlockPos pos) {
        if (!spec().flying) {
            super.checkFallDamage(heightDiff, onGround, state, pos);
        }
    }
}
