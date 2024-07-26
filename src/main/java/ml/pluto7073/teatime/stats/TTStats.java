package ml.pluto7073.teatime.stats;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class TTStats {

    public static ResourceLocation DRINK_TEA = TeaTime.asId("drink_tea");

    public static void init() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, DRINK_TEA, DRINK_TEA);
        Stats.CUSTOM.get(DRINK_TEA, StatFormatter.DEFAULT);
    }

}
