package net.wetnoodle.braverbundles.mixin.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {
    @Inject(method = "bootStrap", at = @At("HEAD"))
    private static void braverBundles$bootStrap(CallbackInfo ci) {
        OptionalDispenseItemBehavior bundleDispenseBehavior = new OptionalDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack bundleStack) {
                BundleContents bundleContents = bundleStack.get(DataComponents.BUNDLE_CONTENTS);
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos frontPos = blockSource.pos().relative(direction);
                List<ItemEntity> itemEntityList = blockSource.level()
                        .getEntitiesOfClass(
                                ItemEntity.class,
                                new AABB(frontPos),
                                livingEntity -> livingEntity instanceof ItemEntity
                        );
                // If there are no items in front of the dispenser
                if (itemEntityList.isEmpty()) {
                    if (bundleContents != null && !bundleContents.isEmpty()) {
                        Optional<ItemStack> optional = BraverBundles$removeOneItemFromBundle(bundleStack, bundleContents);
                        if (optional.isPresent()) {
                            spawnItem(blockSource.level(), optional.get(), 6, direction, DispenserBlock.getDispensePosition(blockSource));
                            playSound(blockSource);
                            // play bundle sound
                            playAnimation(blockSource, direction);
                            setSuccess(true);
                            return bundleStack;
                        }
//                        for (ItemStack dispensedItem : bundleContents.itemsCopy()) {
//                            spawnItem(blockSource.level(), dispensedItem, 6, direction, DispenserBlock.getDispensePosition(blockSource));
//                        }
//                        playSound(blockSource);
//                        playAnimation(blockSource, direction);
//                        itemStack.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
//                        setSuccess(true);
//                        return itemStack;
                    }
                }
                // Else if there are items in front of the dispenser
                else if (bundleContents != null) {
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
                    bundleStack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
                    setSuccess(inserted != 0);
                    return bundleStack;
                }
                setSuccess(false);
                return bundleStack;
            }
        };
        registerBundleBehavior(bundleDispenseBehavior);
    }

    @Unique
    private static void registerBundleBehavior(OptionalDispenseItemBehavior bundleDispenseBehavior) {
        Item[] bundles = {Items.BUNDLE, Items.BLACK_BUNDLE, Items.BLUE_BUNDLE, Items.BROWN_BUNDLE, Items.CYAN_BUNDLE,
                Items.GRAY_BUNDLE, Items.GREEN_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.LIGHT_GRAY_BUNDLE,
                Items.LIME_BUNDLE, Items.MAGENTA_BUNDLE, Items.ORANGE_BUNDLE, Items.PINK_BUNDLE, Items.PURPLE_BUNDLE,
                Items.RED_BUNDLE, Items.WHITE_BUNDLE, Items.YELLOW_BUNDLE};
        for (Item bundle : bundles) {
            DispenserBlock.registerBehavior(bundle, bundleDispenseBehavior);
        }
    }

    @Unique
    private static Optional<ItemStack> BraverBundles$removeOneItemFromBundle(ItemStack bundle, BundleContents bundleContents) {
        BundleContents.Mutable mutable = new BundleContents.Mutable(bundleContents);
        ItemStack removedStack = mutable.removeOne();
        if (removedStack != null) {
            bundle.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
            return Optional.of(removedStack);
        } else {
            return Optional.empty();
        }
    }
}
