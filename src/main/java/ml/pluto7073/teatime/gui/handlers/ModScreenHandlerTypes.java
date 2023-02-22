package ml.pluto7073.teatime.gui.handlers;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModScreenHandlerTypes {

    public static final ScreenHandlerType<SteamerScreenHandler> STEAMER_SCREEN_HANDLER;

    public static void init() {}

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registry.SCREEN_HANDLER, new Identifier(TeaTime.MOD_ID, id), new ScreenHandlerType<>(factory));
    }

    static {
        STEAMER_SCREEN_HANDLER = register("steamer", SteamerScreenHandler::new);
    }

}
