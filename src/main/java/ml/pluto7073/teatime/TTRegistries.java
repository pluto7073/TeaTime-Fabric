package ml.pluto7073.teatime;

import ml.pluto7073.teatime.teatypes.TeaType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class TTRegistries {

    public static final ResourceKey<Registry<TeaType>> TEA_TYPE =
            ResourceKey.createRegistryKey(TeaTime.asId("tea_type"));

    public static void init() {
        DynamicRegistries.registerSynced(TEA_TYPE, TeaType.CODEC);
    }

}
