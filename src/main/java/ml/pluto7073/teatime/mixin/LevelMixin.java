package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.internal.TeaTimeLevelExtensions;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class LevelMixin implements TeaTimeLevelExtensions {

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public TeaTypeManager getTeaTypeManager() {
        return TeaTimeLevelExtensions.super.getTeaTypeManager();
    }
}
