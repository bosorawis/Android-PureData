package complexability.puremotionmusic.Helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.floor;

/**
 * Created by Sorawis on 4/7/2016.
 */
public class InstrumentBase extends Fragment {
    protected FragmentActivity myContext;

    private static final float GRAVITY = (float) 0.05;
    private static final float ACCEL_THRESHOLD = (float) 0.1;

    protected static final int LEFT_ROLL = 1;
    protected static final int LEFT_PITCH = 0;
    protected static final int RIGHT_ROLL = 3;
    protected static final int RIGHT_PITCH = 2;

    public static final int NONE = -1;
    public static final int ECHO = 0;
    public static final int REVERB = 1;
    public static final int VOLUME = 2;

    protected String getStringFromId(int id){
        switch(id){
            case NONE:
                return "None";
            case ECHO:
                return "ECHO";
            case REVERB:
                return "Reverb";
            case VOLUME:
                return "Volume";
            default:
                return null;
        }
    }
    public String effectTomapName(String string){
        switch (string){
            case "Reverb":
                return "wet";
            default:
                return null;
        }
    }
    protected static final String[] EffectNames = {
            "Echo",
            "Reverb",
            "Volume",
    };

    protected static final String[] MappingNames = {
            "del_val",
            "wet",
            "vol",
    };

    protected float concat(byte lowByte, byte highByte){
        //Log.d(TAG, "concat: " + Integer.toString((highByte << 8) | lowByte));(highByte << 8) | lowByte;
        int value = ((highByte << 8) + lowByte) & 0xFFFF;
        if(value > pow(2,15)){
            value = (int) (value - pow(2,16));
        }
        float ret;
        ret = (float) (value/(pow (2.0,15)));
        ret = (float) (ret*8.0);
        return ret;
    }
    protected float calculatePitch( float x_accel, float y_accel, float z_accel ){
        float pitch;
        if((z_accel <= -1 + GRAVITY ) && (z_accel >= -1 - GRAVITY)){
            pitch = 180;
        }
        else if((z_accel < 0) && (x_accel >= 0)){
            pitch = (float) (180 - (180/PI)*atan2(x_accel, sqrt(pow(y_accel,2) +pow(z_accel,2))));
        }
        else if((z_accel < 0) && (x_accel<0)){
            pitch = (float) (-180 - (180/PI)*atan2(x_accel, sqrt(pow(y_accel,2) +pow(z_accel,2))));
        }
        else{
            pitch = (float) ((float)(180/PI)*atan2(x_accel, sqrt(pow(y_accel,2) +pow(z_accel,2))));
        }
        return (float) floor(pitch);
    }
    protected float calculateRoll( float x_accel, float y_accel, float z_accel ){

        float roll;
        if((z_accel <= -1 + GRAVITY ) && (z_accel >= -1 - GRAVITY)){
            roll = 180;
        }
        else if((z_accel < 0) && (y_accel >= 0)){
            roll = (float) (180 - (180/PI)*atan2(y_accel, sqrt(pow(x_accel,2) +pow(z_accel,2))));
        }
        else if((z_accel < 0) && (y_accel<0)){
            roll = (float) (-180 - (180/PI)*atan2(y_accel, sqrt(pow(x_accel,2) +pow(z_accel,2))));
        }
        else{
            roll = (float) ((float)(180/PI)*atan2(y_accel, sqrt(pow(x_accel,2) +pow(z_accel,2))));
        }
        return (float) floor(roll);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }

}
