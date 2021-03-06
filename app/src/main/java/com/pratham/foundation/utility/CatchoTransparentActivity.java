package com.pratham.foundation.utility;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.ProcessPhoenix;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.services.shared_preferences.FastSave;

import net.alhazmy13.catcho.library.Catcho;
import net.alhazmy13.catcho.library.error.CatchoError;

public class CatchoTransparentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catcho);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        CatchoError error = (CatchoError) getIntent().getSerializableExtra(Catcho.ERROR);
        Modal_Log log = new Modal_Log();
        log.setCurrentDateTime(FC_Utility.GetCurrentDateTime());
        log.setErrorType("ERROR");
        log.setExceptionMessage(error.toString());
        log.setExceptionStackTrace(error.getError());
        log.setMethodName("NO_METHOD");
        log.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "no_group"));
        log.setDeviceId("" + FC_Utility.getDeviceID());
        AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getLogsDao().insertLog(log);
        BackupDatabase.backup(CatchoTransparentActivity.this);
        findViewById(R.id.catcho_button).setOnClickListener(v -> {
//            finishAffinity();
            ProcessPhoenix.triggerRebirth(CatchoTransparentActivity.this);
        });
    }
}
