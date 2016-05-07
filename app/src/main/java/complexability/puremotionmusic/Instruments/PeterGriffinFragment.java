package complexability.puremotionmusic.Instruments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import complexability.puremotionmusic.Helper.InstrumentBase;
import complexability.puremotionmusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PeterGriffinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PeterGriffinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeterGriffinFragment extends InstrumentBase {
    private OnFragmentInteractionListener mListener;

    private static final String TAG = "ReverbFragment";
    private PdService pdService = null;

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
        View view = inflater.inflate(R.layout.fragment_peter_griffin, container, false);
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
            InputStream in = res.openRawResource(R.raw.android_interface_basic_with_control);
            patchFile = IoUtils.extractResource(in, "android_interface_basic_with_control.pd", getActivity().getCacheDir());
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
