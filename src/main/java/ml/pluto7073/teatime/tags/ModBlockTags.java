package ml.pluto7073.teatime.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockTags {

    public static final TagKey<Block> WORKSTATIONS = TagKey.of(Registry.BLOCK_KEY, new Identifier("c:workstations"));

}
