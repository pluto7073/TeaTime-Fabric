package ml.pluto7073.teatime.networking;

import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import ml.pluto7073.teatime.networking.packets.s2c.SyncRollableRecipesRegistryS2CPacket;
import net.minecraft.resources.ResourceLocation;

public class TTPackets {

    public static final ResourceLocation ROLLABLES_LIST = SyncRollableRecipesRegistryS2CPacket.TYPE.getId();
    public static final ResourceLocation CUSTOM_TEA_TYPES_LIST = SyncCustomTeaTypesRegistererS2CPacket.TYPE.getId();

}
