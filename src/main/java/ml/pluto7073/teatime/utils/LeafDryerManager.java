package ml.pluto7073.teatime.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LeafDryerManager {

    private long targetTime;
    private long lastDelayTick = 0L;

    public LeafDryerManager(World world) {
        this(world.getTime() + TeaTimeUtils.MAX_TIME_DRYING);
        lastDelayTick = world.getTime();
    }

    public LeafDryerManager(long targetTime) {
        this.targetTime = targetTime;
    }

    public void testDelayTime(BlockPos pos, World world) {
        if ((!world.isSkyVisible(pos) || world.isNight()) && world.getTime() != lastDelayTick) {
            targetTime += 1;
            lastDelayTick = world.getTime();
        }
    }

    public void testRain(BlockPos pos, World world) {
        if (world.hasRain(pos)) {
            targetTime = world.getTime() + TeaTimeUtils.MAX_TIME_DRYING;
        }
    }

    public boolean shouldReplace(World world) {
        return world.getTime() >= targetTime;
    }

}
