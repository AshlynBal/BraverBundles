package net.wetnoodle.braverbundles.mixin.item_entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.wetnoodle.braverbundles.BraverBundles;
import net.wetnoodle.braverbundles.config.BBConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin {
    @WrapOperation(method = "dropContent(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BundleItem;dropContent(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z"))
    boolean braverBundles$dropContent(BundleItem instance, ItemStack itemStack, Player player, Operation<Boolean> original) {
        if (!BBConfig.BUNDLE_SCOOPING) return original.call(instance, itemStack, player);
        boolean successfullyCollected = braverBundles$collectItem(itemStack, player);
        if (!successfullyCollected) {
            return original.call(instance, itemStack, player);
        }
        return false;
    }

    @Unique
    private boolean braverBundles$collectItem(ItemStack itemStack, Player player) {
        boolean success = false;
        HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(player, (entity -> true), player.blockInteractionRange());
        if (hitResult instanceof EntityHitResult entityHitResult) {
            BraverBundles.LOGGER.info(entityHitResult.getEntity().toString());
            if (entityHitResult.getEntity() instanceof ItemEntity itemEntity) {
                success = true;
                braverBundles$sendEntityToBundle(itemStack, itemEntity);
            }
        }
        return success;
    }

    @Unique
    private ItemStack braverBundles$sendEntityToBundle(ItemStack bundleStack, ItemEntity itemEntity) {
        BundleContents bundleContents = bundleStack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents == null) return bundleStack;
        int space = bundleContents.weight().getDenominator() - bundleContents.weight().getNumerator();
        BundleContents.Mutable mutable = new BundleContents.Mutable(bundleContents);
        if (space >= 1) {
            ItemStack stack = itemEntity.getItem().copy();
            int inserted = mutable.tryInsert(stack);
            if (inserted != 0) {
                itemEntity.setItem(stack);
            }
        }
        bundleStack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
        return bundleStack;
    }
}
