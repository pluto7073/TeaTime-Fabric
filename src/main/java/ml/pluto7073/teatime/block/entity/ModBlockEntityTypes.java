package ml.pluto7073.teatime.block.entity;

import com.mojang.datafixers.types.Type;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityTypes {

    public static final BlockEntityType<SteamerBlockEntity> STEAMER_TYPE;

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(TeaTime.MOD_ID, id), builder.build(null));
    }

    static {
        STEAMER_TYPE = create("steamer", BlockEntityType.Builder.of(SteamerBlockEntity::new, ModBlocks.STEAMER));
    }

    public static void init() {}

}
