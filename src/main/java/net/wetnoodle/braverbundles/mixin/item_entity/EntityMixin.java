package net.wetnoodle.braverbundles.mixin.item_entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

//    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
//    void braverBundles$interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
//    }
}
