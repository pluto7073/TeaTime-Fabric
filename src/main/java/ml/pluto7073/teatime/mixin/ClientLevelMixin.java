package ml.pluto7073.teatime.mixin;

import ml.pluto7073.teatime.internal.TeaTimeLevelExtensions;
import ml.pluto7073.teatime.teatypes.TeaTypeManager;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements TeaTimeLevelExtensions {

    @Unique private final TeaTypeManager teatime$TeaTypeManager = new TeaTypeManager();

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public TeaTypeManager getTeaTypeManager() {
        return teatime$TeaTypeManager;
    }
}
