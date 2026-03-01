package ml.pluto7073.teatime.tags;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TTItemTags {

    public static final TagKey<Item> TEA_LEAVES = TagKey.create(Registries.ITEM, new ResourceLocation(TeaTime.MOD_ID, "tea_leaves"));
    public static final TagKey<Item> BREWABLE_TEA_LEAVES = TagKey.create(Registries.ITEM, new ResourceLocation(TeaTime.MOD_ID, "brewable_tea_leaves"));

}
