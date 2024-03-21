package ml.pluto7073.teatime.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTags {

    public static final TagKey<Block> WORKSTATIONS = TagKey.of(RegistryKeys.BLOCK, new Identifier("c:workstations"));

}
