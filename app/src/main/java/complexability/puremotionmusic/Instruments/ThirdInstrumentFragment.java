package complexability.puremotionmusic.Instruments;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

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
import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import complexability.puremotionmusic.Helper.DrawRightBall;
import complexability.puremotionmusic.Helper.DrawTheBall;
import complexability.puremotionmusic.Helper.InstrumentBase;
import complexability.puremotionmusic.MainActivity;
import complexability.puremotionmusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThirdInstrumentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThirdInstrumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdInstrumentFragment extends InstrumentBase implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int TOTAL_MOTION = 4;
    private static final int TOTAL_EFFECT = 3;

    private static final String TAG = "ThirdInstrumentFragment";

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
    private String[] keys = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
    private String[] noteValue = {"1/4","1/8","1/16"};

    DrawTheBall drawTheLeftBall;
    DrawRightBall drawTheRightBall;

    SeekBar sineWaveSeekBar;
    SeekBar sawToothSeekBar;
    SeekBar pwmSeekBar;
    SeekBar dutyCycleSeekBar;

    TextView sineWaveText;
    TextView sawToothText;
    TextView pwmText;
    TextView dutyCycleText;

    SeekBar bassSineWaveSeekBar;
    SeekBar bassSawToothSeekBar;
    SeekBar bassPwmSeekBar;
    SeekBar bassDutyCycleSeekBar;
    TextView bassSineWaveText;
    TextView bassSawToothText;
    TextView bassPwmText;
    TextView bassDutyCycleText;

    Button keyButton;
    Button bpmButton;
    Button noteLengthButton;

    TextView bpmText;
    TextView keyText;
    TextView noteValueText;

    ToggleButton onOffButton;

    Switch reverbSwitch;

    BluetoothSPP bt;
    String availableEffects[];

    int currentBpm = 120;

    int  leadSineWavetVal = 60;
    int  leadSawToothVal = 42;
    int  leadPwmVal = 15;
    int  leadDutyCycleVal = 34;
    int  bassleadSineWaveVal = 47;
    int  bassSawToothVal = 43;
    int  bassPwmVal = 53;
    int  bassDutyCycleVal = 58;
    /*
    Build Material number picker
     */


    public ThirdInstrumentFragment() {
        // Required empty public constructor
    }

    public static ThirdInstrumentFragment newInstance(String param1, String param2) {
        ThirdInstrumentFragment fragment = new ThirdInstrumentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        final View view = inflater.inflate(R.layout.fragment_third_instrument, container, false);

        drawTheLeftBall = (DrawTheBall) view.findViewById(R.id.drawTheLeftBall) ;
        drawTheRightBall = (DrawRightBall) view.findViewById(R.id.drawTheRightBall) ;

        availableEffects = getActivity().getResources().getStringArray(R.array.reverb_effect_name);

        //TextView instrumentName  = (TextView) view.findViewById(R.id.instrumentName);
        RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        //ImageView imageView = (ImageView) view.findViewById(R.id.instrumentImage);
        //instrumentName.setText("8-bit Piano");
        //imageView.setImageResource(R.drawable.eightbit_instr);



        sineWaveText = (TextView) view.findViewById(R.id.sineWaveText);
        sawToothText = (TextView) view.findViewById(R.id.sawToothText);
        pwmText = (TextView) view.findViewById(R.id.pwmText);
        dutyCycleText = (TextView) view.findViewById(R.id.dutyCycleText);

        sineWaveSeekBar = (SeekBar) view.findViewById(R.id.sinewaveSeekBar);
        sawToothSeekBar = (SeekBar) view.findViewById(R.id.sawToothSeekBar);
        pwmSeekBar      = (SeekBar) view.findViewById(R.id.pwmSeekBar);
        dutyCycleSeekBar = (SeekBar) view.findViewById(R.id.dutyCycleSeekBar);

        bassSineWaveText = (TextView) view.findViewById(R.id.bassSineWaveText);
        bassSawToothText = (TextView) view.findViewById(R.id.bassSawtoothText);
        bassPwmText = (TextView) view.findViewById(R.id.bassPwmText);
        bassDutyCycleText = (TextView) view.findViewById(R.id.bassDutyCycleText);

        bassSineWaveSeekBar = (SeekBar) view.findViewById(R.id.bassSineWaveSeekbar);
        bassSawToothSeekBar = (SeekBar) view.findViewById(R.id.bassSawToothSeekbar);
        bassPwmSeekBar      = (SeekBar) view.findViewById(R.id.bassPwmSeekBar);
        bassDutyCycleSeekBar = (SeekBar) view.findViewById(R.id.bassDutyCycleSeekbar);

        reverbSwitch = (Switch) view.findViewById(R.id.reverbSwitch);

        keyButton = (Button) view.findViewById(R.id.keyButton);
        keyText = (TextView) view.findViewById(R.id.keyText);

        bpmButton = (Button) view.findViewById(R.id.setBpmButton);
        noteLengthButton = (Button) view.findViewById(R.id.noteValueButton);
        noteValueText = (TextView) view.findViewById(R.id.noteValueText);
        bpmText = (TextView) view.findViewById(R.id.bpmText);

        keyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] keys = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(myContext);
                builderSingle.setTitle("Select Note Value:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        myContext,
                        android.R.layout.simple_list_item_1);
                for(int i = 0 ; i < keys.length ; i ++){
                    arrayAdapter.add(keys[i]);
                }
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyText.setText(arrayAdapter.getItem(which));
                        PdBase.sendFloat("key", which);
                        Log.d("Item", Integer.toString(which));
                    }
                });
                builderSingle.show();
            }
        });
        bpmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialNumberPicker bpmPicker = new MaterialNumberPicker.Builder(myContext)
                        .minValue(40)
                        .maxValue(300)
                        .defaultValue(currentBpm)
                        .backgroundColor(Color.WHITE)
                        .separatorColor(Color.TRANSPARENT)
                        .textColor(Color.BLACK)
                        .textSize(20)
                        .enableFocusability(false)
                        .wrapSelectorWheel(true)
                        .build();

                new AlertDialog.Builder(myContext)
                        .setTitle("Select BPM")
                        .setView(bpmPicker)
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Snackbar.make(view.findViewById(R.id.container), "You picked : " + bpmPicker.getValue(), Snackbar.LENGTH_LONG).show();
                                bpmText.setText(String.valueOf(bpmPicker.getValue()));
                                PdBase.sendFloat("bpm", (float) bpmPicker.getValue());
                                currentBpm = bpmPicker.getValue();
                            }
                        })
                        .show();

            }
        });

        reverbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                PdBase.sendFloat("reverb_state", val);
            }
        });

        noteLengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(myContext);
                builderSingle.setTitle("Select Note Value:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        myContext,
                        android.R.layout.simple_list_item_1);
                arrayAdapter.add("1/16");
                arrayAdapter.add("1/8");
                arrayAdapter.add("1/4");
                arrayAdapter.add("1/2");
                arrayAdapter.add("1");

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteValueText.setText(arrayAdapter.getItem(which));
                        PdBase.sendFloat("note_length", which);
                        Log.d("Item", Integer.toString(which));
                    }
                });
                builderSingle.show();
            }
        });

        /*
        SeekBar Handler
         */

        sineWaveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sineWaveText.setText(String.valueOf(progress));
                PdBase.sendFloat("sine_osc1_level", (float) (progress/100.));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sawToothSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sawToothText.setText(String.valueOf(progress));
                PdBase.sendFloat("sawtooth_osc1_level", (float) (progress/100.));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pwmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pwmText.setText(String.valueOf(progress));
                PdBase.sendFloat("pwm_osc1_level", (float) (progress/100.));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dutyCycleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dutyCycleText.setText(String.valueOf(progress));
                PdBase.sendFloat("pwm_duty_cycle1", (float) (progress/100.));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /* for bass*/
        bassSineWaveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassSineWaveText.setText(String.valueOf(progress));
                PdBase.sendFloat("sine_osc2_level", (float) (progress/100.));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bassSawToothSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassSawToothText.setText(String.valueOf(progress));
                PdBase.sendFloat("sawtooth_osc2_level", (float) (progress/100.));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bassPwmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassPwmText.setText(String.valueOf(progress));
                Log.d(TAG, Float.toString((float) (progress/100.0)));
                PdBase.sendFloat("pwm_osc2_level", (float) (progress/100.));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bassDutyCycleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassDutyCycleText.setText(String.valueOf(progress));
                PdBase.sendFloat("pwm_duty_cycle2", (float) (progress/100.));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        /*
        Initializa Mapper for mapping motions
         */
        /*
        GUI Initialization
         */

        /*
        Button
         */
        onOffButton = (ToggleButton) view.findViewById(R.id.onOffButton);
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

                PdBase.sendFloat("left_pitch_sel",selected[LEFT_PITCH]);
                PdBase.sendFloat("right_pitch_sel", selected[RIGHT_PITCH]);
                PdBase.sendFloat("left_roll_sel", selected[LEFT_ROLL]);
                PdBase.sendFloat("right_roll_sel", selected[RIGHT_ROLL]);

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
                if (data.length >=24) {
                    dataProc(data);
                }
            }
        });

        AudioParameters.init(getActivity());
        PdPreferences.initPreferences(getActivity().getApplicationContext());
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        getActivity().bindService(new Intent(getActivity(), PdService.class), pdConnection, Context.BIND_AUTO_CREATE);

        return view;    }

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
    public void onDetach() {
        Log.d(TAG,"onDetach");
        super.onDetach();
        mListener = null;
        if(pdService.isRunning()){
            stopAudio();
        }
        cleanup();
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        switch(v.getId()){
            case R.id.left_pitch_button:
                motion = LEFT_PITCH;
                break;
            case R.id.left_roll_button:
                motion = LEFT_ROLL;
                break;
            case R.id.right_pitch_button:
                motion = RIGHT_PITCH;
                break;
            case R.id.right_roll_button:
                motion = RIGHT_ROLL;
                break;
        }
        final int finalMotion = motion;

        new MaterialDialog.Builder(myContext)
                .title(R.string.title)
                .items(R.array.reverb_effect_name)
                .content("Please do not select any effect that has already been selected for other motions")
                .theme(Theme.DARK)
                .dividerColorRes(R.color.dividerDialog)
                .itemsCallbackSingleChoice(selected[motion], new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        selected[finalMotion] = which;
                        //selectedString[finalMotion] = (String) text;
                        //finalCur.setText(AVAILABLE_EFFECT_NAME[finalMotion]);
                        sendChange(finalMotion,which);
                        if(which < availableEffects.length && which >= 0) {
                            Log.d(TAG, availableEffects[which]);
                            //userSelected(finalMotion, choices[which]);
                        }
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    private void sendChange(int finalMotion, int which) {
        Log.d(TAG,"final:" + Integer.toString(finalMotion) +"\t\t   which:" + Integer.toString(which));
        switch (finalMotion){
            case LEFT_PITCH:
                left_pitch_text.setText(availableEffects[which]);
                PdBase.sendFloat("left_pitch_sel",which);
                Log.d(TAG,"SENDING \"" + availableEffects[which] + "\" for Left hand pitch");
                break;
            case RIGHT_PITCH:
                right_pitch_text.setText(availableEffects[which]);
                PdBase.sendFloat("right_pitch_sel", which);
                Log.d(TAG,"SENDING \"" + availableEffects[which] + "\" for Right hand pitch");
                break;
            case LEFT_ROLL:
                left_roll_text.setText(availableEffects[which]);
                PdBase.sendFloat("left_roll_sel", which);
                Log.d(TAG,"SENDING \"" + availableEffects[which] + "\" for Left hand roll");
                break;
            case RIGHT_ROLL:
                right_roll_text.setText(availableEffects[which]);
                PdBase.sendFloat("right_roll_sel", which);
                Log.d(TAG,"SENDING \"" + availableEffects[which] + "\" for Right hand roll");
                break;
            default:
                break;
        }
        resendConfig();
    }
    private void resendConfig() {
        PdBase.sendFloat("left_pitch_sel", selected[LEFT_PITCH]);
        PdBase.sendFloat("right_pitch_sel", selected[RIGHT_PITCH]);
        PdBase.sendFloat("left_roll_sel", selected[LEFT_ROLL]);
        PdBase.sendFloat("right_roll_sel", selected[RIGHT_ROLL]);
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
            //PdBase.setReceiver(receiver);
            //PdBase.subscribe("metro_bng");
            ////PdBase.subscribe("android");
            InputStream in = res.openRawResource(R.raw.instrument_1);
            patchFile = IoUtils.extractResource(in, "instrument_1.pd", getActivity().getCacheDir());
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
        if(pdService==null){
            return;
        }
        try {
            pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
            pdService.startAudio(new Intent(getActivity(), ThirdInstrumentFragment.class), R.drawable.icon, name, "Return to " + name + ".");
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
    protected void changeToNextInstrument() {
        ((MainActivity) getActivity()).moveToFragmentByName("SineWave");
    }

    @Override
    protected void changeToPrevInstrument() {
        ((MainActivity) getActivity()).moveToFragmentByName("SineWave");

    }

    @Override
    protected void startPlaying() {
        startAudio();
    }

    @Override
    protected void stopPlaying() {
        stopAudio();
    }

    @Override
    protected void togglePlaying() {
        onOffButton.toggle();
    }

    public void dataProc(byte[] data){
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



        rightMotion = calculateRightHandKalmanPitchRollForCheckOffTest(r_x_accel, r_y_accel, r_z_accel, r_x_gyro, r_y_gyro, r_z_gyro);
        leftMotion  = calculateLeftHandKalmanPitchRollForCheckOffTest(l_x_accel, l_y_accel, l_z_accel, l_x_gyro, l_y_gyro, l_z_gyro);
        PdBase.sendFloat("left_pitch",leftMotion[PITCH]);
        PdBase.sendFloat("left_roll", leftMotion[ROLL]);
        PdBase.sendFloat("right_pitch", rightMotion[PITCH]);
        PdBase.sendFloat("right_roll", rightMotion[ROLL]);
        //Log.d(TAG,"RIGHTHAND ACCEL x: " + Float.toString((r_x_accel)) + "\t y: "+Float.toString( r_y_accel) +"\t z: "+Float.toString( r_z_accel));
        //Log.d(TAG,"RIGHTHAND ORIEN Roll: " + Integer.toString((int)rightMotion[ROLL]) + "\t Pitch: "+Integer.toString((int)rightMotion[PITCH]));

        //Log.d(TAG,"LEFTHAND ACCEL x: " + Float.toString((l_x_accel)) + "\t\t y: "+Float.toString(l_y_accel) +"\t\t z: "+Float.toString(l_z_accel));
        //Log.d(TAG,"LEFTHAND ORIEN Roll: " + Integer.toString((int)leftMotion[ROLL]) + "\t Pitch: "+Integer.toString((int)leftMotion[PITCH]));

        drawTheLeftBall.updateValue( leftMotion[ROLL], -leftMotion[PITCH]);
        drawTheRightBall.updateValue(rightMotion[ROLL], -rightMotion[PITCH]);
    }
    public void initText(){
        left_pitch_text.setText(availableEffects[3]);
        left_roll_text.setText(availableEffects[0]);
        right_pitch_text.setText(availableEffects[2]);
        right_roll_text.setText(availableEffects[1]);
        selected[LEFT_PITCH] = 3;
        selected[LEFT_ROLL] = 0;
        selected[RIGHT_PITCH] = 2;
        selected[RIGHT_ROLL] = 1;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
