package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.entity.TTTrackedData;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends AbstractDecorationEntityMixin {

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    public void teatime$initWitheringData(CallbackInfo ci) {
        this.dataTracker.startTracking(TTTrackedData.WITHERING_AGE, 0);
        this.dataTracker.startTracking(TTTrackedData.WITHERING, false);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    public void teatime$saveWitheringData(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound compound = new NbtCompound();
        compound.putInt("WitheringAge", this.dataTracker.get(TTTrackedData.WITHERING_AGE));
        compound.putBoolean("Withering", this.dataTracker.get(TTTrackedData.WITHERING));
        nbt.put("WitheringData", compound);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    public void teatime$getWitheringData(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.contains("WitheringData")) return;

        NbtCompound witheringData = nbt.getCompound("WitheringData");

        if (witheringData.contains("WitheringAge")) {
            this.dataTracker.set(TTTrackedData.WITHERING_AGE, witheringData.getInt("WitheringAge"));
        }

        if (witheringData.contains("Withering")) {
            this.dataTracker.set(TTTrackedData.WITHERING, witheringData.getBoolean("Withering"));
        }
    }

}
