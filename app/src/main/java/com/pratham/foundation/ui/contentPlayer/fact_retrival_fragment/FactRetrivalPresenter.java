package com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalReadingVocabulary;
import com.pratham.foundation.ui.identifyKeywords.QuestionModel;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;

@EBean
public class FactRetrivalPresenter implements FactRetrivalContract.FactRetrivalPresenter {
    private QuestionModel questionModel;
    private FactRetrivalContract.FactRetrivalView view;
    private Context context;
    private String gameName, resId, contentTitle;
    private float perc;
    private List<QuetionAns> quetionAnsList;
    private List<QuestionModel> quetionModelList;
    private int totalWordCount, learntWordCount;
    private List<QuetionAns> selectedFive;

    public FactRetrivalPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(FactRetrivalContract.FactRetrivalView factRetrivalView, String contentTitle, String resId) {
        this.view = factRetrivalView;
        this.resId = resId;
        this.contentTitle = contentTitle;
    }

    @Override
    public void getData() {
        //get data
        String text = FC_Utility.loadJSONFromAsset(context, "factRetrial.json");
       // List instrumentNames = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        quetionModelList = gson.fromJson(text, type);
        quetionAnsList = quetionModelList.get(0).getKeywords();
        getDataList();

           /* JSONArray jsonArray = new JSONArray(text);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            questionModel.setParagraph((String) jsonObject.get("paragraph"));
            JSONArray array = (JSONArray) jsonObject.get("keywords");
            for (int i = 0; i < array.length(); i++) {
                instrumentNames.add(array.getString(i));
            }
            questionModel.setKeywords(instrumentNames);*/

    }

    @Background
    @Override
    public void getDataList() {
        try {
            selectedFive=new ArrayList<QuetionAns>();
            perc = getPercentage();
            Collections.shuffle(quetionAnsList);
            for (int i = 0; i < quetionAnsList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + quetionAnsList.get(i).getQuetion()))
                        selectedFive.add(quetionAnsList.get(i));
                } else {
                    selectedFive.add(quetionAnsList.get(i));
                }
                if (selectedFive.size() >= 5) {
                    break;
                }
            }
            Collections.shuffle(selectedFive);
            view.showParagraph(quetionModelList.get(0).getParagraph(),selectedFive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = quetionAnsList.size();
            learntWordCount = getLearntWordsCount();
            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
                return perc;
            } else
                return 0f;
        } catch (Exception e) {
            return 0f;
        }
    }


    private int getLearntWordsCount() {
        int count = 0;
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, resId, wordStr);
            if (word != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addLearntWords(ModalReadingVocabulary modalReadingVocabulary) {
        KeyWords learntWords = new KeyWords();
        learntWords.setResourceId(resId);
        learntWords.setSentFlag(0);
        learntWords.setStudentId(FC_Constants.currentStudentID);
        //  learntWords.setSessionId(FC_Constants.currentSession);
        learntWords.setKeyWord(modalReadingVocabulary.getConvoTitle());
        learntWords.setWordType("word");
        //  learntWords.setSynId(gameName);
        appDatabase.getKeyWordDao().insert(learntWords);
        BackupDatabase.backup(context);
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FC_Constants.currentSession);
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FC_Constants.currentStudentID);
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(4);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(resId);
                assessment.setSessionIDa(FC_Constants.assessmentSession);
                assessment.setSessionIDm(FC_Constants.currentSession);
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FC_Constants.currentAssessmentStudentID);
                assessment.setStartDateTimea(resStartTime);
                assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                assessment.setLevela(FC_Constants.currentLevel);
                assessment.setLabel("test: " + Label);
                assessment.setSentFlag(0);
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}