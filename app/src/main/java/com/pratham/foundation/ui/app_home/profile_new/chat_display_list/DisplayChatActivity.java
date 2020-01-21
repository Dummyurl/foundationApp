package com.pratham.foundation.ui.app_home.profile_new.chat_display_list;

import android.content.res.Configuration;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EActivity(R.layout.activity_certificate_display)
public class DisplayChatActivity extends BaseActivity implements
        ChatQuesContract.ChatQuesView,
        ChatQuesContract.ChatQuesItemClicked{

    @Bean(ChatQuesPresenter.class)
    ChatQuesContract.ChatQuesPresenter presenter;

    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.certificate_recycler_view)
    RecyclerView recycler_view;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    String sub_Name;
    ChatQuesAdapter certificateAdapter;
    List<Score> imgQuesMainList;

    @AfterViews
    public void initialize() {
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        presenter.setView(DisplayChatActivity.this);
        displayProfileName();
        displayProfileImage();
        presenter.showQuestion();
    }

    @UiThread
    @Override
    public void addToAdapter(List<Score> assessmentList) {
        imgQuesMainList = assessmentList;
        if(certificateAdapter==null) {
            certificateAdapter = new ChatQuesAdapter(this, imgQuesMainList,
                    DisplayChatActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(certificateAdapter);
        }else
            certificateAdapter.notifyDataSetChanged();
    }


    @Background
    public void displayProfileImage() {
        String sImage;
        try {
            if (!GROUP_LOGIN)
                sImage = AppDatabase.getDatabaseInstance(this).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            else
                sImage = "group_icon";
        } catch (Exception e) {
            e.printStackTrace();
            sImage = "group_icon";
        }
        setStudentProfileImage(sImage);
    }
    
    @UiThread
    public void setStudentProfileImage(String sImage) {
    }

    @Background
    public void displayProfileName() {
        String profileName = "QR Group";
        try {
            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(this).getGroupsDao().getGroupNameByGrpID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            else if (!FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(this).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            }

            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = profileName.split(" ")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        setProfileName(profileName);
    }

    @Click(R.id.main_back)
    public void pressedBack(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @UiThread
    public void setProfileName(String profileName) {
//        tv_Activity.setText(profileName);
        tv_Topic.setText(profileName);
    }

    @Override
    public void gotoQuestions(Score scoreDisp) {
        //TODO OPEN Fragment and show data
//        Toast.makeText(this, "gotoCertificate", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, ShowImgQuestionActivity_.class);
//        intent.putExtra("scoreDisp", scoreDisp);
//        startActivity(intent);
    }
}