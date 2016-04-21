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

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import complexability.puremotionmusic.Helper.InstrumentBase;
import complexability.puremotionmusic.MainActivity;
import complexability.puremotionmusic.R;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.atan2;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ReverbFragment";

    private static final int[] availableEffect = new int[]{ECHO, REVERB, VOLUME};
    private static final String[] mappingName  = {"wet"};
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private PdService pdService = null;
    private int[] selected = new int[10];
    private String[] selectedString = new String[10];
    private String[] choices;

    /*
    User interface elements
    */
    private Button left_updown_btn;
    private Button left_leftright_btn;
    private Button left_forwardback_btn;
    private Button left_pitch_btn;
    private Button left_roll_btn;

    private TextView left_updown_text;
    private TextView left_leftright_text;
    private TextView left_forwardback_text;
    private TextView left_pitch_text;
    private TextView left_roll_text;


    private Button   right_updown_btn;
    private Button   right_leftright_btn;
    private Button   right_forwardback_btn;
    private Button   right_pitch_btn;
    private Button   right_roll_btn;

    private TextView right_updown_text;
    private TextView right_leftright_text;
    private TextView right_forwardback_text;
    private TextView right_pitch_text;
    private TextView right_roll_text;
    private ToggleButton onOffButton;
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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
            pdPost("bang");
            Log.d(TAG, source + ": BANG!");
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
        GUI Initialization
         */

        onOffButton = (ToggleButton) view.findViewById(R.id.toggleButton);

        left_updown_btn = (Button) view.findViewById(R.id.left_updown_button);
        left_leftright_btn = (Button) view.findViewById(R.id.left_leftright_button);
        left_forwardback_btn = (Button) view.findViewById(R.id.left_forwardback_button);
        left_pitch_btn = (Button) view.findViewById(R.id.left_pitch_button);
        left_roll_btn = (Button) view.findViewById(R.id.left_roll_button);


        right_updown_btn = (Button) view.findViewById(R.id.right_updown_button);
        right_leftright_btn = (Button) view.findViewById(R.id.right_leftright_button);
        right_forwardback_btn = (Button) view.findViewById(R.id.right_forwardback_button);
        right_pitch_btn = (Button) view.findViewById(R.id.right_pitch_button);
        right_roll_btn = (Button) view.findViewById(R.id.right_roll_button);

        left_updown_btn.setOnClickListener(this);
        left_leftright_btn.setOnClickListener(this);
        left_forwardback_btn.setOnClickListener(this);
        left_pitch_btn.setOnClickListener(this);
        left_roll_btn.setOnClickListener(this);

        right_updown_btn.setOnClickListener(this);
        right_leftright_btn.setOnClickListener(this);
        right_forwardback_btn.setOnClickListener(this);
        right_pitch_btn.setOnClickListener(this);
        right_roll_btn.setOnClickListener(this);

        left_updown_text = (TextView) view.findViewById(R.id.left_updown_text);
        left_leftright_text  = (TextView) view.findViewById(R.id.left_leftright_text);
        left_forwardback_text = (TextView) view.findViewById(R.id.left_frontback_text);
        left_pitch_text = (TextView) view.findViewById(R.id.left_pitch_text);
        left_roll_text = (TextView) view.findViewById(R.id.left_roll_text);

        right_updown_text = (TextView) view.findViewById(R.id.right_updown_text);
        right_leftright_text  = (TextView) view.findViewById(R.id.right_leftright_text);
        right_forwardback_text = (TextView) view.findViewById(R.id.right_frontback_text);
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
                //Log.d(TAG,"hello");
                //Log.d(TAG, Arrays.toString(data));
                if (data != null) {
                    //Log.d(TAG, Integer.toString(data.length));
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
        choices =  getActivity().getResources().getStringArray(R.array.reverb_effect_name);
        for (int i = 0 ; i < selectedString.length ; i++){
            //TODO Initializing
            selected[i] = -1;
            selectedString[i] = getStringFromId(selected[i]);
        }
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
            //Left
            case R.id.left_updown_button:
                motion = 0;
                cur = left_updown_text;
                break;
            case R.id.left_leftright_button:
                motion = 1;
                cur = left_leftright_text;
                break;
            case R.id.left_forwardback_button:
                motion = 2;
                cur = left_forwardback_text;
                break;
            case R.id.left_pitch_button:
                motion = 3;
                cur = left_pitch_text;
                break;
            case R.id.left_roll_button:
                motion = 4;
                cur = left_roll_text;
                break;
            //Right
            case R.id.right_updown_button:
                motion = 5;
                cur = right_updown_text;
                break;
            case R.id.right_leftright_button:
                motion = 6;
                cur = right_leftright_text;
                break;
            case R.id.right_forwardback_button:
                motion = 7;
                cur = right_forwardback_text;
                break;
            case R.id.right_pitch_button:
                motion = 8;
                cur = right_pitch_text;
                break;
            case R.id.right_roll_button:
                motion = 9;
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
                        if(which < availableEffect.length && which >= 0) {
                            Log.d(TAG, choices[which]);
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
            InputStream in = res.openRawResource(R.raw.android_raw_pitch_roll_test);
            patchFile = IoUtils.extractResource(in, "android_raw_pitch_roll_test.pd", getActivity().getCacheDir());
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

        //bt.setOnDataReceivedListener(null);
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
        float[] val = new float[10];
        float[] finalData = new float[10];
        if(data.length != 18){
            Log.d(TAG, "FUCK!!!!");
        }
        for (int i = 0 ; i < data.length ; i = i + 2){
            //val[i/2] =  ((float) (concat(data[i],data[i+1])/(pow(2,16))));
            //if(i == 4){
            //    short d = 0;
            //    d = (short) (((data[i+1] << 8) + data[i]) & 0xFFFF);
            //    //Log.d(TAG, "val: " + Short.toString((d)));
            //}
            val[i/2] = concat(data[i], data[i+1]);
        }
        //Log.d(TAG, "val: " + Float.toString(val[2]));
        //finalData[0] = calculatePitch(val[0],val[1],val[2]);
        //finalData[1] = calculateRoll(val[0], val[1], val[2]);
        Log.d(TAG + "LEFT","pitch: " +  Float.toString(val[0]) + "\t\t\t roll: "+ Float.toString(val[1]));
        Log.d(TAG + "RIGHT","pitch: " +  Float.toString(val[2]) + "\t\t\t roll: "+ Float.toString(val[3]));

        PdBase.sendFloat("left_pitch", (val[0]));
        PdBase.sendFloat("left_roll", (val[1]));
        PdBase.sendFloat("right_pitch", (val[2]));
        PdBase.sendFloat("right_roll", (val[3]));

    }
    public void initText(){
        left_updown_text.setText(selectedString[0]);
        left_leftright_text.setText(selectedString[1]);
        left_forwardback_text .setText(selectedString[2]);
        left_pitch_text.setText(selectedString[3]);
        left_roll_text.setText(selectedString[4]);

        right_updown_text.setText(selectedString[5]);
        right_leftright_text.setText(selectedString[6]);
        right_forwardback_text.setText(selectedString[7]);
        right_pitch_text.setText(selectedString[8]);
        right_roll_text.setText(selectedString[9]);

    }
}
