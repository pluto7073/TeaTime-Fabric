package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.item.ModItems;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractDecorationEntity.class)
public abstract class AbstractDecorationEntityMixin extends EntityMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    public void teatime$updateWitheringTea(CallbackInfo ci) {
        if (!(((AbstractDecorationEntity) (Object) this) instanceof ItemFrameEntity thisFrame)) return;
        ItemStack witheringItem = thisFrame.getDataTracker().get(ItemFrameEntityAccessor.getItemStack());
        if (!witheringItem.isOf(ModItems.TEA_LEAVES)) {
            this.dataTracker.set(TTTrackedData.WITHERING_AGE, 0);
            this.dataTracker.set(TTTrackedData.WITHERING, false);
            return;
        }
        int age = this.dataTracker.get(TTTrackedData.WITHERING_AGE);
        this.dataTracker.set(TTTrackedData.WITHERING, true);
        ++age;
        if (getWorld().hasRain(BlockPos.ofFloored(getPos().x, getPos().y, getPos().z))) {
            age = 0;
        }
        if (age >= 6000) {
            this.dataTracker.set(ItemFrameEntityAccessor.getItemStack(), new ItemStack(ModItems.WITHERED_TEA_LEAVES));
        }
        this.dataTracker.set(TTTrackedData.WITHERING_AGE, age);
    }

}
