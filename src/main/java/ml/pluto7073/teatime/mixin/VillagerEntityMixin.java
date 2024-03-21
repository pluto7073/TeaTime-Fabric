package ml.pluto7073.teatime.mixin;

import com.google.common.collect.ImmutableSet;
import ml.pluto7073.teatime.item.ModItems;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntityMixin{

    @Inject(at = @At("TAIL"), method = "hasSeedToPlant", cancellable = true)
    public void teatime_hasTeaSeed(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.getInventory().containsAny(ImmutableSet.of(ModItems.TEA_SEEDS)));
    }

}
