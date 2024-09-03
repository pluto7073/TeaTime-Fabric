package ml.pluto7073.teatime.action;

import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.teatime.TeaTime;
import net.minecraft.core.Registry;

public class TeaTimeActions {

    public static final OnDrinkSerializer<AddTeaEffectsAction> ADD_TEA_EFFECTS =
            Registry.register(PDRegistries.ON_DRINK_SERIALIZER, TeaTime.asId("add_tea_effects"),
                    new AddTeaEffectsAction.Serializer());

    public static void init() {}

}
