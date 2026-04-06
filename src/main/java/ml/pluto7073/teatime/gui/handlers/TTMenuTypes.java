package ml.pluto7073.teatime.gui.handlers;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class TTMenuTypes {

    public static final MenuType<SteamerMenu> STEAMER = register("steamer", SteamerMenu::new);
    public static final MenuType<TeaKettleMenu> TEA_KETTLE = register("tea_kettle", TeaKettleMenu::new);

    public static void init() {}

    private static <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuType.MenuSupplier<T> factory) {
        return Registry.register(BuiltInRegistries.MENU, new ResourceLocation(TeaTime.MOD_ID, id), new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }

}
