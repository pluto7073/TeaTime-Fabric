package ml.pluto7073.teatime.block.entity;

import ml.pluto7073.pdapi.block.entity.MugBlockEntity;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TeaMugBlockEntity extends MugBlockEntity {

    private ResourceKey<TeaType> type;

    public TeaMugBlockEntity(BlockPos pos, BlockState blockState) {
        super(TTBlockEntityTypes.TEA_MUG, pos, blockState);
        type = TeaTypeManager.EMPTY;
    }

    @Override
    public void saveAdditionalToItemTag(CompoundTag itemTag) {
        super.saveAdditionalToItemTag(itemTag);
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type.location().toString());
        itemTag.put("TeaData", tag);
    }

    public void setTeaType(ResourceKey<TeaType> type) {
        this.type = type;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("TeaType", type.location().toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        type = ResourceKey.create(TeaTypeManager.TEA_TYPE, new ResourceLocation(tag.getString("TeaType")));
    }

    @Override
    public void loadFromItem(ItemStack stack) {
        super.loadFromItem(stack);
        type = ResourceKey.create(TeaTypeManager.TEA_TYPE, TeaTimeUtils.getTeaTypeId(stack));
    }
}
