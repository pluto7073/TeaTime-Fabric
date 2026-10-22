package ml.pluto7073.teatime.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.teatime.TTRegistries;
import ml.pluto7073.teatime.component.TTComponents;
import ml.pluto7073.teatime.item.TTItems;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeaREI implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        List<ItemStack> bags = new ArrayList<>();
        if (Minecraft.getInstance().level == null) return;
        for (Reference<TeaType> tea : Minecraft.getInstance().level.registryAccess().registryOrThrow(TTRegistries.TEA_TYPE).holders().toList()) {
            if (Objects.equals(tea.key().location(), PDAPI.asId("herbal"))) continue;
            List<Item> ingredients = Lists.newArrayList(tea.value().getIngredients(Minecraft.getInstance().level.registryAccess()).iterator());
            ingredients.add(0, Items.STRING);
            ingredients.add(0, Items.PAPER);
            ItemStack teaBag = new ItemStack(TTItems.TEA_BAG);
            teaBag.set(TTComponents.TEA_TYPE, tea);
            bags.add(teaBag);
            DefaultCustomShapelessDisplay display = DefaultCustomShapelessDisplay.simple(
                    ingredients.stream().map(EntryIngredients::of).toList(),
                    List.of(EntryIngredients.of(teaBag)),
                    Optional.empty()
            );
            registry.add(display);
        }

        ItemStack water = new ItemStack(Items.POTION);
        water.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        registry.add(DefaultCustomShapelessDisplay.simple(
                List.of(
                        EntryIngredients.of(water),
                        EntryIngredients.ofItemStacks(bags)
                ),
                List.of(EntryIngredients.of(TTItems.TEA)),
                Optional.empty()
        ));
    }

}
