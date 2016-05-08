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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import complexability.puremotionmusic.Helper.AccelBaseInstrument;
import complexability.puremotionmusic.MainActivity;
import complexability.puremotionmusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LightSaberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LightSaberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LightSaberFragment extends AccelBaseInstrument implements SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    private static final String TAG = "LightSaberFragment";
    private PdService pdService = null;
    ToggleButton onOffButton;
    Button button, button2;
    BluetoothSPP bt;

    public LightSaberFragment() {
        // Required empty public constructor
    }

    public static LightSaberFragment newInstance(String param1, String param2) {
        LightSaberFragment fragment = new LightSaberFragment();

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
        final View view = inflater.inflate(R.layout.fragment_light_saber, container, false);
        onOffButton = (ToggleButton) view.findViewById(R.id.onOffButton);
        button = (Button) view.findViewById(R.id.button);
        button2 = (Button) view.findViewById(R.id.button2);

        onOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                startAudio();
                //PdBase.sendFloat("init_vars", val);
                //PdBase.sendFloat("bpm", (float) 70.0);
                PdBase.sendFloat("onOff", val);
                PdBase.sendFloat("init_vars", val);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdBase.sendFloat("hum",301);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdBase.sendFloat("slash",301);
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
    private void initPd() throws IOException {
        Resources res = getResources();
        Log.d(TAG, "initpd");
        File patchFile = null;
        //int sampleRate = AudioParameters.suggestSampleRate();
        int sampleRate = 22050;
        Log.d(this.TAG, "sample rate: " + Integer.toString(sampleRate));
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        try {
            File dir = getActivity().getApplicationContext().getFilesDir();
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.saber3),dir,true);
            File pdPatch = new File(dir, "saber3.pd");
            PdBase.openPatch(pdPatch);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            //finish();
        } finally {
            if (patchFile != null) patchFile.delete();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(pdService.isRunning()) {
            stopAudio();
        }
        bt.setOnDataReceivedListener(null);
        ((MainActivity) getActivity()).cleanUpBluetoothListener();

        if(pdService.isRunning()){
            stopAudio();
        }
        cleanup();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        if(pdService!=null){
            if(pdService.isRunning()){
                stopAudio();
            }
        }

        cleanup();
        bt.resetOnDataReceivedListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(pdService != null) {
            if (pdService.isRunning()) {
                stopAudio();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    public void dataProc(byte[] data) {
        float l_x_accel = concat(data[LEFT_X_ACCEL_LOWBYTE], data[LEFT_X_ACCEL_HIGHBYTE]);
        float l_y_accel = concat(data[LEFT_Y_ACCEL_LOWBYTE], data[LEFT_Y_ACCEL_HIGHBYTE]);
        float l_z_accel = concat(data[LEFT_Z_ACCEL_LOWBYTE], data[LEFT_Z_ACCEL_HIGHBYTE]);

        float r_x_accel = concat(data[RIGHT_X_ACCEL_LOWBYTE], data[RIGHT_X_ACCEL_HIGHBYTE]);
        float r_y_accel = concat(data[RIGHT_Y_ACCEL_LOWBYTE], data[RIGHT_Y_ACCEL_HIGHBYTE]);
        float r_z_accel = concat(data[RIGHT_Z_ACCEL_LOWBYTE], data[RIGHT_Z_ACCEL_HIGHBYTE]);

        float left_mag = findMagnitude(l_x_accel, l_y_accel, l_z_accel);
        float right_mag = findMagnitude(r_x_accel, r_y_accel, r_z_accel);

        if(left_mag > 1.5 || right_mag > 2.5){
            PdBase.sendFloat("slash", (float) 1.0);
        }
        else{
            //PdBase.sendFloat("slash", (float) 0.0);
        }
    }
    private void startAudio() {
        String name = getResources().getString(R.string.app_name);
        if(pdService==null){
            return;
        }
        try {
            pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
            pdService.startAudio(new Intent(getActivity(), AccelBaseInstrument.class), R.drawable.icon, name, "Return to " + name + ".");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
