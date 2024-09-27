package net.wetnoodle.braverbundles.mixin.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {
    @Inject(method = "bootStrap", at = @At("HEAD"))
    private static void braverBundles$bootStrap(CallbackInfo ci) {
        registerBundleBehavior();
    }

    @Unique
    private static void registerBundleBehavior() {
        DispenserBlock.registerBehavior(Items.BUNDLE, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BundleContents bundleContents = itemStack.get(DataComponents.BUNDLE_CONTENTS);
                if (bundleContents != null && !bundleContents.isEmpty()) {
                    Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                    for (ItemStack dispensedItem : bundleContents.itemsCopy()) {
                        spawnItem(blockSource.level(), dispensedItem, 6, direction, DispenserBlock.getDispensePosition(blockSource));
                    }
                    playSound(blockSource);
                    playAnimation(blockSource, direction);
                    itemStack.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
                    return itemStack;
                } else {
                    return super.execute(blockSource, itemStack);
                }
            }
        });
    }
}
