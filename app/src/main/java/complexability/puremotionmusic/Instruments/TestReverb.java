package complexability.puremotionmusic.Instruments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import complexability.puremotionmusic.R;

public class TestReverb extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "TestReverb";
    private Button playbtn;
    private ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reverb2);

        playbtn = (Button) findViewById(R.id.playbtn);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton2);

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Click");
                if (pdService.isRunning()) {
                    stopAudio();
                } else {
                    Log.d(TAG,"startAudio");
                    startAudio();
                    Log.d(TAG, "after");
                }
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                PdBase.sendFloat("onOff", val);
            }
        });

        AudioParameters.init(this);
        PdPreferences.initPreferences(getApplicationContext());
       PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener( this);
        bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);

    }
    /* synchronize on this lock whenever you access pdService */
    private final Object lock = new Object();

    /* the reference to the actual launched PdService */
    private PdService pdService = null;

    private final ServiceConnection pdConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"HEllo");
            pdService = ((PdService.PdBinder)service).getService();
            initPd();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // this method will never be called
        }
    };
    private PdReceiver receiver = new PdReceiver() {

        private void pdPost(String msg) {
            toast("Pure Data says, \"" + msg + "\"");
        }

        @Override
        public void print(String s) {
            post(s);
        }

        @Override
        public void receiveBang(String source) {
            pdPost("bang");
        }

        @Override
        public void receiveFloat(String source, float x) {
            pdPost("float: " + x);
        }

        @Override
        public void receiveList(String source, Object... args) {
        }

        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
        }

        @Override
        public void receiveSymbol(String source, String symbol) {
            pdPost("symbol: " + symbol);
        }
    };

    /* actually bind the service, which triggers the code above;
       this is the method you should call to launch Pd */



    int patch = 0;



    private void initPd() {
        Resources res = getResources();
        File patchFile = null;
        try {
            PdBase.setReceiver(receiver);
            PdBase.subscribe("android");
            InputStream in = res.openRawResource(R.raw.freevvv);
            patchFile = IoUtils.extractResource(in, "freevvv.pd", getCacheDir());
            PdBase.openPatch(patchFile);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            finish();
        } finally {
            if (patchFile != null) patchFile.delete();
        }
    }

    private void startAudio() {
        String name = getResources().getString(R.string.app_name);
        try {
            pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
            pdService.startAudio(new Intent(this, TestReverb.class), R.drawable.icon, name, "Return to " + name + ".");
        } catch (IOException e) {
            Log.d(TAG, String.valueOf(e));
        }
    }

    private void stopAudio() {
        pdService.stopAudio();
    }

    private void cleanup() {
        try {
            unbindService(pdConnection);
        } catch (IllegalArgumentException e) {
            // already unbound
            pdService = null;
        }
    }


    /* override default exit method to run cleanup() first */
    @Override
    public void onDestroy() {
        cleanup();
        super.onDestroy();
    }

    private Toast toast = null;

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText(TAG + ": " + msg);
                toast.show();
            }
        });
    }
    private void post(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (pdService.isRunning()) {
            startAudio();
        }
    }
}
