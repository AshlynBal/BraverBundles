package net.wetnoodle.braverbundles.mixin.item_entity;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends EntityMixin {
    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int pickupDelay;

    @Shadow
    private @Nullable UUID target;

//    @Override
//    void braverBundles$interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
//        ItemStack bundle = player.getItemInHand(interactionHand);
//        if (bundle.is(Items.BUNDLE)) {
//            if (!this.level().isClientSide) {
//                ItemStack itemStack = this.getItem();
//                Item item = itemStack.getItem();
//                int i = itemStack.getCount();
//                if (this.pickupDelay == 0 && (this.target == null || this.target.equals(player.getUUID())) /*&& attempt to bundle*/) {
//
//                }
//            }
//        } else super.braverBundles$interact(player, interactionHand, cir);
//    }
//
//    @Unique
//    void braverBundles$moveToBundle(ItemStack bundle, ItemStack entityStack, Player player) {
//        BundleContents bundleContents = bundle.get(DataComponents.BUNDLE_CONTENTS);
//        if (bundleContents == null || !entityStack.getItem().canFitInsideContainerItems()) return;
//        BundleContents.Mutable mutable = new BundleContents.Mutable(bundleContents);
//        int i = mutable.tryInsert(entityStack);
//        if (i > 0) {
////            BundleItem.playInsertSound(player);
//        }
//    }
}
