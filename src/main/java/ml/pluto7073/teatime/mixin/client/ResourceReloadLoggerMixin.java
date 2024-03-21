package ml.pluto7073.teatime.mixin.client;

import ml.pluto7073.teatime.utils.VersionChecker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceReloadLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ResourceReloadLogger.class)
public class ResourceReloadLoggerMixin {

    @Inject(at = @At("TAIL"), method = "finish")
    public void teatime_callWarningScreen(CallbackInfo ci) {
        VersionChecker.showWarningScreen();
    }

}
