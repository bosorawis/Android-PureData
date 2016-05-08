package complexability.puremotionmusic.Helper;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import complexability.puremotionmusic.R;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by turbo on 5/7/2016.
 */
public abstract class AccelBaseInstrument extends Fragment {
    //protected PdService pdService = null;
    private static final String TAG = "AccelBaseInstrument";
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


    protected boolean getBit(byte data, int pos) {
        byte info = (byte) ((data >> pos) & 1);
        if (info != 0) {
            return true;
        } else {
            return false;
        }

    }

    protected float findMagnitude(float x, float y, float z){
        return (float) sqrt(x*x+y*y+z*z);
    }

    protected abstract void changeToNextInstrument();
    protected abstract void changeToPrevInstrument();

    protected abstract void startPlaying();
    protected abstract void stopPlaying();
    protected abstract void togglePlaying();

    public abstract void dataProc(byte[] data);
}
