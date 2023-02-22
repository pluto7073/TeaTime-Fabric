package ml.pluto7073.teatime.block;

import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    public static final Block TEA_SHRUB = new TeaShrub(FabricBlockSettings.of(Material.PLANT)
            .noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).nonOpaque());
    public static final Block STEAMER = new SteamerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL));

    private static void register(String id, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(TeaTime.MOD_ID, id), block);
    }

    public static void init() {
        register("tea_shrub", TEA_SHRUB);
        register("steamer", STEAMER);
    }

}
