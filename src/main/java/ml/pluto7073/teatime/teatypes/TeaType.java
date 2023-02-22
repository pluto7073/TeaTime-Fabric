package ml.pluto7073.teatime.teatypes;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TeaType {

    public static final Map<Identifier, TeaType> TYPES = new HashMap<>();

    public static final TeaType EMPTY;
    public static final TeaType GREEN_TEA;
    public static final TeaType BLACK_TEA;

    private static TeaType register(TeaType teaType) {
        TYPES.put(new Identifier(TeaTime.MOD_ID, teaType.id), teaType);
        return teaType;
    }

    public static TeaType getFromLeaves(ItemStack stack) {
        for (Identifier i : TYPES.keySet()) {
            TeaType type = TYPES.get(i);
            if (stack.isOf(type.getLeaves())) {
                return type;
            }
        }
        return EMPTY;
    }

    public static Identifier getId(TeaType teaType) {
        for (Identifier i : TYPES.keySet()) {
            TeaType type = TYPES.get(i);
            if (type.id.equals(teaType.id)) {
                return i;
            }
        }
        return new Identifier(TeaTime.MOD_ID, "empty");
    }

    public static void init() {}

    private final Item leaves;
    private final int colour;
    private final String id;
    private final StatusEffectInstance[] effects;

    public TeaType(String id, int colour, Item leaves, StatusEffectInstance... effects) {
        this.id = id;
        this.colour = colour;
        this.leaves = leaves;
        this.effects = effects;
    }

    public Item getLeaves() {
        return leaves;
    }

    public int getColour() {
        return colour;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        Identifier id = getId(this);
        return "teatype." + id.getNamespace() + "." + id.getPath();
    }

    public StatusEffectInstance[] getEffects() {
        return effects;
    }

    static {
        EMPTY = register(new TeaType("empty", 0xFFFFFF, Items.AIR));
        GREEN_TEA = register(new TeaType("green_tea", 0xd1b849, ModItems.DRIED_TEA_LEAVES, new StatusEffectInstance(StatusEffects.HASTE, 20 * 60)));
        BLACK_TEA = register(new TeaType("black_tea", 0x4d0705, ModItems.FERMENTED_TEA_LEAVES, new StatusEffectInstance(StatusEffects.HASTE, 20 * 60),
                new StatusEffectInstance(StatusEffects.SPEED, 20 * 60)));
    }

}
