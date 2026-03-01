package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.item.TTItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Shadow
    private static void add(float chance, ItemLike item) {
    }

    @Inject(at = @At("TAIL"), method = "bootStrap")
    private static void teatime_registerModCompostItems(CallbackInfo ci) {
        add(0.3F, TTItems.TEA_SEEDS);
        add(0.3F, TTItems.TEA_LEAVES);
    }

}
