package ml.pluto7073.teatime;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.addition.OnDrink;
import ml.pluto7073.pdapi.addition.OnDrinkTemplate;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.teatime.block.ModBlocks;
import ml.pluto7073.teatime.block.entity.ModBlockEntityTypes;
import ml.pluto7073.teatime.entity.TTTrackedData;
import ml.pluto7073.teatime.event.CustomTeaTypesRegisterer;
import ml.pluto7073.teatime.event.ModEvents;
import ml.pluto7073.teatime.gui.handlers.TTMenuTypes;
import ml.pluto7073.teatime.item.ModItems;
import ml.pluto7073.teatime.recipe.ModRecipes;
import ml.pluto7073.teatime.stats.TTStats;
import ml.pluto7073.teatime.teatypes.TeaType;
import ml.pluto7073.teatime.teatypes.TeaTypes;
import ml.pluto7073.teatime.utils.TeaTimeUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TeaTime implements ModInitializer {

    public static final String MOD_ID = "teatime";
    public static final Logger logger = LogManager.getLogger("TeaTime");
    public static ResourceKey<CreativeModeTab> TT_GROUP;
    public static boolean PLUTOSCOFFEEMOD_LOADED = false;
    public static OnDrinkTemplate ADD_TEA_EFFECTS = (id, onDrinkData) -> {
        ResourceLocation teaId = new ResourceLocation(GsonHelper.getAsString(onDrinkData, "tea"));
        TeaType type = TeaTypes.get(teaId);
        final List<MobEffectInstance> effects = List.of(type.getEffects());
        return new OnDrink() {
            @Override
            public void onDrink(ItemStack stack, Level level, LivingEntity user) {
                effects.forEach(user::addEffect);
                if (user instanceof Player player) player.awardStat(TTStats.DRINK_TEA);
            }

            @Override
            public JsonObject toJson() {
                return onDrinkData;
            }
        };
    };

    private static TeaTime INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        PLUTOSCOFFEEMOD_LOADED = FabricLoader.getInstance().isModLoaded("plutoscoffee");
        logger.info(PLUTOSCOFFEEMOD_LOADED ? "PlutosCoffeeMod Found!" : "PlutosCoffeeMod does not appear to be loaded");
        ModBlocks.init();
        ModBlockEntityTypes.init();
        ModRecipes.init();
        ModItems.init();
        TeaTypes.init();
        TTStats.init();
        TTTrackedData.init();
        OnDrinkTemplate.register(asId("add_tea_effects"), ADD_TEA_EFFECTS);
        createItemGroup();
        registerResourceReloadListener();
        ModEvents.init();
        TTMenuTypes.init();
    }

    public static void createItemGroup() {
        TT_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, asId("tt_group"));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TT_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.TEA_LEAVES)).title(Component.translatable("itemGroup.teatime.tt_group")).build());
        ItemGroupEvents.modifyEntriesEvent(TT_GROUP).register(stacks -> {
                    stacks.accept(new ItemStack(ModItems.STEAMER));
                    stacks.accept(new ItemStack(ModItems.TEA_SEEDS));
                    stacks.accept(new ItemStack(ModItems.TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.WITHERED_TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.WHITE_TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.STEAMED_TEA_LEAVES));
                    stacks.acceptAll(TeaTimeUtils.getRolledLeaves());
                    stacks.accept(new ItemStack(ModItems.DRIED_TEA_LEAVES));
                    stacks.accept(new ItemStack(ModItems.FERMENTED_TEA_LEAVES));
                    stacks.acceptAll(TeaTimeUtils.getTeaBags());
                    stacks.acceptAll(TeaTimeUtils.getTea());
                    stacks.accept(PDItems.MILK_BOTTLE);
                    stacks.accept(PDItems.DRINK_WORKSTATION);
                });
    }

    public static TeaTime getInstance() {
        return INSTANCE;
    }

    public static void registerResourceReloadListener() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CustomTeaTypesRegisterer());
    }

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
