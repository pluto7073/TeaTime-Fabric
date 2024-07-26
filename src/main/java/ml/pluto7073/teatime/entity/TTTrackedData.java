package ml.pluto7073.teatime.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.decoration.ItemFrame;

public class TTTrackedData {

    public static final EntityDataAccessor<Integer> WITHERING_AGE;
    public static final EntityDataAccessor<Boolean> WITHERING;

    public static void init() {}

    static {
        WITHERING_AGE = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
        WITHERING = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.BOOLEAN);
    }

}
