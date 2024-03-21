package ml.pluto7073.teatime.networking;

import ml.pluto7073.teatime.networking.packets.s2c.SyncCustomTeaTypesRegistererS2CPacket;
import ml.pluto7073.teatime.networking.packets.s2c.SyncRollableRecipesRegistryS2CPacket;
import net.minecraft.util.Identifier;

public class TTPackets {

    public static final Identifier ROLLABLES_LIST = SyncRollableRecipesRegistryS2CPacket.TYPE.getId();
    public static final Identifier CUSTOM_TEA_TYPES_LIST = SyncCustomTeaTypesRegistererS2CPacket.TYPE.getId();

}
