package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.entity.TTTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends BlockAttachedEntityMixin {

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    public void teatime$initWitheringData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(TTTrackedData.WITHERING_AGE, 0);
        builder.define(TTTrackedData.WITHERING, false);
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    public void teatime$saveWitheringData(CompoundTag nbt, CallbackInfo ci) {
        CompoundTag compound = new CompoundTag();
        compound.putInt("WitheringAge", this.entityData.get(TTTrackedData.WITHERING_AGE));
        compound.putBoolean("Withering", this.entityData.get(TTTrackedData.WITHERING));
        nbt.put("WitheringData", compound);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    public void teatime$getWitheringData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.contains("WitheringData")) return;

        CompoundTag witheringData = nbt.getCompound("WitheringData");

        if (witheringData.contains("WitheringAge")) {
            this.entityData.set(TTTrackedData.WITHERING_AGE, witheringData.getInt("WitheringAge"));
        }

        if (witheringData.contains("Withering")) {
            this.entityData.set(TTTrackedData.WITHERING, witheringData.getBoolean("Withering"));
        }
    }

}
