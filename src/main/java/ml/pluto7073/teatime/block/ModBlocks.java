package ml.pluto7073.teatime.block;

import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ModBlocks {

    public static final Block TEA_SHRUB = new TeaShrub(FabricBlockSettings.create().mapColor(MapColor.DARK_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY));
    public static final Block STEAMER = new SteamerBlock(FabricBlockSettings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(0.5F).nonOpaque());

    private static void register(String id, Block block) {
        Registry.register(Registries.BLOCK, new Identifier(TeaTime.MOD_ID, id), block);
    }

    public static void init() {
        register("tea_shrub", TEA_SHRUB);
        register("steamer", STEAMER);
    }

}
