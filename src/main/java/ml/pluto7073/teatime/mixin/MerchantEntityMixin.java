package ml.pluto7073.teatime.mixin;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {
    @Shadow public abstract SimpleInventory getInventory();
}
