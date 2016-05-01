package complexability.puremotionmusic.Helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
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

    private static final float GRAVITY = (float) 0.1;
    private static final float ACCEL_THRESHOLD = (float) 0.1;
    private static final float RAD_TO_DEG = (float) ((float) 180/PI);
    private static final float DT = (float) 0.01;

    protected static final int LEFT_ROLL = 1;
    protected static final int LEFT_PITCH = 0;
    protected static final int RIGHT_ROLL = 3;
    protected static final int RIGHT_PITCH = 2;

    public static final int LEFT_X_ACCEL_LOWBYTE   = 0;
    public static final int LEFT_X_ACCEL_HIGHBYTE  = 1;
    public static final int LEFT_Y_ACCEL_LOWBYTE   = 2;
    public static final int LEFT_Y_ACCEL_HIGHBYTE  = 3;
    public static final int LEFT_Z_ACCEL_LOWBYTE   = 4;
    public static final int LEFT_Z_ACCEL_HIGHBYTE  = 5;

    public static final int LEFT_X_GYRO_LOWBYTE    = 6;
    public static final int LEFT_X_GYRO_HIGHBYTE   = 7;
    public static final int LEFT_Y_GYRO_LOWBYTE    = 8;
    public static final int LEFT_Y_GYRO_HIGHBYTE   = 9;
    public static final int LEFT_Z_GYRO_LOWBYTE    = 10;
    public static final int LEFT_Z_GYRO_HIGHBYTE   = 11;

    public static final int RIGHT_X_ACCEL_LOWBYTE   = 12;
    public static final int RIGHT_X_ACCEL_HIGHBYTE  = 13;
    public static final int RIGHT_Y_ACCEL_LOWBYTE   = 14;
    public static final int RIGHT_Y_ACCEL_HIGHBYTE  = 15;
    public static final int RIGHT_Z_ACCEL_LOWBYTE   = 16;
    public static final int RIGHT_Z_ACCEL_HIGHBYTE  = 17;

    public static final int RIGHT_X_GYRO_LOWBYTE    = 18;
    public static final int RIGHT_X_GYRO_HIGHBYTE   = 19;
    public static final int RIGHT_Y_GYRO_LOWBYTE    = 20;
    public static final int RIGHT_Y_GYRO_HIGHBYTE   = 21;
    public static final int RIGHT_Z_GYRO_LOWBYTE    = 22;
    public static final int RIGHT_Z_GYRO_HIGHBYTE   = 23;




    static float l_lastFx = 0;
    static float l_lastFy = 0;
    static float l_lastFz = 0;

    static float r_lastFx = 0;
    static float r_lastFy = 0;
    static float r_lastFz = 0;
    static float l_accX, l_accY, l_accZ;
    static float r_gyroX, r_gyroY, r_gyroZ;

    static float r_gyroXangle, r_gyroYangle; // Angle calculate using the gyro only
    static float r_compAngleX, r_compAngleY; // Calculated angle using a complementary filter
    static float r_kalAngleX, r_kalAngleY; // Calculated angle using a Kalman filter


    static float rp_gyroXangle, rp_gyroYangle; // Angle calculate using the gyro only
    static float rp_compAngleX, rp_compAngleY; // Calculated angle using a complementary filter
    static float rp_kalAngleX,  rp_kalAngleY; // Calculated angle using a Kalman filter

    Kalman kalmanX = new Kalman();
    Kalman kalmanY = new Kalman();


    protected String getStringFromId(int id){
        switch(id){
            case 0:
                return "None";
            case 1:
                return "ECHO";
            case 2:
                return "Reverb";
            case 3:
                return "Volume";
            default:
                return null;
        }
    }

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

    protected float concatGyro(byte lowByte, byte highByte){
        //Log.d(TAG, "concat: " + Integer.toString((highByte << 8) | lowByte));(highByte << 8) | lowByte;
        int value = ((highByte << 8) + lowByte) & 0xFFFF;
        if(value > pow(2,15)){
            value = (int) (value - pow(2,16));
        }
        float ret;
        ret = (float) (value/(pow (2.0,15)));
        ret = (float) (ret*500.0);
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

    protected float testLeftRoll(float x_accel, float y_accel, float z_accel){
        float alpha = (float) 0.5;
        float fxg = (float) (x_accel *alpha +(l_lastFx * (1.0-alpha)));
        l_lastFx = fxg;
        float fyg = (float) (y_accel *alpha +(l_lastFy * (1.0-alpha)));
        l_lastFy = fyg;
        float fzg = (float) (z_accel *alpha +(l_lastFz * (1.0-alpha)));
        l_lastFz = fzg;




        float roll = (float) (atan2(fyg, fzg)*180/PI);
        return roll;
    }

    protected float testLeftPitch(float x_accel, float y_accel, float z_accel){
        float alpha = (float) 0.5;
        float fxg = (float) (x_accel *alpha +(l_lastFx * (1.0-alpha)));
        l_lastFx = fxg;
        float fyg = (float) (y_accel *alpha +(l_lastFy * (1.0-alpha)));
        l_lastFy = fyg;
        float fzg = (float) (z_accel *alpha +(l_lastFz * (1.0-alpha)));
        l_lastFz = fzg;

        float pitch = (float) (atan2(-fxg, sqrt(fyg*fyg + fzg*fzg))*180/PI);
        return pitch;
    }

    protected float testRightRoll(float x_accel, float y_accel, float z_accel){
        float alpha = (float) 0.5;
        float fxg = (float) (x_accel *alpha +(r_lastFx * (1.0-alpha)));
        r_lastFx = fxg;
        float fyg = (float) (y_accel *alpha +(r_lastFy * (1.0-alpha)));
        r_lastFy = fyg;
        float fzg = (float) (z_accel *alpha +(r_lastFz * (1.0-alpha)));
        r_lastFz = fzg;
        float roll = (float) (atan2(fyg, fzg)*180/PI);
        return roll;
    }

    protected float testRightPitch(float x_accel, float y_accel, float z_accel){
        float alpha = (float) 0.5;
        float fxg = (float) (x_accel *alpha +(r_lastFx * (1.0-alpha)));
        r_lastFx = fxg;
        float fyg = (float) (y_accel *alpha +(r_lastFy * (1.0-alpha)));
        r_lastFy = fyg;
        float fzg = (float) (z_accel *alpha +(r_lastFz * (1.0-alpha)));
        r_lastFz = fzg;

        float pitch = (float) (atan2(-fxg, sqrt(fyg*fyg + fzg*fzg))*180/PI);
        return pitch;
    }

    protected float alsoRightRoll(float x_accel, float y_accel, float z_accel){
        float alpha = (float) 0.5;
        float fxg = (float) (x_accel *alpha +(r_lastFx * (1.0-alpha)));
        r_lastFx = fxg;
        float fyg = (float) (y_accel *alpha +(r_lastFy * (1.0-alpha)));
        r_lastFy = fyg;
        float fzg = (float) (z_accel *alpha +(r_lastFz * (1.0-alpha)));
        r_lastFz = fzg;
        float roll = (float) ((float)  atan(fyg/sqrt(pow(fxg,2) + pow(fzg,2)))*180/PI);
        return roll;
    }

    protected float alsoRightPitch(float x_accel, float y_accel, float z_accel){
        float alpha = (float) 0.5;
        float fxg = (float) (x_accel *alpha +(r_lastFx * (1.0-alpha)));
        r_lastFx = fxg;
        float fyg = (float) (y_accel *alpha +(r_lastFy * (1.0-alpha)));
        r_lastFy = fyg;
        float fzg = (float) (z_accel *alpha +(r_lastFz * (1.0-alpha)));
        r_lastFz = fzg;

        float pitch = (float) ((float) atan(fxg/sqrt(pow(fyg,2) + pow(fzg,2))) *180/PI);
        return pitch;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }
    public float[] calculateRightKalmanPitchRoll(float x_accel, float y_accel, float z_accel, float x_gyro, float y_gyro, float z_gyro){
        float ret[] = new float[2];
        double roll  = atan2(y_accel, z_accel) * RAD_TO_DEG;
        double pitch = atan(-x_accel / sqrt(y_accel * y_accel + z_accel * z_accel)) * RAD_TO_DEG;

        double gyroXrate = x_gyro; // Convert to deg/s
        double gyroYrate = y_gyro; // Convert to deg/s

        // This fixes the transition problem when the accelerometer angle jumps between -180 and 180 degrees
        /*For 180deg roll */
        if ((roll < -90 && r_kalAngleX > 90) || (roll > 90 && r_kalAngleX < -90)) {
            kalmanX.setAngle((float) roll);
            r_compAngleX = (float) roll;
            r_kalAngleX = (float) roll;
            r_gyroXangle = (float) roll;
        } else
            r_kalAngleX = kalmanX.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter


        if (abs(r_kalAngleX) > 90)
            gyroYrate = -gyroYrate; // Invert rate, so it fits the restriced accelerometer reading
        r_kalAngleY = kalmanY.getAngle(pitch, gyroYrate, DT);
        //************************************************************************************************/
        r_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
        r_gyroYangle += gyroYrate * DT;
        //gyroXangle += kalmanX.getRate() * dt; // Calculate gyro angle using the unbiased rate
        //gyroYangle += kalmanY.getRate() * dt;

        r_compAngleX = (float) (0.93 * (r_compAngleX + gyroXrate * DT) + 0.07 * roll); // Calculate the angle using a Complimentary filter
        r_compAngleY = (float) (0.93 * (r_compAngleY + gyroYrate * DT) + 0.07 * pitch);

        // Reset the gyro angle when it has drifted too much
        if (r_gyroXangle < -180 || r_gyroXangle > 180)
            r_gyroXangle = r_kalAngleX;
        if (r_gyroYangle < -180 || r_gyroYangle > 180)
            r_gyroYangle = r_kalAngleY;

        ret[0] = r_compAngleX;
        ret[1] = r_compAngleY;
        return ret;
    }

    public void init_right(float x_accel, float y_accel, float z_accel){
        double roll  = atan2(y_accel, z_accel) * RAD_TO_DEG;
        double pitch = atan(-x_accel / sqrt(y_accel * y_accel + y_accel * y_accel)) * RAD_TO_DEG;


        kalmanX.setAngle((float) roll); // Set starting angle
        kalmanY.setAngle((float) pitch);
        r_gyroXangle = (float) roll;
        r_gyroYangle = (float) pitch;
        r_compAngleX = (float) roll;
        r_compAngleY = (float) pitch;

    }

}
