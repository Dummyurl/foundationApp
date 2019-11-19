package com.pratham.foundation.ui.contentPlayer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansTextViewBold;
import com.pratham.foundation.interfaces.ShowInstruction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionsDialog extends Dialog {

    @BindView(R.id.dia_title)
    SansTextViewBold dia_title;
    ShowInstruction showInstruction;
    private String resorcetype = "";
    private Context context;

    public InstructionsDialog(@NonNull Context context, ShowInstruction showInstruction, String resorcetype) {
        super(context,R.style.FullScreenCustomDialogStyle);
        this.resorcetype = resorcetype;
        this.context = context;
        this.showInstruction = showInstruction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_dialog);
        ButterKnife.bind(this);
        getWindow().setDimAmount(.7f);
        int resId = context.getResources().getIdentifier(resorcetype, "string", context.getPackageName());
        if (resId != 0) {
            String instruction = context.getString(resId);
            dia_title.setText(instruction);
        }else {
            dia_title.setText("");
        }
    }

    @OnClick(R.id.dia_btn_green)
    public void playGame() {
        showInstruction.play();
        this.dismiss();
    }


    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
    }
}
