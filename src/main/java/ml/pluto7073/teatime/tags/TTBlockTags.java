package ml.pluto7073.teatime.tags;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TTBlockTags {

    public static final TagKey<Block> WORKSTATIONS = TagKey.create(Registries.BLOCK, new ResourceLocation("c:workstations"));
    public static final TagKey<Block> HEATING_BLOCKS = TagKey.create(Registries.BLOCK, TeaTime.asId("heating_blocks"));

}
