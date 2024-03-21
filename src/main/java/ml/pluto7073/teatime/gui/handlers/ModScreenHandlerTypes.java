package ml.pluto7073.teatime.gui.handlers;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlerTypes {

    public static final ScreenHandlerType<SteamerScreenHandler> STEAMER_SCREEN_HANDLER;

    public static void init() {}

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(TeaTime.MOD_ID, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    static {
        STEAMER_SCREEN_HANDLER = register("steamer", SteamerScreenHandler::new);
    }

}
