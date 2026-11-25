package com.myminecraft.illager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;

/**
 * Illager overhaul.
 *  - Pillagers: kite away from you (PillagerMixin adds {@link PillagerKiteGoal}), see farther, tougher.
 *  - Vindicators: faster and more health.
 *  - Evokers: more health, longer reach.
 */
public final class IllagerAiUpgrade {
    private static final String TAG = "myminecraft_upgraded";

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity.getTags().contains(TAG)) return;
            if (entity instanceof Pillager p) {
                p.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(36.0);
                p.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
                p.setHealth(30.0f);
                p.addTag(TAG);
            } else if (entity instanceof Vindicator v) {
                v.getAttribute(Attributes.MAX_HEALTH).setBaseValue(32.0);
                v.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4);
                v.setHealth(32.0f);
                v.addTag(TAG);
            } else if (entity instanceof Evoker e) {
                e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0);
                e.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(28.0);
                e.setHealth(36.0f);
                e.addTag(TAG);
            }
        });
    }

    private IllagerAiUpgrade() {}
}
