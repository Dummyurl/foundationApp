package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE;
import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_NODE_ID;
import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_STRING;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.INTERNET_LANGUAGE_API;
import static com.pratham.foundation.utility.FC_Constants.rootParentId;

@EBean
public class SelectSubjectPresenter implements SelectSubjectContract.SubjectPresenter, API_Content_Result {

    Context context;
    API_Content api_content;
    Gson gson;
    SelectSubjectContract.SubjectView subjectView;
    List<ContentTable> subjectList, langList;

    SelectSubjectPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(SelectSubjectContract.SubjectView subjectView) {
        this.subjectView = subjectView;
        api_content = new API_Content(context, SelectSubjectPresenter.this);
        langList = new ArrayList<>();
        subjectList = new ArrayList<>();
        gson = new Gson();
    }

    @Override
    public void clearSubjList() {
        subjectList.clear();
    }

    @Background
    @Override
    public void getSubjectList() {
        subjectView.showLoader();
        String currLang = FastSave.getInstance().getString(APP_LANGUAGE, HINDI);
        String currLangNodeId = FastSave.getInstance().getString(APP_LANGUAGE_NODE_ID,"");
        Log.d("currLang", "getSubjectList: "+currLang);
        String rootID = AppDatabase.getDatabaseInstance(context).getContentTableDao().getRootData(rootParentId, currLang);
        if (rootID != null)
            subjectList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(rootID);

        if (FC_Utility.isDataConnectionAvailable(context))
            api_content.getAPIContent(FC_Constants.INTERNET_BROWSE, FC_Constants.INTERNET_BROWSE_API, currLangNodeId);
        else {
            subjectView.initializeSubjectList(subjectList);
            subjectView.notifySubjAdapter();
            subjectView.dismissLoadingDialog();
        }
    }

    @Background
    @Override
    public void getLanguage() {
        langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(rootParentId);
        if (FC_Utility.isDataConnectionAvailable(context))
            api_content.getAPILanguage(APP_LANGUAGE_STRING, INTERNET_LANGUAGE_API);
        else {
            if (langList.size() > 0)
                subjectView.showLanguageSelectionDialog(langList);
        }

    }

    boolean parentFound = false;

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(APP_LANGUAGE_STRING)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();

                List<ContentTable> serverContentList = gson.fromJson(response, listType);

                for (int i = 0; i < serverContentList.size(); i++) {
                    parentFound = false;
                    List<ContentTable> tempList;
                    ContentTable contentTable = new ContentTable();
                    for (int j = 0; j < langList.size(); j++) {
                        if (serverContentList.get(i).getNodeId().equalsIgnoreCase(
                                langList.get(j).getNodeId())) {
                            parentFound = true;
                        }
                    }
                    if (!parentFound) {
                        langList.add(serverContentList.get(i));
                    }
                }
                subjectView.showLanguageSelectionDialog(langList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();

                List<ContentTable> serverContentList = gson.fromJson(response, listType);

                for (int i = 0; i < serverContentList.size(); i++) {
                    parentFound = false;
                    List<ContentTable> tempList;
                    ContentTable contentTable = new ContentTable();
                    serverContentList.get(i).setIsDownloaded("false");
                    for (int j = 0; j < subjectList.size(); j++) {
                        if (serverContentList.get(i).getNodeId().equalsIgnoreCase(
                                subjectList.get(j).getNodeId())) {
                            parentFound = true;
                        }
                    }
                    if (!parentFound) {
                        subjectList.add(serverContentList.get(i));
                    }
                }
                subjectView.initializeSubjectList(subjectList);
                subjectView.notifySubjAdapter();
                subjectView.dismissLoadingDialog();
            } catch (Exception e) {
                e.printStackTrace();
                subjectView.initializeSubjectList(subjectList);
                subjectView.notifySubjAdapter();
                subjectView.dismissLoadingDialog();
            }
        }

    }

    @Override
    public void receivedError(String header) {
        subjectView.initializeSubjectList(subjectList);
        subjectView.notifySubjAdapter();
        subjectView.dismissLoadingDialog();
    }
}
