package gov.nasa.arc.verve.common.ardor3d.interact;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.ZBufferState.TestFunction;


public class VerveWidget {
    static final ZBufferState alwaysZState;
    
    static {
        alwaysZState = new ZBufferState();
        alwaysZState.setFunction(TestFunction.Always);
        alwaysZState.setWritable(false);
    }
    
    static ColorRGBA alwaysZColor(ReadOnlyColorRGBA color, ColorRGBA retVal) {
        retVal.set(color);
        retVal.setAlpha(color.getAlpha()*0.3f);
        return retVal;
    }
    


}
