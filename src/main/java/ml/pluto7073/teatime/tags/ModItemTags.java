package ml.pluto7073.teatime.tags;

import ml.pluto7073.teatime.TeaTime;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItemTags {

    public static final TagKey<Item> TEA_LEAVES = TagKey.of(Registry.ITEM_KEY, new Identifier(TeaTime.MOD_ID, "tea_leaves"));
    public static final TagKey<Item> BREWABLE_TEA_LEAVES = TagKey.of(Registry.ITEM_KEY, new Identifier(TeaTime.MOD_ID, "brewable_tea_leaves"));

}
