package complexability.puremotionmusic.Instruments;

import android.content.Context;
import android.media.effect.Effect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

import complexability.puremotionmusic.Helper.Constants;
import complexability.puremotionmusic.Helper.Effects;
import complexability.puremotionmusic.PureDataBaseFragment;
import complexability.puremotionmusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Sequencer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Sequencer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sequencer extends PureDataBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PdUiDispatcher dispatcher;

    private OnFragmentInteractionListener mListener;


    Constants constants = new Constants();

    Effects effectList[] = new Effects[4];

    public Sequencer() {
        // Required empty public constructor
        effectList[0] = constants.getEffect(Constants.VOLUME);
        effectList[1] = constants.getEffect(Constants.NOTE);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sequencer.
     */
    // TODO: Rename and change types and number of parameters
    public static Sequencer newInstance(String param1, String param2) {
        Sequencer fragment = new Sequencer();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("TestFragment", "onCreateView");
        try{
            initPD();
            dispatcher = new PdUiDispatcher();
            PdBase.setReceiver(dispatcher);
            //loadPDPatch("simplepatch.pd");
            //loadPDPatch("reverb.pd");
            loadPDPatch("new_sequencer.pd");
            //loadPDPatch("sequencer3.pd");
        }catch (IOException e){
        }

        final View view = inflater.inflate(R.layout.fragment_sequencer, container, false);
        ToggleButton onOffButton = (ToggleButton) view.findViewById(R.id.onOffButton);
        onOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                PdBase.sendFloat("onOff", val);
            }
        });
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Seek bar", "val: " + Integer.toString(progress));
                float x = (float) progress + 30;
                PdBase.sendFloat("val", x);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                float x = (float) progress + 30;
                PdBase.sendFloat("val", x);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                float x = (float) progress + 30;
                PdBase.sendFloat("val", x);
            }

        });
        Button testButton = (Button) view.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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
    protected void loadPDPatch(String patchName) throws IOException {
        File dir = myContext.getFilesDir();
        //IoUtils.extractZipResource(getResources().openRawResource(R.raw.reverb), dir, true);
        //IoUtils.extractZipResource(getResources().openRawResource(R.raw.simplepatch), dir, true);
        //IoUtils.extractZipResource(getResources().openRawResource(R.raw.sequencer3), dir, true);
        //IoUtils.extractZipResource(getResources().openRawResource(R.raw.basic_sequencer), dir, true);
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.new_sequencer), dir, true);
        File pdPatch = new File(dir, patchName);
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onSequencerFragmentInteraction(String string);
    }
}
