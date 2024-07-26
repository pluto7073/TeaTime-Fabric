package ml.pluto7073.teatime.block;

import ml.pluto7073.teatime.TeaTime;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {

    public static final Block TEA_SHRUB = new TeaShrub(FabricBlockSettings.create().mapColor(MapColor.COLOR_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(SoundType.CROP).pistonBehavior(PushReaction.DESTROY));
    public static final Block STEAMER = new SteamerBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_GRAY).requiresTool().strength(0.5F).nonOpaque());

    private static void register(String id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(TeaTime.MOD_ID, id), block);
    }

    public static void init() {
        register("tea_shrub", TEA_SHRUB);
        register("steamer", STEAMER);
    }

}
