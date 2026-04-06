package ml.pluto7073.teatime.internal;

import ml.pluto7073.teatime.teatypes.TeaTypeManager;

public interface TeaTimeLevelExtensions {

    default TeaTypeManager getTeaTypeManager() {
        return null;
    }

}
