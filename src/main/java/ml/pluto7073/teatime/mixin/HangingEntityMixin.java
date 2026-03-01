package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.item.TTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HangingEntity.class)
public abstract class HangingEntityMixin extends EntityMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    public void teatime$updateWitheringTea(CallbackInfo ci) {
        if (!(((HangingEntity) (Object) this) instanceof ItemFrame thisFrame)) return;
        ItemStack witheringItem = thisFrame.getEntityData().get(ItemFrameEntityAccessor.getItemStack());
        if (!witheringItem.is(TTItems.TEA_LEAVES)) {
            this.entityData.set(TTTrackedData.WITHERING_AGE, 0);
            this.entityData.set(TTTrackedData.WITHERING, false);
            return;
        }
        int age = this.entityData.get(TTTrackedData.WITHERING_AGE);
        this.entityData.set(TTTrackedData.WITHERING, true);
        ++age;
        if (level().isRainingAt(BlockPos.containing(position().x, position().y, position().z))) {
            age = 0;
        }
        if (age >= 6000) {
            this.entityData.set(ItemFrameEntityAccessor.getItemStack(), new ItemStack(TTItems.WITHERED_TEA_LEAVES));
        }
        this.entityData.set(TTTrackedData.WITHERING_AGE, age);
    }

}
