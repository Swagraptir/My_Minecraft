package com.myminecraft.registry;

import com.myminecraft.Ids;
import com.myminecraft.animal.AnimalSpec;
import com.myminecraft.animal.AnimalSpecs;
import com.myminecraft.animal.Barnacle;
import com.myminecraft.animal.Firefly;
import com.myminecraft.animal.GenericAnimal;
import com.myminecraft.animal.GenericMonster;
import com.myminecraft.animal.Wildfire;
import com.myminecraft.animal.Glare;
import com.myminecraft.animal.Moobloom;
import com.myminecraft.animal.Rascal;
import com.myminecraft.animal.TuffGolem;
import com.myminecraft.furniture.SeatEntity;
import com.myminecraft.guard.MedievalGuard;
import com.myminecraft.illager.FrostGolem;
import com.myminecraft.illager.IceShard;
import com.myminecraft.illager.Iceologer;
import com.myminecraft.vehicle.VehicleMob;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.golem.SnowGolem;

@SuppressWarnings("unchecked")
public final class ModEntities {
    public static EntityType<SeatEntity> SEAT;
    public static EntityType<IceShard> ICE_SHARD;
    public static EntityType<Iceologer> ICEOLOGER;
    public static EntityType<FrostGolem> FROST_GOLEM;
    public static EntityType<MedievalGuard> KNIGHT;
    public static EntityType<MedievalGuard> ARCHER_GUARD;
    public static EntityType<VehicleMob> BIKE;
    public static EntityType<VehicleMob> CAR;
    public static EntityType<Wildfire> WILDFIRE;
    public static EntityType<Barnacle> BARNACLE;
    public static final Map<String, EntityType<? extends Mob>> ANIMALS = new LinkedHashMap<>();

    private static <T extends Entity> EntityType<T> reg(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = Ids.entityKey(name);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void register() {
        SEAT = reg("seat", EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                .sized(0.01f, 0.01f).noSave().clientTrackingRange(8).updateInterval(20));
        ICE_SHARD = reg("ice_shard", EntityType.Builder.<IceShard>of(IceShard::new, MobCategory.MISC)
                .sized(0.3f, 0.3f).clientTrackingRange(4).updateInterval(10));

        ICEOLOGER = reg("iceologer", EntityType.Builder.<Iceologer>of(Iceologer::new, MobCategory.MONSTER)
                .sized(0.6f, 1.95f).clientTrackingRange(8));
        FabricDefaultAttributeRegistry.register(ICEOLOGER, Iceologer.createAttributes());

        FROST_GOLEM = reg("frost_golem", EntityType.Builder.<FrostGolem>of(FrostGolem::new, MobCategory.MISC)
                .sized(0.7f, 1.9f).clientTrackingRange(8));
        FabricDefaultAttributeRegistry.register(FROST_GOLEM, SnowGolem.createAttributes());

        KNIGHT = reg("knight", EntityType.Builder.<MedievalGuard>of((t, l) -> new MedievalGuard(t, l, MedievalGuard.Role.KNIGHT), MobCategory.CREATURE)
                .sized(0.6f, 1.95f).clientTrackingRange(10));
        ARCHER_GUARD = reg("archer_guard", EntityType.Builder.<MedievalGuard>of((t, l) -> new MedievalGuard(t, l, MedievalGuard.Role.ARCHER), MobCategory.CREATURE)
                .sized(0.6f, 1.95f).clientTrackingRange(10));
        FabricDefaultAttributeRegistry.register(KNIGHT, MedievalGuard.createAttributes(MedievalGuard.Role.KNIGHT));
        FabricDefaultAttributeRegistry.register(ARCHER_GUARD, MedievalGuard.createAttributes(MedievalGuard.Role.ARCHER));

        BIKE = reg("bike", EntityType.Builder.<VehicleMob>of((t, l) -> new VehicleMob(t, l, VehicleMob.Kind.BIKE), MobCategory.MISC)
                .sized(0.7f, 1.0f).passengerAttachments(0.55f).clientTrackingRange(10));
        CAR = reg("car", EntityType.Builder.<VehicleMob>of((t, l) -> new VehicleMob(t, l, VehicleMob.Kind.CAR), MobCategory.MISC)
                .sized(1.8f, 1.3f).passengerAttachments(0.45f).clientTrackingRange(10));
        FabricDefaultAttributeRegistry.register(BIKE, VehicleMob.createAttributes(VehicleMob.Kind.BIKE));
        FabricDefaultAttributeRegistry.register(CAR, VehicleMob.createAttributes(VehicleMob.Kind.CAR));

        for (AnimalSpec spec : AnimalSpecs.ALL) {
            if (spec.monster) {
                EntityType.Builder<GenericMonster> b = EntityType.Builder.<GenericMonster>of((t, l) -> switch (spec.special) {
                            case WILDFIRE -> new Wildfire(t, l, spec);
                            case BARNACLE -> new Barnacle(t, l, spec);
                            default -> new GenericMonster(t, l, spec);
                        }, spec.special == AnimalSpec.Special.BARNACLE ? MobCategory.WATER_CREATURE : MobCategory.MONSTER)
                        .sized(spec.hitWidth(), spec.hitHeight()).clientTrackingRange(spec.bw < 9 ? 6 : 8);
                if (spec.special == AnimalSpec.Special.WILDFIRE) {
                    b.fireImmune();
                }
                EntityType<GenericMonster> type = reg(spec.name, b);
                ANIMALS.put(spec.name, type);
                FabricDefaultAttributeRegistry.register(type, spec.special == AnimalSpec.Special.BARNACLE
                        ? Barnacle.createAttributes(spec) : GenericMonster.createAttributes(spec));
                if (spec.special == AnimalSpec.Special.WILDFIRE) WILDFIRE = (EntityType<Wildfire>) (EntityType<?>) type;
                if (spec.special == AnimalSpec.Special.BARNACLE) BARNACLE = (EntityType<Barnacle>) (EntityType<?>) type;
                continue;
            }
            EntityType<GenericAnimal> type = reg(spec.name, EntityType.Builder.<GenericAnimal>of(
                            (t, l) -> switch (spec.special) {
                                case MOOBLOOM -> new Moobloom(t, l, spec);
                                case GLARE -> new Glare(t, l, spec);
                                case RASCAL -> new Rascal(t, l, spec);
                                case TUFF_GOLEM -> new TuffGolem(t, l, spec);
                                case FIREFLY -> new Firefly(t, l, spec);
                                default -> new GenericAnimal(t, l, spec);
                            }, spec.spawnKind == AnimalSpec.SpawnKind.CAVE || spec.spawnKind == AnimalSpec.SpawnKind.TUFF
                                    ? MobCategory.AMBIENT : MobCategory.CREATURE)
                    .sized(spec.hitWidth(), spec.hitHeight()).clientTrackingRange(spec.bw < 9 ? 6 : 8));
            ANIMALS.put(spec.name, type);
            FabricDefaultAttributeRegistry.register(type, GenericAnimal.createAttributes(spec));
        }
    }

    private ModEntities() {}
}
