package complexability.puremotionmusic;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;


import java.io.File;
import java.io.IOException;

import complexability.puremotionmusic.Helper.Effects;


/**
 * Created by Sorawis on 3/20/2016.
 */
public class PureDataBaseFragment extends Fragment {


    //private PdUiDispatcher dispatcher;
    protected FragmentActivity myContext;
    private String patchName;







    protected void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        Log.d("PureDataFragment", "sample rate: " + Integer.toString(sampleRate));
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);

        //dispatcher = new PdUiDispatcher();
        //PdBase.setReceiver(dispatcher);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PureDataFragment", "onResume");

        PdAudio.startAudio(myContext);
    }

    @Override
    public void onPause() {
        Log.d("PureDataFragment", "onPause");

        super.onPause();
        PdAudio.stopAudio();
    }

    @Override
    public void onDestroy() {
        Log.d("PureDataFragment", "onDestroy");
        super.onDestroy();
        //if (PdAudio.isRunning()) {
        //PdAudio.stopAudio();

        PdAudio.release();
        //}
       // if (PdBase.isRunning()) {

        //}
    }

    @Override
    public void onDestroyView() {
        Log.d("PureDataFragment", "onDestroyView");
        super.onDestroyView();
        //if (PdAudio.isRunning()) {
        PdAudio.stopAudio();

        PdAudio.release();
        //}
        //if (PdBase.isRunning()) {
        PdBase.release();
        //}
    }

}
