package complexability.puremotionmusic.Instruments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import complexability.puremotionmusic.Helper.Kalman;
import complexability.puremotionmusic.R;

import static android.app.PendingIntent.getActivity;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.floor;

/**
 * Created by turbo on 5/3/2016.
 */
public abstract class OrientationInstrumentBase extends Fragment{
    protected FragmentActivity myContext;
    private static final String TAG = "InstrumentBase";
    private static final float GRAVITY = (float) 0.1;
    private static final float ACCEL_THRESHOLD = (float) 0.1;
    private static final float RAD_TO_DEG = (float) ((float) 180 / PI);
    private static final float DT = (float) 0.01;

    protected static final int ROLL = 0;
    protected static final int PITCH = 1;


    protected static final int LEFT_ROLL = 1;
    protected static final int LEFT_PITCH = 0;
    protected static final int RIGHT_ROLL = 3;
    protected static final int RIGHT_PITCH = 2;

    public static final int LEFT_X_ACCEL_LOWBYTE = 0;
    public static final int LEFT_X_ACCEL_HIGHBYTE = 1;
    public static final int LEFT_Y_ACCEL_LOWBYTE = 2;
    public static final int LEFT_Y_ACCEL_HIGHBYTE = 3;
    public static final int LEFT_Z_ACCEL_LOWBYTE = 4;
    public static final int LEFT_Z_ACCEL_HIGHBYTE = 5;

    public static final int LEFT_X_GYRO_LOWBYTE = 6;
    public static final int LEFT_X_GYRO_HIGHBYTE = 7;
    public static final int LEFT_Y_GYRO_LOWBYTE = 8;
    public static final int LEFT_Y_GYRO_HIGHBYTE = 9;
    public static final int LEFT_Z_GYRO_LOWBYTE = 10;
    public static final int LEFT_Z_GYRO_HIGHBYTE = 11;

    public static final int RIGHT_X_ACCEL_LOWBYTE = 12;
    public static final int RIGHT_X_ACCEL_HIGHBYTE = 13;
    public static final int RIGHT_Y_ACCEL_LOWBYTE = 14;
    public static final int RIGHT_Y_ACCEL_HIGHBYTE = 15;
    public static final int RIGHT_Z_ACCEL_LOWBYTE = 16;
    public static final int RIGHT_Z_ACCEL_HIGHBYTE = 17;

    public static final int RIGHT_X_GYRO_LOWBYTE = 18;
    public static final int RIGHT_X_GYRO_HIGHBYTE = 19;
    public static final int RIGHT_Y_GYRO_LOWBYTE = 20;
    public static final int RIGHT_Y_GYRO_HIGHBYTE = 21;
    public static final int RIGHT_Z_GYRO_LOWBYTE = 22;
    public static final int RIGHT_Z_GYRO_HIGHBYTE = 23;


    public static final int LEFTHAND_DOWN_TAP_BIT = 1001;
    public static final int LEFTHAND_LEFT_TAP_BIT = 1002;
    public static final int LEFTHAND_RIGHT_TAP_BIT = 1003;

    public static final int RIGHTHAND_DOWN_TAP_BIT = 2001;
    public static final int RIGHTHAND_LEFT_TAP_BIT = 2002;
    public static final int RIGHTHAND_RIGHT_TAP_BIT = 2003;

    protected static final double GYRO_TRUST = 0.90;
    protected static final double ACCEL_TRUST = 0.10;

    protected static final double CHANGING_TOL = 0.20;

    /* Variable handling rolling average*/
    static float[] right_rollingAverage_x_accel;
    static float[] right_rollingAverage_y_accel;
    static float[] right_rollingAverage_z_accel;
    static int right_current_new_item_index;

    static float[] left_rollingAverage_x_accel;
    static float[] left_rollingAverage_y_accel;
    static float[] left_rollingAverage_z_accel;
    static int     left_current_new_item_index;

    static float previousRoll;
    static float previous_right_roll;
    static float previous_right_pitch;

    static float previous_left_roll;
    static float previous_left_pitch;

    static float l_lastFx = 0;
    static float l_lastFy = 0;
    static float l_lastFz = 0;

    static float r_lastFx = 0;
    static float r_lastFy = 0;
    static float r_lastFz = 0;
    static float l_accX, l_accY, l_accZ;
    static float r_gyroX, r_gyroY, r_gyroZ;

    static float[] previous_right_accel = {0, 0 ,0};
    static float[] previous_left_accel = {0, 0 ,0};


    static float l_gyroXangle, l_gyroYangle; // Angle calculate using the gyro only
    static float l_compAngleX, l_compAngleY; // Calculated angle using a complementary filter
    static float l_kalAngleX,  l_kalAngleY; // Calculated angle using a Kalman filter


    static float lp_gyroXangle, lp_gyroYangle; // Angle calculate using the gyro only
    static float lp_compAngleX, lp_compAngleY; // Calculated angle using a complementary filter
    static float lp_kalAngleX,  lp_kalAngleY; // Calculated angle using a Kalman filter

    static float r_gyroXangle, r_gyroYangle; // Angle calculate using the gyro only
    static float r_compAngleX, r_compAngleY; // Calculated angle using a complementary filter
    static float r_kalAngleX, r_kalAngleY; // Calculated angle using a Kalman filter


    static float rp_gyroXangle, rp_gyroYangle; // Angle calculate using the gyro only
    static float rp_compAngleX, rp_compAngleY; // Calculated angle using a complementary filter
    static float rp_kalAngleX, rp_kalAngleY; // Calculated angle using a Kalman filter

    Kalman kalmanX = new Kalman();
    Kalman kalmanY = new Kalman();

    Kalman kalmanX_forPitch = new Kalman();
    Kalman kalmanY_forPitch = new Kalman();

    protected static float[] rightMotion = new float[2];
    protected static float[] leftMotion = new float[2];

    public OrientationInstrumentBase(){

        right_rollingAverage_x_accel = new float[10];
        right_rollingAverage_y_accel = new float[10];
        right_rollingAverage_z_accel = new float[10];
        left_rollingAverage_x_accel = new float[10];
        left_rollingAverage_y_accel = new float[10];
        left_rollingAverage_z_accel = new float[10];
        for(int i = 0 ; i < 10 ; i++){
            right_rollingAverage_x_accel[i] = 0;
            right_rollingAverage_y_accel[i] = 0;
            right_rollingAverage_z_accel[i] = 0;

            left_rollingAverage_x_accel[i] = 0;
            left_rollingAverage_y_accel[i] = 0;
            left_rollingAverage_z_accel[i] = 0;
        }
    }
    protected String getStringFromId(int id) {
        switch (id) {
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

    protected float concat(byte lowByte, byte highByte) {
        //Log.d(TAG, "concat: " + Integer.toString((highByte << 8) | lowByte));(highByte << 8) | lowByte;
        int value = ((highByte << 8) + lowByte) & 0xFFFF;
        if (value > pow(2, 15)) {
            value = (int) (value - pow(2, 16));
        }
        float ret;
        ret = (float) (value / (pow(2.0, 15)));
        ret = (float) (ret * 8.0);
        return ret;
    }

    protected float concatGyro(byte lowByte, byte highByte) {
        //Log.d(TAG, "concat: " + Integer.toString((highByte << 8) | lowByte));(highByte << 8) | lowByte;
        int value = ((highByte << 8) + lowByte) & 0xFFFF;
        if (value > pow(2, 15)) {
            value = (int) (value - pow(2, 16));
        }
        float ret;
        ret = (float) (value / (pow(2.0, 15)));
        ret = (float) (ret * 500.0);
        return ret;
    }

    public void init_right(float x_accel, float y_accel, float z_accel) {
        double roll = atan2(y_accel, z_accel) * RAD_TO_DEG;
        double pitch = atan(-x_accel / sqrt(y_accel * y_accel + y_accel * y_accel)) * RAD_TO_DEG;


        kalmanX.setAngle((float) roll); // Set starting angle
        kalmanY.setAngle((float) pitch);
        r_gyroXangle = (float) roll;
        r_gyroYangle = (float) pitch;
        r_compAngleX = (float) roll;
        r_compAngleY = (float) pitch;

    }

    public float[] calculateRightKalmanPitchRollForCheckOff(float x_accel, float y_accel, float z_accel, float x_gyro, float y_gyro, float z_gyro) {
        float ret[] = new float[2];
        double roll;
        double pitch;

        double gyroXrate = x_gyro; // Convert to deg/s
        double gyroYrate = y_gyro; // Convert to deg/s

        //This fixes the transition problem when the accelerometer angle jumps between -180 and 180 degrees


        if ((x_accel) >= 0.98) {
             /*For 180deg roll */
            ret[ROLL] = previousRoll;
            ret[PITCH] = -90;
        }
        //else if(abs(x_accel) <= 1.00){
        //}
        else if (x_accel <= -0.98) {
            ret[ROLL] = previousRoll;
            ret[PITCH] = 90;
        } else if (z_accel < 0.0 && x_accel < 0.02) {
            roll = atan2(y_accel, z_accel) * RAD_TO_DEG;
            pitch = atan(-x_accel / sqrt(y_accel * y_accel + z_accel * z_accel)) * RAD_TO_DEG;
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

            r_compAngleX = (float) (GYRO_TRUST * (r_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            r_compAngleY = (float) (GYRO_TRUST * (r_compAngleY + gyroYrate * DT) + ACCEL_THRESHOLD * pitch);

            // Reset the gyro angle when it has drifted too much
            if (r_gyroXangle < -180 || r_gyroXangle > 180)
                r_gyroXangle = r_kalAngleX;
            if (r_gyroYangle < -180 || r_gyroYangle > 180)
                r_gyroYangle = r_kalAngleY;

            ret[0] = r_compAngleX;
            ret[1] = 180 - r_compAngleY;
            previousRoll = ret[0];
        } else if (z_accel < 0.0 && x_accel >= 0.02) {
            roll = atan2(y_accel, z_accel) * RAD_TO_DEG;
            pitch = atan(-x_accel / sqrt(y_accel * y_accel + z_accel * z_accel)) * RAD_TO_DEG;
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

            r_compAngleX = (float) (GYRO_TRUST * (r_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            r_compAngleY = (float) (GYRO_TRUST * (r_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);

            // Reset the gyro angle when it has drifted too much
            if (r_gyroXangle < -180 || r_gyroXangle > 180)
                r_gyroXangle = r_kalAngleX;
            if (r_gyroYangle < -180 || r_gyroYangle > 180)
                r_gyroYangle = r_kalAngleY;

            ret[0] = r_compAngleX;
            ret[1] = -180 - r_compAngleY;
            previousRoll = ret[0];
        } else {
            roll = atan2(y_accel, z_accel) * RAD_TO_DEG;
            pitch = atan(-x_accel / sqrt(y_accel * y_accel + z_accel * z_accel)) * RAD_TO_DEG;
            if ((roll < -90 && r_kalAngleX > 90) || (roll > 90 && r_kalAngleX < -90)) {
                kalmanX.setAngle((float) roll);
                r_compAngleX = (float) roll;
                r_kalAngleX = (float) roll;
                r_gyroXangle = (float) roll;
            } else {
                r_kalAngleX = kalmanX.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter
            }

            if (abs(r_kalAngleX) > 90)
                gyroYrate = -gyroYrate; // Invert rate, so it fits the restriced accelerometer reading
            r_kalAngleY = kalmanY.getAngle(pitch, gyroYrate, DT);
            //************************************************************************************************/
            r_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            r_gyroYangle += gyroYrate * DT;
            //gyroXangle += kalmanX.getRate() * dt; // Calculate gyro angle using the unbiased rate
            //gyroYangle += kalmanY.getRate() * dt;

            r_compAngleX = (float) (GYRO_TRUST * (r_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            r_compAngleY = (float) (GYRO_TRUST * (r_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);

            // Reset the gyro angle when it has drifted too much
            if (r_gyroXangle < -180 || r_gyroXangle > 180)
                r_gyroXangle = r_kalAngleX;
            if (r_gyroYangle < -180 || r_gyroYangle > 180)
                r_gyroYangle = r_kalAngleY;

            ret[0] = r_compAngleX;
            ret[1] = r_compAngleY;
            previousRoll = ret[0];
            //}

        }
        /**
         * if x_accel == 1
         *      pitch = 90
         * else if x_accel == -1
         *      pitch = -90
         * else if z>=0
         *      skjdflashdfsa
         * else if z<0
         *
         */
        return ret;

    }

    protected void changeInstrumentByName(String name) {
        Fragment fragment = null;
        switch (name) {
            case "ReverbFragment":
                fragment = new ReverbFragment();
                break;
            case "SineWaveFragment":
                fragment = new SineWave();
                break;
            default:
                return;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager(); // For AppCompat use getSupportFragmentManager
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.container, fragment).commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void instrumentFragmentInteraction(String string);
    }

    public boolean getBit(byte data, int pos) {
        byte info = (byte) ((data >> pos) & 1);
        if (info != 0) {
            return true;
        } else {
            return false;
        }

    }

    public float[] calculateRightHandKalmanPitchRollForCheckOff(float x_accel, float y_accel, float z_accel, float x_gyro, float y_gyro, float z_gyro) {
        float ret[] = new float[2];
        double roll;
        double pitch;

        double gyroXrate = x_gyro; // Convert to deg/s
        double gyroYrate = y_gyro; // Convert to deg/s
        float[] accelData = getRightRollingAveragedAccel(x_accel,y_accel,z_accel);

        boolean[] isItGoingUp = isRightAccelIncreasing(x_accel, y_accel, z_accel);
        boolean[] isItChanging = isRightAccelChanging(accelData[0], accelData[1], accelData[2]);

        //if (isItGoingUp[0] && !isItChanging[1] && !isItGoingUp[2]) { //Pitch up ==> x increase, y stay, z decrease



        if(!isItChanging[0] && abs(accelData[1])<0.05 && abs(1+accelData[2])<0.1){
            ret[ROLL] = (float) 180.0;
            ret[PITCH] = 0;
        }
        //Pitch upward
        else if(accelData[0] < 0.05 && abs(accelData[1])<0.1 && accelData[2] < 0.05 ){ //x negative, y not changing, z negative
            //roll = atan(y_accel / sqrt(x_accel * x_accel + z_accel * z_acel)) * RAD_TO_DEG;
            //pitch = atan2(-x_accel, z_accel) * RAD_TO_DEG;
            roll = atan(accelData[1] / sqrt(accelData[0] * accelData[0] + accelData[2] * accelData[2])) * RAD_TO_DEG;
            pitch = atan2(-accelData[0], accelData[2]) * RAD_TO_DEG;


            if ((pitch < -90 && rp_kalAngleY > 90) || (pitch > 90 && rp_kalAngleY < -90)) {
                kalmanY_forPitch.setAngle((float) pitch);
                rp_compAngleY = (float) pitch;
                rp_kalAngleY = (float) pitch;
                rp_gyroYangle = (float) pitch;
            } else {
                rp_kalAngleY = kalmanY_forPitch.getAngle(pitch, gyroYrate, DT); // Calculate the angle using a Kalman filter
            }
            if (abs(rp_kalAngleY) > 90) {
                gyroXrate = -gyroXrate; // Invert rate, so it fits the restriced accelerometer reading
            }

            rp_kalAngleX = kalmanX_forPitch.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter


            rp_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            rp_gyroYangle += gyroYrate * DT;

            rp_compAngleX = (float) (GYRO_TRUST * (rp_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            rp_compAngleY = (float) (GYRO_TRUST * (rp_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);

            if (rp_gyroXangle < -180 || rp_gyroXangle > 180) {
                rp_gyroXangle = rp_kalAngleX;
            }
            if (rp_gyroYangle < -180 || rp_gyroYangle > 180) {
                rp_gyroYangle = rp_kalAngleY;
            }
            ret[0] = rp_compAngleX;
            ret[1] = rp_compAngleY;
        }
        //pitch downward
        else if(accelData[0] >= 0.05 && abs(accelData[1])<0.1 && accelData[2] <0.05 ){ //x negative, y not changing, z negative

            roll = atan(accelData[1] / sqrt(accelData[0] * accelData[0] + accelData[2] * accelData[2])) * RAD_TO_DEG;
            pitch = atan2(-accelData[0], accelData[2]) * RAD_TO_DEG;


            if ((pitch < -90 && rp_kalAngleY > 90) || (pitch > 90 && rp_kalAngleY < -90)) {
                kalmanY_forPitch.setAngle((float) pitch);
                rp_compAngleY = (float) pitch;
                rp_kalAngleY = (float) pitch;
                rp_gyroYangle = (float) pitch;
            } else
                rp_kalAngleY = kalmanY_forPitch.getAngle(pitch, gyroYrate, DT); // Calculate the angle using a Kalman filter

            if (abs(rp_kalAngleY) > 90)
                gyroXrate = -gyroXrate; // Invert rate, so it fits the restriced accelerometer reading
            rp_kalAngleX = kalmanX_forPitch.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter


            rp_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            rp_gyroYangle += gyroYrate * DT;

            rp_compAngleX = (float) (GYRO_TRUST * (rp_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            rp_compAngleY = (float) (GYRO_TRUST * (rp_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);

            if (rp_gyroXangle < -180 || rp_gyroXangle > 180)
                rp_gyroXangle = rp_kalAngleX;
            if (rp_gyroYangle < -180 || rp_gyroYangle > 180)
                rp_gyroYangle = rp_kalAngleY;
            ret[0] = rp_compAngleX;
            ret[1] = rp_compAngleY;
        }
        //Roll L-R
        else {
            roll = atan2(accelData[1], accelData[2]) * RAD_TO_DEG;
            pitch = atan(-accelData[0] / sqrt(accelData[1] * accelData[1] + accelData[2] * accelData[2])) * RAD_TO_DEG;
            if ((roll < -90 && r_kalAngleX > 90) || (roll > 90 && r_kalAngleX < -90)) {
                kalmanX.setAngle((float) roll);
                r_compAngleX = (float) roll;
                r_kalAngleX = (float) roll;
                r_gyroXangle = (float) roll;
            } else {
                r_kalAngleX = kalmanX.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter
            }
            if (abs(r_kalAngleX) > 90)
                gyroYrate = -gyroYrate; // Invert rate, so it fits the restriced accelerometer reading
            r_kalAngleY = kalmanY.getAngle(pitch, gyroYrate, DT);
            //************************************************************************************************/
            r_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            r_gyroYangle += gyroYrate * DT;
            //gyroXangle += kalmanX.getRate() * dt; // Calculate gyro angle using the unbiased rate
            //gyroYangle += kalmanY.getRate() * dt;
            r_compAngleX = (float) (GYRO_TRUST * (r_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            r_compAngleY = (float) (GYRO_TRUST * (r_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);
            // Reset the gyro angle when it has drifted too much
            if (r_gyroXangle < -180 || r_gyroXangle > 180)
                r_gyroXangle = r_kalAngleX;
            if (r_gyroYangle < -180 || r_gyroYangle > 180)
                r_gyroYangle = r_kalAngleY;
            ret[0] = r_compAngleX;
            ret[1] = r_compAngleY;
        }
        if(abs(previous_right_pitch - ret[PITCH]) >= 40){
            ret[PITCH] = (float) ((previous_right_pitch + ret[PITCH])/2.0);
        }
        if(abs(previous_right_roll - ret[ROLL]) >= 40){
            ret[ROLL] = (float) ((previous_right_roll+ret[ROLL])/2.0);
        }

        previous_right_roll = ret[0];
        previous_right_pitch = ret[1];
        return ret;
    }
    public float[] calculateLeftHandKalmanPitchRollForCheckOff(float x_accel, float y_accel, float z_accel, float x_gyro, float y_gyro, float z_gyro) {
        float ret[] = new float[2];
        double roll;
        double pitch;

        double gyroXrate = x_gyro; // Convert to deg/s
        double gyroYrate = y_gyro; // Convert to deg/s
        float[] accelData = getLeftRollingAveragedAccel(x_accel,y_accel,z_accel);

        boolean[] isItGoingUp = isLeftAccelIncreasing(x_accel, y_accel, z_accel);
        boolean[] isItChanging = isLeftAccelChanging(accelData[0], accelData[1], accelData[2]);

        //if (isItGoingUp[0] && !isItChanging[1] && !isItGoingUp[2]) { //Pitch up ==> x increase, y stay, z decrease



        if(!isItChanging[0] && abs(accelData[1])<0.05 && abs(1+accelData[2])<0.1){
            ret[ROLL] = (float) 180.0;
            ret[PITCH] = 0;
        }
        //Pitch upward
        else if(accelData[0] < 0.05 && abs(accelData[1])<0.1 && accelData[2] < 0.05 ){ //x negative, y not changing, z negative
            //roll = atan(y_accel / sqrt(x_accel * x_accel + z_accel * z_acel)) * RAD_TO_DEG;
            //pitch = atan2(-x_accel, z_accel) * RAD_TO_DEG;
            roll = atan(accelData[1] / sqrt(accelData[0] * accelData[0] + accelData[2] * accelData[2])) * RAD_TO_DEG;
            pitch = atan2(-accelData[0], accelData[2]) * RAD_TO_DEG;


            if ((pitch < -90 && lp_kalAngleY > 90) || (pitch > 90 && lp_kalAngleY < -90)) {
                kalmanY_forPitch.setAngle((float) pitch);
                lp_compAngleY = (float) pitch;
                lp_kalAngleY = (float) pitch;
                lp_gyroYangle = (float) pitch;
            } else {
                lp_kalAngleY = kalmanY_forPitch.getAngle(pitch, gyroYrate, DT); // Calculate the angle using a Kalman filter
            }
            if (abs(lp_kalAngleY) > 90) {
                gyroXrate = -gyroXrate; // Invert rate, so it fits the restriced accelerometer reading
            }

            lp_kalAngleX = kalmanX_forPitch.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter


            lp_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            lp_gyroYangle += gyroYrate * DT;

            lp_compAngleX = (float) (GYRO_TRUST * (lp_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            lp_compAngleY = (float) (GYRO_TRUST * (lp_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);

            if (lp_gyroXangle < -180 || lp_gyroXangle > 180) {
                lp_gyroXangle = lp_kalAngleX;
            }
            if (lp_gyroYangle < -180 || lp_gyroYangle > 180) {
                lp_gyroYangle = lp_kalAngleY;
            }
            ret[0] = lp_compAngleX;
            ret[1] = lp_compAngleY;
        }
        //pitch downward
        else if(accelData[0] >= 0.05 && abs(accelData[1])<0.1 && accelData[2] <0.05 ){ //x negative, y not changing, z negative

            roll = atan(accelData[1] / sqrt(accelData[0] * accelData[0] + accelData[2] * accelData[2])) * RAD_TO_DEG;
            pitch = atan2(-accelData[0], accelData[2]) * RAD_TO_DEG;


            if ((pitch < -90 && lp_kalAngleY > 90) || (pitch > 90 && lp_kalAngleY < -90)) {
                kalmanY_forPitch.setAngle((float) pitch);
                lp_compAngleY = (float) pitch;
                lp_kalAngleY = (float) pitch;
                lp_gyroYangle = (float) pitch;
            } else
                lp_kalAngleY = kalmanY_forPitch.getAngle(pitch, gyroYrate, DT); // Calculate the angle using a Kalman filter

            if (abs(rp_kalAngleY) > 90)
                gyroXrate = -gyroXrate; // Invert rate, so it fits the restriced accelerometer reading
            lp_kalAngleX = kalmanX_forPitch.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter


            lp_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            lp_gyroYangle += gyroYrate * DT;

            lp_compAngleX = (float) (GYRO_TRUST * (lp_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            lp_compAngleY = (float) (GYRO_TRUST * (lp_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);

            if (lp_gyroXangle < -180 || lp_gyroXangle > 180)
                lp_gyroXangle = lp_kalAngleX;
            if (lp_gyroYangle < -180 || lp_gyroYangle > 180)
                lp_gyroYangle = lp_kalAngleY;
            ret[0] = lp_compAngleX;
            ret[1] = lp_compAngleY;
        }
        //Roll L-R
        else {
            roll = atan2(accelData[1], accelData[2]) * RAD_TO_DEG;
            pitch = atan(-accelData[0] / sqrt(accelData[1] * accelData[1] + accelData[2] * accelData[2])) * RAD_TO_DEG;
            if ((roll < -90 && l_kalAngleX > 90) || (roll > 90 && l_kalAngleX < -90)) {
                kalmanX.setAngle((float) roll);
                l_compAngleX = (float) roll;
                l_kalAngleX = (float) roll;
                l_gyroXangle = (float) roll;
            } else {
                l_kalAngleX = kalmanX.getAngle(roll, gyroXrate, DT); // Calculate the angle using a Kalman filter
            }
            if (abs(l_kalAngleX) > 90)
                gyroYrate = -gyroYrate; // Invert rate, so it fits the restriced accelerometer reading
            l_kalAngleY = kalmanY.getAngle(pitch, gyroYrate, DT);
            //************************************************************************************************/
            l_gyroXangle += gyroXrate * DT; // Calculate gyro angle without any filter
            l_gyroYangle += gyroYrate * DT;
            //gyroXangle += kalmanX.getRate() * dt; // Calculate gyro angle using the unbiased rate
            //gyroYangle += kalmanY.getRate() * dt;
            l_compAngleX = (float) (GYRO_TRUST * (l_compAngleX + gyroXrate * DT) + ACCEL_TRUST * roll); // Calculate the angle using a Complimentary filter
            l_compAngleY = (float) (GYRO_TRUST * (l_compAngleY + gyroYrate * DT) + ACCEL_TRUST * pitch);
            // Reset the gyro angle when it has drifted too much
            if (l_gyroXangle < -180 || l_gyroXangle > 180)
                l_gyroXangle = l_kalAngleX;
            if (l_gyroYangle < -180 || l_gyroYangle > 180)
                l_gyroYangle = l_kalAngleY;
            ret[0] = l_compAngleX;
            ret[1] = l_compAngleY;
        }
        if(abs(previous_left_pitch - ret[PITCH]) >= 40){
            ret[PITCH] = (float) ((previous_left_pitch + ret[PITCH])/2.0);
        }
        if(abs(previous_left_roll - ret[ROLL]) >= 40){
            ret[ROLL] = (float) ((previous_left_roll+ret[ROLL])/2.0);
        }

        previous_left_roll = ret[0];
        previous_left_pitch = ret[1];
        return ret;
    }

    public boolean[] isRightAccelIncreasing(float cur_x_accel, float cur_y_accel, float cur_z_accel){
        boolean[] ret = new boolean[3];
        ret[0] = false;
        ret[1] = false;
        ret[2] = false;
        if(previous_right_accel[0] - cur_x_accel < previous_right_accel[0]){
            ret[0] = true;
        }
        if(previous_right_accel[1] - cur_y_accel < previous_right_accel[1]){
            ret[1] = true;
        }
        if(previous_right_accel[2] - cur_z_accel < previous_right_accel[2]){
            ret[2] = true;
        }
        return ret;
    }
    public boolean[] isRightAccelChanging(float cur_x_accel, float cur_y_accel, float cur_z_accel){
        boolean[] ret = new boolean[3];
        ret[0] = false;
        ret[1] = false;
        ret[2] = false;
        if(abs(previous_right_accel[0] - cur_x_accel) >= CHANGING_TOL){
            ret[0] = true;
        }
        if(abs(previous_right_accel[1] - cur_y_accel) >=CHANGING_TOL){
            ret[1] = true;
        }
        if(abs(previous_right_accel[2] - cur_z_accel) >=CHANGING_TOL){
            ret[2] = true;
        }
        return ret;
    }

    private float[] getRightRollingAveragedAccel(float new_x_accel, float new_y_accel, float new_z_accel){
        right_rollingAverage_x_accel[right_current_new_item_index] = new_x_accel;
        right_rollingAverage_y_accel[right_current_new_item_index] = new_y_accel;
        right_rollingAverage_z_accel[right_current_new_item_index] = new_z_accel;
        float[] ret = new float[3];
        float[] sum = new float[3];
        sum[0] = 0;
        sum[1] = 0;
        sum[2] = 0;
        right_current_new_item_index++;
        if(right_current_new_item_index == 10){
            right_current_new_item_index = 0;
        }

        for(int i = 0 ; i < 10 ; i++){
            sum[0] = right_rollingAverage_x_accel[i]+sum[0];
            sum[1] = right_rollingAverage_y_accel[i]+sum[1];
            sum[2] = right_rollingAverage_z_accel[i]+sum[2];

        }

        ret[0] = (float) (sum[0]/10.0);
        ret[1] = (float) (sum[1]/10.0);
        ret[2] = (float) (sum[2]/10.0);
        return ret;
    }

    public boolean[] isLeftAccelIncreasing(float cur_x_accel, float cur_y_accel, float cur_z_accel){
        boolean[] ret = new boolean[3];
        ret[0] = false;
        ret[1] = false;
        ret[2] = false;
        if(previous_left_accel[0] - cur_x_accel < previous_left_accel[0]){
            ret[0] = true;
        }
        if(previous_left_accel[1] - cur_y_accel < previous_left_accel[1]){
            ret[1] = true;
        }
        if(previous_left_accel[2] - cur_z_accel < previous_left_accel[2]){
            ret[2] = true;
        }
        return ret;
    }
    public boolean[] isLeftAccelChanging(float cur_x_accel, float cur_y_accel, float cur_z_accel){
        boolean[] ret = new boolean[3];
        ret[0] = false;
        ret[1] = false;
        ret[2] = false;
        if(abs(previous_left_accel[0] - cur_x_accel) >= CHANGING_TOL){
            ret[0] = true;
        }
        if(abs(previous_left_accel[1] - cur_y_accel) >=CHANGING_TOL){
            ret[1] = true;
        }
        if(abs(previous_left_accel[2] - cur_z_accel) >=CHANGING_TOL){
            ret[2] = true;
        }
        return ret;
    }

    private float[] getLeftRollingAveragedAccel(float new_x_accel, float new_y_accel, float new_z_accel){
        left_rollingAverage_x_accel[left_current_new_item_index] = new_x_accel;
        left_rollingAverage_y_accel[left_current_new_item_index] = new_y_accel;
        left_rollingAverage_z_accel[left_current_new_item_index] = new_z_accel;
        float[] ret = new float[3];
        float[] sum = new float[3];
        sum[0] = 0;
        sum[1] = 0;
        sum[2] = 0;
        left_current_new_item_index++;
        if(left_current_new_item_index == 10){
            left_current_new_item_index = 0;
        }

        for(int i = 0 ; i < 10 ; i++){
            sum[0] = left_rollingAverage_x_accel[i]+sum[0];
            sum[1] = left_rollingAverage_y_accel[i]+sum[1];
            sum[2] = left_rollingAverage_z_accel[i]+sum[2];

        }

        ret[0] = (float) (sum[0]/10.0);
        ret[1] = (float) (sum[1]/10.0);
        ret[2] = (float) (sum[2]/10.0);
        return ret;
    }
    protected void tapDetection(byte status){
        //Left hand left tap
        if(getBit(status, 0)){

        }
        //Left Hand
        if(getBit(status, 1)){

        }
        //Right hand right tap
        if(getBit(status, 2)){
            Log.d("InstrumentBase", "Right hand right tap");
            changeToNextInstrument();
        }
        //Right hand Down tap
        if(getBit(status, 3)){
            togglePlaying();
            Log.d("InstrumentBase", "Right hand Down tap");
        }
        //Right hand left tap
        if(getBit(status, 4)){
            Log.d("InstrumentBase", "Right hand left tap");
            changeToPrevInstrument();
        }
        //Left hand right tap
        if(getBit(status, 5)){
            Log.d("InstrumentBase", "Left hand right tap");
            changeToNextInstrument();
        }
        //Left hand down tap
        if(getBit(status, 6)){
            Log.d("InstrumentBase", "Left hand down tap");
            togglePlaying();
        }
        //Left hand left tap
        if(getBit(status, 7)){
            Log.d("InstrumentBase", "Left hand left tap");
            changeToPrevInstrument();
        }

    }

    protected abstract void changeToNextInstrument();
    protected abstract void changeToPrevInstrument();

    protected abstract void startPlaying();
    protected abstract void stopPlaying();
    protected abstract void togglePlaying();
}
