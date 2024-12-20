package net.wetnoodle.braverbundles.mixin.item_entity;

import net.minecraft.world.item.BundleItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin {
//    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BundleItem;dropContents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z"), cancellable = true)
//    void braverBundles$use(
//            Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
//            @Local ItemStack itemStack
//    ) {
////        Vec3 vec3 = player.getViewVector(1.0F).normalize();
////        Vec3 vec32 = new Vec3(player.getX(), player.getEyeY(), player.getZ());
////        vec3.add(vec32);
//    }
}
