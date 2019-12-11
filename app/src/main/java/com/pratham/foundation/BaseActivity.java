package com.pratham.foundation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.utility.CatchoTransparentActivity;
import com.pratham.foundation.utility.FC_Utility;

import net.alhazmy13.catcho.library.Catcho;

import java.util.Locale;

import static com.pratham.foundation.ApplicationClass.audioManager;
import static com.pratham.foundation.ApplicationClass.ttsService;


public class BaseActivity extends AppCompatActivity {

    public static boolean muteFlg = false;
    View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hideSystemUI();
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDecorView = getWindow().getDecorView();
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        startSTT();
        muteFlg = false;
        Catcho.Builder(this)
                .activity(CatchoTransparentActivity.class).build();
//                .recipients("abc@domain.com").build();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();
    }

    private void hideSystemUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void startSTT() {
        if (ttsService == null) {
            ttsService = new TTSService(getApplication());
            ttsService.setActivity(this);
            ttsService.setSpeechRate(0.7f);
            ttsService.setLanguage(new Locale("en", "IN"));
        }
    }

    public static void setMute(int m) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            if (m == 1 && !muteFlg) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                muteFlg = true;
            } else if (m == 0 && muteFlg) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
                muteFlg = false;
            }
        } else {

            if (m == 1 && !muteFlg) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                muteFlg = true;
            } else if (m == 0 && muteFlg) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                muteFlg = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ActivityOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ActivityResumed();
        BackupDatabase.backup(this);
    }

    @SuppressLint("StaticFieldLeak")
    public void endSession(Context context) {
        try {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
                        String toDateTemp = AppDatabase.appDatabase.getSessionDao().getToDate(curSession);
                        if (toDateTemp.equalsIgnoreCase("na")) {
                            AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
                        }
                        BackupDatabase.backup(context);
                    } catch (Exception e) {
                        String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
                        AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}