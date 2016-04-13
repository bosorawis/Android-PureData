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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;

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
import java.util.Objects;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import complexability.puremotionmusic.Helper.InstrumentBase;
import complexability.puremotionmusic.Helper.Mapper;
import complexability.puremotionmusic.MainActivity;
import complexability.puremotionmusic.R;

import static java.lang.StrictMath.floor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReverbFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReverbFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReverbFragment extends InstrumentBase implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TOTAL_MOTION = 4;
    private static final int TOTAL_EFFECT = 3;

    private static final String TAG = "ReverbFragment";
    private static final int[] AVAILABLE_EFFECT = new int[]{ECHO, REVERB, VOLUME};
    private static final String[] AVAILABLE_MAPPING_NAME  = {"ech,","wet","vol", "curr_note"};
    private static final String[] AVAILABLE_EFFECT_NAME  = {"Echo,","Reverb","Volume","Frequency"};



    private static Mapper[] mapper = new Mapper[TOTAL_EFFECT];


    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;
    private PdService pdService = null;
    private int[] selected = new int[TOTAL_MOTION];
    private String[] selectedString = new String[TOTAL_MOTION];
    private String[] choices;

    float[] motionData = new float[10];

    private TextView left_pitch_text;
    private TextView left_roll_text;


    private TextView right_pitch_text;
    private TextView right_roll_text;
    //*******************************************
    BluetoothSPP bt;
    public ReverbFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReverbFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReverbFragment newInstance(String param1, String param2) {
        ReverbFragment fragment = new ReverbFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private PdReceiver receiver = new PdReceiver() {

        private void pdPost(String msg) {
            Log.d(TAG, msg);
        }

        @Override
        public void print(String s) {
            Log.d(TAG, s);
        }

        @Override
        public void receiveBang(String source) {
            //pdPost("bang");
            //Log.d(TAG, source + ": BANG!");
        }

        @Override
        public void receiveFloat(String source, float x) {
            pdPost("float: " + x);
        }

        @Override
        public void receiveList(String source, Object... args) {
            pdPost("list: " + Arrays.toString(args));
        }

        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
            pdPost("message: " + Arrays.toString(args));
        }

        @Override
        public void receiveSymbol(String source, String symbol) {
            pdPost("symbol: " + symbol);
        }
    };

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
        final View view = inflater.inflate(R.layout.fragment_reverb, container, false);

        /*
        Value Initialization
         */
        choices =  getActivity().getResources().getStringArray(R.array.reverb_effect_name);
        for (int i = 0 ; i < selectedString.length ; i++){
            //TODO Initializing
            selected[i] = -1;
            selectedString[i] = getStringFromId(selected[i]);
        }
        /*
        Initializa Mapper for mapping motions
         */
        //for (int i = 0 ; i  < TOTAL_MOTION ; i++){
        //    mapper[i] = new Mapper(-1, AVAILABLE_EFFECT_NAME[i], AVAILABLE_MAPPING_NAME[i]);
        //}

        /*
        GUI Initialization
         */

        ToggleButton onOffButton = (ToggleButton) view.findViewById(R.id.toggleButton);
        /*
        Button
         */
        Button left_pitch_btn = (Button) view.findViewById(R.id.left_pitch_button);
        Button left_roll_btn = (Button) view.findViewById(R.id.left_roll_button);
        Button right_pitch_btn = (Button) view.findViewById(R.id.right_pitch_button);
        Button right_roll_btn = (Button) view.findViewById(R.id.right_roll_button);


        left_pitch_btn.setOnClickListener(this);
        left_roll_btn.setOnClickListener(this);
        right_pitch_btn.setOnClickListener(this);
        right_roll_btn.setOnClickListener(this);

        /*
        Text boxes
         */
        left_pitch_text = (TextView) view.findViewById(R.id.left_pitch_text);
        left_roll_text = (TextView) view.findViewById(R.id.left_roll_text);
        right_pitch_text = (TextView) view.findViewById(R.id.right_pitch_text);
        right_roll_text = (TextView) view.findViewById(R.id.right_roll_text);

        initText();

        onOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                startAudio();
                PdBase.sendFloat("init_vars", val);
                PdBase.sendFloat("onOff", val);
            }
        });

        //**********************************************************************************
        /*
        Bluetooth set onlistener
         */
        bt = ((MainActivity) getActivity()).getBt();
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                if (data != null) {
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
            //mListener.onFragmentInteraction(uri);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (pdService.isRunning()) {
            startAudio();
        }
    }

    @Override
    public void onClick(View v) {
        int motion = 0;
        TextView cur = null;
        switch(v.getId()){
            case R.id.left_pitch_button:
                motion = LEFT_PITCH;
                cur = left_pitch_text;
                break;
            case R.id.left_roll_button:
                motion = LEFT_ROLL;
                cur = left_roll_text;
                break;
            case R.id.right_pitch_button:
                motion = RIGHT_PITCH;
                cur = right_pitch_text;
                break;
            case R.id.right_roll_button:
                motion = RIGHT_ROLL;
                cur = right_roll_text;
                break;
        }
        final int finalMotion = motion;

        final TextView finalCur = cur;
        new MaterialDialog.Builder(myContext)
                .title(R.string.title)
                .items(R.array.reverb_effect_name)
                .itemsCallbackSingleChoice(selected[motion], new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        selected[finalMotion] = which;
                        selectedString[finalMotion] = (String) text;
                        assert finalCur != null;
                        finalCur.setText(selectedString[finalMotion]);
                        if(which < AVAILABLE_EFFECT.length && which >= 0) {
                            Log.d(TAG, choices[which]);
                            //userSelected(finalMotion, choices[which]);
                        }
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onReverbFragmentInteraction(String string);
    }

    /**
     * Initialize pd with a patch file
     *
     */
    private void initPd() throws IOException {
        Resources res = getResources();
        File patchFile = null;
        int sampleRate = AudioParameters.suggestSampleRate();
        Log.d("PureDataFragment", "sample rate: " + Integer.toString(sampleRate));
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);

        try {
            PdBase.setReceiver(receiver);
            PdBase.subscribe("metro_bng");
            //PdBase.subscribe("android");
            InputStream in = res.openRawResource(R.raw.android_interface);
            patchFile = IoUtils.extractResource(in, "android_interface.pd", getActivity().getCacheDir());
            PdBase.openPatch(patchFile);
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


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        if(pdService.isRunning()) {
            stopAudio();
        }
        cleanup();
        bt.resetOnDataReceivedListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(pdService.isRunning()) {
            stopAudio();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void dataProc(byte[] data){
        //Log.d(TAG, String.valueOf(data));
        //For testing only
        String Param[] = {"wet"};

        float l_x_accel = concat(data[0], data[1]);
        float l_y_accel = concat(data[2], data[3]);
        float l_z_accel = concat(data[4], data[5]);

        float r_x_accel = concat(data[12], data[13]);
        float r_y_accel = concat(data[14], data[15]);
        float r_z_accel = concat(data[16], data[17]);


        motionData[LEFT_PITCH] = calculatePitch(l_x_accel, l_y_accel, l_z_accel);
        motionData[LEFT_ROLL] = calculateRoll(l_x_accel, l_y_accel, l_z_accel);
        motionData[RIGHT_PITCH] = calculatePitch(r_x_accel, r_y_accel, r_z_accel);
        motionData[RIGHT_ROLL]  = calculateRoll(r_x_accel, r_y_accel, r_z_accel);

        for(int i = 0 ; i < TOTAL_MOTION ; i++){
            Log.d(TAG, "selected[" + Integer.toString(i) +"]: " + selectedString[i]);
            switch (selectedString[i]){
                case "None":
                    break;
                case "Echo":
                    break;
                case "Reverb":
                    PdBase.sendFloat("wet", ((180+motionData[i]))/400);
                    break;
                case "Volume":
                    break;
                    //PdBase.sendFloat(mapper[VOLUME].getMapName(), motionData[i]);
                case "Frequency":
                    Log.d(TAG, "HELLO!!");
                    PdBase.sendFloat("curr_note", (float) floor((180+motionData[i])/21));
                default:
                    break;
            }
        }
    }
    public void initText(){

        left_pitch_text.setText(selectedString[LEFT_PITCH]);
        left_roll_text.setText(selectedString[LEFT_ROLL]);

        right_pitch_text.setText(selectedString[RIGHT_PITCH]);
        right_roll_text.setText(selectedString[RIGHT_ROLL]);

    }
    private void userSelected(int motionID, String item){
        if(Objects.equals(item, "None")){
            return;
        }
        for(int i = 0 ; i < mapper.length ; i++){
            if(Objects.equals(mapper[i].getEffect(), item)){
                mapper[i].setMotion(motionID);
                return;
            }
        }
    }
}
