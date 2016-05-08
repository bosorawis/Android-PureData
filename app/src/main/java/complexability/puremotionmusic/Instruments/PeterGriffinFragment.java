package complexability.puremotionmusic.Instruments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import complexability.puremotionmusic.Helper.DrawRightBall;
import complexability.puremotionmusic.Helper.InstrumentBase;
import complexability.puremotionmusic.MainActivity;
import complexability.puremotionmusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PeterGriffinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PeterGriffinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeterGriffinFragment extends InstrumentBase implements SharedPreferences.OnSharedPreferenceChangeListener {
    private OnFragmentInteractionListener mListener;
    DrawRightBall drawTheRightBall;

    private static final String TAG = "PeterGriffinFragment";
    private PdService pdService = null;
    ToggleButton onOffButton;
    BluetoothSPP bt;
    public PeterGriffinFragment() {
        // Required empty public constructor
    }

    public static PeterGriffinFragment newInstance(String param1, String param2) {
        PeterGriffinFragment fragment = new PeterGriffinFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private final ServiceConnection pdConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pdService = ((PdService.PdBinder)service).getService();
            try {
                initPd();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // this method will never be called
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peter_griffin, container, false);
        drawTheRightBall = (DrawRightBall) view.findViewById(R.id.draw_the_right_ball) ;

        onOffButton = (ToggleButton) view.findViewById(R.id.onOffButton);
        onOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                startAudio();
                PdBase.sendFloat("onOff", val);
            }
        });

        bt = ((MainActivity) getActivity()).getBt();
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                if (data.length >=24) {
                    dataProc(data);
                }
            }
        });

        AudioParameters.init(getActivity());
        PdPreferences.initPreferences(getActivity().getApplicationContext());
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        getActivity().bindService(new Intent(getActivity(), PdService.class), pdConnection, Context.BIND_AUTO_CREATE);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected void changeToNextInstrument() {

    }

    @Override
    protected void changeToPrevInstrument() {

    }

    @Override
    protected void startPlaying() {

    }

    @Override
    protected void stopPlaying() {

    }

    @Override
    protected void togglePlaying() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    protected void dataProc(byte[] data){
        if(data[24] != 0){
            tapDetection(data[24]);
        }
        float l_x_accel = concat(data[LEFT_X_ACCEL_LOWBYTE], data[LEFT_X_ACCEL_HIGHBYTE]);
        float l_y_accel = concat(data[LEFT_Y_ACCEL_LOWBYTE], data[LEFT_Y_ACCEL_HIGHBYTE]);
        float l_z_accel = concat(data[LEFT_Z_ACCEL_LOWBYTE], data[LEFT_Z_ACCEL_HIGHBYTE]);

        float r_x_accel = concat(data[RIGHT_X_ACCEL_LOWBYTE], data[RIGHT_X_ACCEL_HIGHBYTE]);
        float r_y_accel = concat(data[RIGHT_Y_ACCEL_LOWBYTE], data[RIGHT_Y_ACCEL_HIGHBYTE]);
        float r_z_accel = concat(data[RIGHT_Z_ACCEL_LOWBYTE], data[RIGHT_Z_ACCEL_HIGHBYTE]);


        float l_x_gyro = concatGyro(data[LEFT_X_GYRO_LOWBYTE], data[LEFT_X_GYRO_HIGHBYTE]);
        float l_y_gyro = concatGyro(data[LEFT_Y_GYRO_LOWBYTE], data[LEFT_Y_GYRO_HIGHBYTE]);
        float l_z_gyro = concatGyro(data[LEFT_Z_GYRO_LOWBYTE], data[LEFT_Z_GYRO_HIGHBYTE]);

        float r_x_gyro = concatGyro(data[RIGHT_X_GYRO_LOWBYTE], data[RIGHT_X_GYRO_HIGHBYTE]);
        float r_y_gyro = concatGyro(data[RIGHT_Y_GYRO_LOWBYTE], data[RIGHT_Y_GYRO_HIGHBYTE]);
        float r_z_gyro = concatGyro(data[RIGHT_Z_GYRO_LOWBYTE], data[RIGHT_Z_GYRO_HIGHBYTE]);
        rightMotion = calculateRightHandKalmanPitchRollForCheckOff(r_x_accel, r_y_accel, r_z_accel, r_x_gyro, r_y_gyro, r_z_gyro);
        leftMotion  = calculateLeftHandKalmanPitchRollForCheckOff(l_x_accel, l_y_accel, l_z_accel, l_x_gyro, l_y_gyro, l_z_gyro);

        Log.d(TAG,"RIGHTHAND ORIEN Roll: " + Integer.toString((int)rightMotion[ROLL]) + "\t Pitch: "+Integer.toString((int)rightMotion[PITCH]));


        PdBase.sendFloat("audio_speed", -rightMotion[ROLL]);
        drawTheRightBall.updateValue(rightMotion[ROLL], -rightMotion[PITCH]);

    }
    /**
     * Initialize pd with a patch file
     *
     */
    private void initPd() throws IOException {
        Resources res = getResources();
        File patchFile = null;
        int sampleRate = AudioParameters.suggestSampleRate();
        Log.d(TAG, "sample rate: " + Integer.toString(sampleRate));
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        try {
            File dir = getActivity().getApplicationContext().getFilesDir();
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.peter_griffin),dir,true);
            File pdPatch = new File(dir, "peter_test.pd");
            PdBase.openPatch(pdPatch);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            //finish();
        } finally {
            if (patchFile != null) patchFile.delete();
        }
    }

    /**
     * Start audio service
     */
    private void startAudio() {
        String name = getResources().getString(R.string.app_name);
        if(pdService==null){
            return;
        }
        try {
            pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
            pdService.startAudio(new Intent(getActivity(), ReverbFragment.class), R.drawable.icon, name, "Return to " + name + ".");
        } catch (IOException e) {
            Log.d(TAG, String.valueOf(e));
        }
    }
    private void stopAudio() {
        pdService.stopAudio();
    }
    private void cleanup() {
        try {
            getActivity().unbindService(pdConnection);
        } catch (IllegalArgumentException e) {
            // already unbound
            pdService = null;
        }
        PdBase.release();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
