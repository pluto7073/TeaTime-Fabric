package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.item.ModItems;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Shadow
    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
    }

    @Inject(at = @At("TAIL"), method = "registerDefaultCompostableItems")
    private static void teatime_registerModCompostItems(CallbackInfo ci) {
        registerCompostableItem(0.3F, ModItems.TEA_SEEDS);
        registerCompostableItem(0.3F, ModItems.TEA_LEAVES);
    }

}
