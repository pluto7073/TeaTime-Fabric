package ml.pluto7073.teatime.block.entity;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.block.TTBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class TTBlockEntityTypes {

    public static final BlockEntityType<SteamerBlockEntity> STEAMER = create("steamer", Builder.of(SteamerBlockEntity::new, TTBlocks.STEAMER));
    public static final BlockEntityType<TeaKettleBlockEntity> TEA_KETTLE = create("tea_kettle", Builder.of(TeaKettleBlockEntity::new, TTBlocks.TEA_KETTLE));
    public static final BlockEntityType<TeaMugBlockEntity> TEA_MUG = create("tea_mug", Builder.of(TeaMugBlockEntity::new, TTBlocks.TEA_MUG));

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(TeaTime.MOD_ID, id), builder.build(null));
    }

    public static void init() {}

}
