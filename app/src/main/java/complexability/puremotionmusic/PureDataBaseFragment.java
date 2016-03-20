package complexability.puremotionmusic;

import android.content.Context;
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

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by Sorawis on 3/20/2016.
 */
public class PureDataBaseFragment extends Fragment {


    private PdUiDispatcher dispatcher;
    private FragmentActivity myContext;


    protected void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);

    }


    protected void loadPDPatch(String patchName) throws IOException {
        File dir = myContext.getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.simplepatch), dir, true);
        File pdPatch = new File(dir, patchName);
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        PdAudio.startAudio(myContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        PdAudio.startAudio(myContext);
    }

}
