package cx.tfe.fennec.mixin;

import cx.tfe.fennec.features.impl.debug.ShowEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the vanilla glow outline on while HighlightMobs is enabled, by
 * overriding the exact method the entity renderer already calls to decide
 * whether to draw it. Scoped to LivingEntity (mobs, animals, players) —
 * widen to Entity directly if you also want item drops, arrows, boats,
 * etc. to glow.
 */
@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void fennec$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        if (!ShowEntities.INSTANCE.getEnabled()) return;

        Entity self = (Entity) (Object) this;


        if (self instanceof LivingEntity) {
            cir.setReturnValue(true);
        }
    }
}