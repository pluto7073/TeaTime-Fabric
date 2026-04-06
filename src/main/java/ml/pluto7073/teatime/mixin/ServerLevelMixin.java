package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.internal.TeaTimeLevelExtensions;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements TeaTimeLevelExtensions {

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public TeaTypeManager getTeaTypeManager() {
        return TeaTime.SERVER_TEA_TYPE_MANAGER;
    }
}
