package ml.pluto7073.teatime.block.entity;

import ml.pluto7073.pdapi.block.entity.MugBlockEntity;
import ml.pluto7073.teatime.TTRegistries;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.teatypes.TeaType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TeaMugBlockEntity extends MugBlockEntity {

    private Holder<TeaType> type;

    public TeaMugBlockEntity(BlockPos pos, BlockState blockState) {
        super(TTBlockEntityTypes.TEA_MUG, pos, blockState);
        type = TeaType.EMPTY;
    }

    @Override
    public void saveAdditionalToItemTag(ItemStack stack) {
        super.saveAdditionalToItemTag(stack);
        stack.set(TTComponents.TEA_TYPE, type);
    }

    public void setTeaType(Holder<TeaType> type) {
        this.type = type;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        type.unwrapKey().ifPresent(key ->
                nbt.putString("TeaType", key.location().toString()));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        type = tag.contains("TeaType") ?
                provider.lookupOrThrow(TTRegistries.TEA_TYPE)
                        .getOrThrow(ResourceKey.create(TTRegistries.TEA_TYPE, ResourceLocation.parse(tag.getString("TeaType")))) :
                TeaType.EMPTY;
    }

    @Override
    public void loadFromItem(ItemStack stack) {
        super.loadFromItem(stack);
        type = stack.getOrDefault(TTComponents.TEA_TYPE, TeaType.EMPTY);
    }
}
