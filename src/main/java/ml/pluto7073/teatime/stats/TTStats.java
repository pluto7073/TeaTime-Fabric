package ml.pluto7073.teatime.stats;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class TTStats {

    public static Identifier DRINK_TEA = TeaTime.asId("drink_tea");

    public static void init() {
        Registry.register(Registries.CUSTOM_STAT, DRINK_TEA, DRINK_TEA);
        Stats.CUSTOM.getOrCreateStat(DRINK_TEA, StatFormatter.DEFAULT);
    }

}
