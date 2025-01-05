package ml.pluto7073.teatime.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeaREI implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        List<ItemStack> bags = new ArrayList<>();
        for (TeaType tea : TeaTypeManager.values()) {
            if (Objects.equals(TeaTypeManager.getId(tea), PDAPI.asId("herbal"))) continue;
            List<Item> ingredients = Lists.newArrayList(tea.getIngredients().iterator());
            ingredients.add(0, Items.STRING);
            ingredients.add(0, Items.PAPER);
            ItemStack teaBag = TeaTimeUtils.setTeaType(new ItemStack(ModItems.TEA_BAG), tea);
            bags.add(teaBag);
            DefaultCustomShapelessDisplay display = DefaultCustomShapelessDisplay.simple(
                    ingredients.stream().map(EntryIngredients::of).toList(),
                    List.of(EntryIngredients.of(teaBag)),
                    Optional.empty()
            );
            registry.add(display);
        }

        ItemStack water = new ItemStack(Items.POTION);
        PotionUtils.setPotion(water, Potions.WATER);
        registry.add(DefaultCustomShapelessDisplay.simple(
                List.of(
                        EntryIngredients.of(water),
                        EntryIngredients.ofItemStacks(bags)
                ),
                List.of(EntryIngredients.of(ModItems.TEA)),
                Optional.empty()
        ));
    }

}
