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

/**
 * Created by turbo on 5/7/2016.
 */
public abstract class AccelBaseInstrument extends Fragment {
    //protected PdService pdService = null;
    private static final String TAG = "AccelBaseInstrument";

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


    protected abstract void changeToNextInstrument();
    protected abstract void changeToPrevInstrument();

    protected abstract void startPlaying();
    protected abstract void stopPlaying();
    protected abstract void togglePlaying();
    public abstract void dataProc();
}
