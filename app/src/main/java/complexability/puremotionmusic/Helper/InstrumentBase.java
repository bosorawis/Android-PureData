package complexability.puremotionmusic.Helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.atan2;

/**
 * Created by Sorawis on 4/7/2016.
 */
public class InstrumentBase extends Fragment {
    protected FragmentActivity myContext;

    private static final float GRAVITY = (float) 1.00;
    private static final float ACCEL_THRESHOLD = (float) 0.1;


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

    public static final String[] EffectNames = {
            "Echo",
            "Reverb",
            "Volume",
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
        float result = (float) atan2(x_accel, sqrt(pow(y_accel,2.0) + pow(z_accel,2.0)));
        //Log.d(TAG, "x: " + x_accel + "\ty :" + y_accel + "\t z:" + z_accel);
        //result = (float) (result*180.0/(PI));
        //return result;

        float pitch = 0;
        if(abs(x_accel) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && abs(z_accel) <= ACCEL_THRESHOLD){
            pitch = 0;
        }
        else if(abs(x_accel) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && abs(z_accel+GRAVITY)<=ACCEL_THRESHOLD){
            pitch = 180;
        }
        else if(abs(x_accel-GRAVITY) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && abs(z_accel) <= ACCEL_THRESHOLD){
            pitch = 90;
        }
        else if(abs(x_accel+GRAVITY) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && abs(z_accel) <= ACCEL_THRESHOLD){
            pitch = -90;
        }
        else if(abs(x_accel) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && abs(z_accel-GRAVITY)<=ACCEL_THRESHOLD){
            pitch = 0;
        }

        else if (abs(x_accel) <= ACCEL_THRESHOLD || abs(y_accel) <= ACCEL_THRESHOLD){
            // Positive Pitch
            if(x_accel > 0 && abs(y_accel) <= ACCEL_THRESHOLD && z_accel > ACCEL_THRESHOLD){
                if(x_accel == GRAVITY){
                    pitch = (float) (-(atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
                else{
                    pitch = (float) ((atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
            }
            else if((x_accel > 0) && abs(y_accel) <= ACCEL_THRESHOLD && z_accel < -ACCEL_THRESHOLD){
                pitch = (float) (180 - ((atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI));
            }

            // Negative Pitch
            else if(x_accel < 0 && abs(y_accel) <= ACCEL_THRESHOLD && z_accel <= 0){
                pitch = (float) (-180 - ((atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI));
            }
            else if((x_accel < 0) && abs(y_accel) <= ACCEL_THRESHOLD && z_accel > 0){
                if(x_accel == -GRAVITY){
                    pitch = (float) (-(atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
                else{
                    pitch = (float) ((atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
            }

            if((abs(x_accel) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD) && abs(z_accel+GRAVITY)<ACCEL_THRESHOLD){
                pitch = 180;
            }
        }
        else{
            pitch = (float) ((atan2(x_accel, sqrt(pow(y_accel,2) + pow(z_accel,2)))*180.0)/PI);

        }

        return pitch;
    }
    protected float calculateRoll( float x_accel, float y_accel, float z_accel ){
        float result = (float) atan2(y_accel, sqrt(pow(x_accel, 2.0) + pow(z_accel, 2.0)));
        result = (float) (result*180.0/PI);

        float roll = 0;
        if(abs(x_accel) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && abs(z_accel) <= ACCEL_THRESHOLD){
            roll = 0;
        }
        else if(abs(x_accel) <= ACCEL_THRESHOLD && abs(y_accel) <= ACCEL_THRESHOLD && z_accel == -2*GRAVITY){
            roll = 180;
        }
        else if(x_accel == GRAVITY && abs(y_accel) <= ACCEL_THRESHOLD && z_accel == -GRAVITY){
            roll = 0;
        }
        else if(x_accel == -GRAVITY && abs(y_accel) <= ACCEL_THRESHOLD && z_accel == -GRAVITY){
            roll = 0;
        }
        else if(abs(x_accel) <= ACCEL_THRESHOLD && y_accel == GRAVITY && z_accel == -GRAVITY){
            roll = 90;
        }
        else if(abs(x_accel) <= ACCEL_THRESHOLD && y_accel == -GRAVITY && z_accel == -GRAVITY){
            roll = -90;
        }

        else if (abs(x_accel) <= ACCEL_THRESHOLD || abs(y_accel) <= ACCEL_THRESHOLD){
            // Positive Roll
            if(y_accel > 0 && abs(x_accel) <= ACCEL_THRESHOLD && z_accel > -GRAVITY){
                if(y_accel == GRAVITY){
                    roll = (float) (-(atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
                else{
                    roll = (float) ((atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
            }
            else if((y_accel > 0) && abs(x_accel) <= ACCEL_THRESHOLD && z_accel <= -GRAVITY){
                roll = (float) (180 - ((atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI));
            }

            // Negative Roll
            else if(y_accel < 0 && abs(x_accel) <= ACCEL_THRESHOLD && z_accel <= -GRAVITY){
                roll = (float) (-180 - ((atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI));
            }
            else if((y_accel < 0) && abs(x_accel) <= ACCEL_THRESHOLD && z_accel > -GRAVITY){
                if(y_accel == -GRAVITY){
                    roll = (float) (-(atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
                else{
                    roll = (float) ((atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI);
                }
            }
            else if((abs(x_accel) <= 1 && abs(y_accel) <= ACCEL_THRESHOLD) && z_accel <= -GRAVITY){
                roll = 180;
            }

            else{
                roll = (float) ((atan2(y_accel, sqrt(pow(x_accel,2) + pow(z_accel,2)))*180.0)/PI);
            }
        }

        return roll;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }

}
