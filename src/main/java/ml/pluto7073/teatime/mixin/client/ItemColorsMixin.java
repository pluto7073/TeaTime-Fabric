package ml.pluto7073.teatime.mixin.client;

import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ItemColors.class)
public class ItemColorsMixin {

    @Inject(at = @At("TAIL"), method = "create", cancellable = true)
    private static void teatime_setTeaColors(BlockColors blockColors, CallbackInfoReturnable<ItemColors> cir) {
        ItemColors colors = cir.getReturnValue();
        colors.register((stack, tintIndex) -> tintIndex > 0 ? -1 : TeaTimeUtils.getTeaType(stack).getColour(), ModItems.TEA);
        cir.setReturnValue(colors);
    }

}
