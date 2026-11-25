package com.myminecraft.illager;

import com.myminecraft.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * The Iceologer: a frost caster illager.
 *  - Ice Barrage: throws a fan of ice shards (its ranged attack)
 *  - Frost Summon: calls Frost Golems (snow golems that fight for it)
 * When {@code boss} is true (the Warlord of the ice castle) it has a boss bar, much more health and casts faster.
 */
public class Iceologer extends SpellcasterIllager {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.myminecraft.iceologer_boss"), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean boss;

    public Iceologer(EntityType<? extends Iceologer> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.MAX_HEALTH, 28.0);
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
        if (boss) {
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(200.0);
            getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75);
            getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(40.0);
            setHealth(getMaxHealth());
            setPersistenceRequired();
            setCustomName(Component.translatable("entity.myminecraft.iceologer_boss"));
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.BLUE_ICE));
        }
    }

    public boolean isBoss() {
        return boss;
    }

    // ------------------------------------------------------------------------------------- goals
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0f, 0.6, 1.0));
        this.goalSelector.addGoal(4, new FrostSummonGoal());
        this.goalSelector.addGoal(5, new IceBarrageGoal());
        this.goalSelector.addGoal(6, new IceRainGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
    }

    private void barrage(LivingEntity target) {
        if (!(level() instanceof ServerLevel server)) return;
        int count = boss ? 5 : 3;
        Vec3 from = position().add(0, getEyeHeight() - 0.2, 0);
        for (int i = 0; i < count; i++) {
            IceShard shard = new IceShard(ModEntities.ICE_SHARD, server);
            shard.setOwner(this);
            shard.setPos(from);
            double dx = target.getX() - from.x;
            double dy = target.getEyeY() - from.y;
            double dz = target.getZ() - from.z;
            double flat = Math.max(Math.sqrt(dx * dx + dz * dz), 1.0);
            double spread = (i - (count - 1) / 2.0) * 0.12 * flat;
            shard.shoot(dx + dz / flat * spread, dy + flat * 0.1, dz - dx / flat * spread, 1.3f, 2.0f);
            server.addFreshEntity(shard);
        }
    }

    private void summonGolems(LivingEntity target) {
        if (!(level() instanceof ServerLevel server)) return;
        int count = boss ? 3 : 2;
        for (int i = 0; i < count; i++) {
            FrostGolem golem = ModEntities.FROST_GOLEM.create(server, EntitySpawnReason.MOB_SUMMONED);
            if (golem == null) continue;
            double a = random.nextDouble() * Math.PI * 2;
            golem.snapTo(getX() + Math.cos(a) * 2.5, getY(), getZ() + Math.sin(a) * 2.5, random.nextFloat() * 360f, 0);
            golem.setTarget(target);
            server.addFreshEntity(golem);
        }
    }

    private int golemCache;
    private int golemCacheTick = -100;

    /** Entity searches are not free: the count is cached for a second. */
    private int golemsNearby() {
        if (tickCount - golemCacheTick > 20) {
            golemCache = level().getEntitiesOfClass(FrostGolem.class, getBoundingBox().inflate(24)).size();
            golemCacheTick = tickCount;
        }
        return golemCache;
    }

    class IceBarrageGoal extends SpellcasterUseSpellGoal {
        @Override
        public boolean canUse() {
            LivingEntity t = getTarget();
            return super.canUse() && t != null && distanceToSqr(t) < 28 * 28;
        }

        @Override protected int getCastingTime() { return boss ? 22 : 34; }
        @Override protected int getCastingInterval() { return boss ? 70 : 110; }

        @Override
        protected void performSpellCasting() {
            LivingEntity t = getTarget();
            if (t != null) barrage(t);
        }

        @Override protected SoundEvent getSpellPrepareSound() { return SoundEvents.EVOKER_PREPARE_ATTACK; }
        @Override protected IllagerSpell getSpell() { return IllagerSpell.BLINDNESS; }
    }

    /** Summons blocks of packed ice that fall on the target (idea from the "Chillager" in Lost Features). */
    class IceRainGoal extends SpellcasterUseSpellGoal {
        @Override
        public boolean canUse() {
            LivingEntity t = getTarget();
            return super.canUse() && t != null && distanceToSqr(t) < 30 * 30;
        }

        @Override protected int getCastingTime() { return 40; }
        @Override protected int getCastingInterval() { return boss ? 150 : 260; }

        @Override
        protected void performSpellCasting() {
            LivingEntity t = getTarget();
            if (t == null || !(level() instanceof ServerLevel server)) return;
            int n = boss ? 8 : 4;
            for (int i = 0; i < n; i++) {
                BlockPos p = t.blockPosition().offset(random.nextInt(7) - 3, 8 + random.nextInt(3), random.nextInt(7) - 3);
                if (server.getBlockState(p).isAir()) {
                    FallingBlockEntity ice = FallingBlockEntity.fall(server, p, Blocks.PACKED_ICE.defaultBlockState());
                    ice.setHurtsEntities(3.0f, 40);
                    ice.dropItem = false;
                }
            }
        }

        @Override protected SoundEvent getSpellPrepareSound() { return SoundEvents.EVOKER_PREPARE_WOLOLO; }
        @Override protected IllagerSpell getSpell() { return IllagerSpell.FANGS; }
    }

    class FrostSummonGoal extends SpellcasterUseSpellGoal {
        @Override
        public boolean canUse() {
            return super.canUse() && golemsNearby() < (boss ? 5 : 3);
        }

        @Override protected int getCastingTime() { return 60; }
        @Override protected int getCastingInterval() { return boss ? 260 : 420; }

        @Override
        protected void performSpellCasting() {
            LivingEntity t = getTarget();
            if (t != null) summonGolems(t);
        }

        @Override protected SoundEvent getSpellPrepareSound() { return SoundEvents.EVOKER_PREPARE_SUMMON; }
        @Override protected IllagerSpell getSpell() { return IllagerSpell.SUMMON_VEX; }
    }

    // ------------------------------------------------------------------------------------- boss bar & persistence
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (boss) bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (boss) {
            bossEvent.setProgress(getHealth() / getMaxHealth());
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Boss", boss);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getBooleanOr("Boss", false)) {
            float saved = getHealth();
            setBoss(true);
            setHealth(Math.min(saved, getMaxHealth()));
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean playerKill) {
        super.dropCustomDeathLoot(level, source, playerKill);
        if (boss) {
            spawnAtLocation(level, new ItemStack(Items.BLUE_ICE, 8 + random.nextInt(9)));
            spawnAtLocation(level, new ItemStack(Items.DIAMOND, 3 + random.nextInt(4)));
            spawnAtLocation(level, new ItemStack(Items.TOTEM_OF_UNDYING));
            spawnAtLocation(level, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
        }
    }

    // ------------------------------------------------------------------------------------- misc illager plumbing
    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain) {}

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.EVOKER_HURT;
    }
}
