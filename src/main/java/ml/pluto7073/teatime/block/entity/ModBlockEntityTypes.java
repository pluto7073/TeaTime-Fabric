package ml.pluto7073.teatime.block.entity;

import com.mojang.datafixers.types.Type;
import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.block.ModBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ModBlockEntityTypes {

    public static final BlockEntityType<SteamerBlockEntity> STEAMER_TYPE;

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TeaTime.MOD_ID, id), builder.build(type));
    }

    static {
        STEAMER_TYPE = create("steamer", BlockEntityType.Builder.create(SteamerBlockEntity::new, ModBlocks.STEAMER));
    }

    public static void init() {}

}
