package net.wetnoodle.braverbundles.mixin.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {
    @Inject(method = "bootStrap", at = @At("HEAD"))
    private static void braverBundles$bootStrap(CallbackInfo ci) {
        registerBundleBehavior();
    }

    @Unique
    private static void registerBundleBehavior() {
        DispenserBlock.registerBehavior(Items.BUNDLE, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BundleContents bundleContents = itemStack.get(DataComponents.BUNDLE_CONTENTS);
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos frontPos = blockSource.pos().relative(direction);
                List<ItemEntity> itemEntityList = blockSource.level()
                        .getEntitiesOfClass(
                                ItemEntity.class,
                                new AABB(frontPos),
                                livingEntity -> livingEntity instanceof ItemEntity
                        );
                if (itemEntityList.isEmpty()) {
                    if (bundleContents != null && !bundleContents.isEmpty()) {
                        for (ItemStack dispensedItem : bundleContents.itemsCopy()) {
                            spawnItem(blockSource.level(), dispensedItem, 6, direction, DispenserBlock.getDispensePosition(blockSource));
                        }
                        playSound(blockSource);
                        playAnimation(blockSource, direction);
                        itemStack.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
                        setSuccess(true);
                        return itemStack;
                    }
                } else if (bundleContents != null) {
                    int space = bundleContents.weight().getDenominator() - bundleContents.weight().getNumerator();
                    BundleContents.Mutable mutable = new BundleContents.Mutable(bundleContents);
                    int inserted = 0;
                    if (space >= 1) for (ItemEntity itemEntity : itemEntityList) {
                        ItemStack stack = itemEntity.getItem().copy();
                        inserted = mutable.tryInsert(stack);
                        if (inserted != 0) {
                            itemEntity.setItem(stack);
                            break;
                        }
                    }
                    itemStack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
                    setSuccess(inserted != 0);
                    return itemStack;
                }
                setSuccess(false);
                return itemStack;
            }
        });


//        DispenserBlock.registerBehavior(Items.BUNDLE, new DefaultDispenseItemBehavior() {
//            @Override
//            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
//                BundleContents bundleContents = itemStack.get(DataComponents.BUNDLE_CONTENTS);
//                if (bundleContents != null && !bundleContents.isEmpty()) {
//                    Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
//                    for (ItemStack dispensedItem : bundleContents.itemsCopy()) {
//                        spawnItem(blockSource.level(), dispensedItem, 6, direction, DispenserBlock.getDispensePosition(blockSource));
//                    }
//                    playSound(blockSource);
//                    playAnimation(blockSource, direction);
//                    itemStack.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
//                    return itemStack;
//                } else {
//                    return super.execute(blockSource, itemStack);
//                }
//            }
//        });


    }

    @Unique
    default void braverBundles$moveToBundle(ItemStack bundle, ItemStack entityStack, Player player) {
        BundleContents bundleContents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents == null || !entityStack.getItem().canFitInsideContainerItems()) return;
        BundleContents.Mutable mutable = new BundleContents.Mutable(bundleContents);
        int i = mutable.tryInsert(entityStack);
        if (i > 0) {
//            BundleItem.playInsertSound(player);
        }
    }
}
