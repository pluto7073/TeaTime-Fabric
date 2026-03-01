package ml.pluto7073.teatime.block.entity;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.block.TTBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class ModBlockEntityTypes {

    public static final BlockEntityType<SteamerBlockEntity> STEAMER_TYPE;
    public static final BlockEntityType<SteamerBlockEntity> STEAMER = create("steamer", Builder.of(SteamerBlockEntity::new, TTBlocks.STEAMER));

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(TeaTime.MOD_ID, id), builder.build(null));
    }

    public static void init() {}

}
