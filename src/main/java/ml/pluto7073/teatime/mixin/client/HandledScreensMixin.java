package ml.pluto7073.teatime.mixin.client;

import ml.pluto7073.teatime.gui.SteamerScreen;
import ml.pluto7073.teatime.gui.handlers.ModScreenHandlerTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreens.class)
public class HandledScreensMixin {

    @Shadow
    public static <M extends ScreenHandler, U extends Screen & ScreenHandlerProvider<M>> void register(ScreenHandlerType<? extends M> type, HandledScreens.Provider<M, U> provider) {}

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void teatime_registerHandledScreens(CallbackInfo ci) {
        register(ModScreenHandlerTypes.STEAMER_SCREEN_HANDLER, SteamerScreen::new);
    }

}
