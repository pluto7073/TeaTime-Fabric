package ml.pluto7073.teatime.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ItemFrameEntity;

public class TTTrackedData {

    public static final TrackedData<Integer> WITHERING_AGE;
    public static final TrackedData<Boolean> WITHERING;

    public static void init() {}

    static {
        WITHERING_AGE = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
        WITHERING = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

}
