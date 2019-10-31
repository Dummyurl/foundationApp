package com.pratham.foundation.ui.contentPlayer.reading_paragraphs;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalParaMainMenu;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.modalclasses.ModalParaWord;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.ui.contentPlayer.reading_paragraphs.ReadingParagraphsActivity.correctArr;
import static com.pratham.foundation.ui.contentPlayer.reading_paragraphs.ReadingParagraphsActivity.lineBreakCounter;


@EBean
public class ReadingParagraphsPresenter implements ReadingParagraphsContract.ReadingParagraphsPresenter {

    public ReadingParagraphsContract.ReadingParagraphsView readingView;

    public Context context;
    public List<ModalParaWord> modalParaList, questionList;
    public ModalParaMainMenu modalParaMainMenu;
    public ModalParaSubMenu modalParaSubMenu;
    public JSONArray vocabParaJsonArray;
    public List<ModalParaSubMenu> modalParaSubMenuList;
    public List<KeyWords> learntWordsList;
    public KeyWords learntWords;
    public float perc = 0f, pagePerc;
    public int randomCategory, GLC, totalVocabSize, learntWordsCount;
    public String resId, resStartTime, resType, paraAudio;

    public ReadingParagraphsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ReadingParagraphsContract.ReadingParagraphsView readingView) {
        this.readingView = readingView;
        learntWordsList = new ArrayList<>();
    }

    @Override
    public void setResId(String resourceId) {
        resId = resourceId;
        resStartTime = FC_Utility.getCurrentDateTime();
    }

    @Background
    public void getRandomParaData() {
        try {
            JSONObject jsonObject = null;
            learntWordsCount = getLearntWordsCount();
            totalVocabSize = vocabParaJsonArray.length();
            perc = getPercentage(learntWordsCount, totalVocabSize);
            try {
                if (perc < 95)
                    for (int i = 0; i < vocabParaJsonArray.length(); i++) {
                        if (!checkLearnt("" + vocabParaJsonArray.getJSONObject(i).getString("paraTitle"))) {
                            jsonObject = vocabParaJsonArray.getJSONObject(i);
                            break;
                        }
                    }
                else {
                    int randomNo = FC_Utility.generateRandomNum(totalVocabSize);
                    jsonObject = vocabParaJsonArray.getJSONObject(randomNo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonObject != null) {
                Gson gson = new Gson();
                modalParaMainMenu = gson.fromJson(jsonObject.toString(), ModalParaMainMenu.class);
                modalParaSubMenuList = modalParaMainMenu.getPageList();
                paraAudio = modalParaSubMenuList.get(0).getReadPageAudio();
                randomCategory = FC_Utility.generateRandomNum(modalParaSubMenuList.size());
                modalParaList = modalParaSubMenuList.get(randomCategory).getReadList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        readingView.setParaAudio(paraAudio);
        readingView.setListData(modalParaList);
        readingView.setCategoryTitle(modalParaMainMenu.getContentTitle());
        readingView.initializeContent();
    }

    private int getLearntWordsCount() {
        int count = 0;
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, "" + resId);
        return count;
    }

    private float getPercentage(int counter, int totLen) {
        float perc = 0f;
        if (counter > 0)
            perc = ((float) counter / totLen) * 100;
        return perc;
    }

    private boolean checkLearnt(String wordCheck) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, "" + resId, wordCheck.toLowerCase());
            if (word != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Background
    @Override
    public void getDataList() {

        try {
            modalParaSubMenuList = modalParaMainMenu.getPageList();
            paraAudio = modalParaSubMenuList.get(0).getReadPageAudio();
            randomCategory = FC_Utility.generateRandomNum(modalParaSubMenuList.size());
            modalParaList = modalParaSubMenuList.get(randomCategory).getReadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        readingView.setParaAudio(paraAudio);
        readingView.setListData(modalParaList);
        readingView.initializeContent();
    }

    @Background
    @Override
    public void fetchJsonData(String contentPath, String resTypes) {
        resType = resTypes;
        try {
            InputStream is = new FileInputStream(contentPath + "Data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            Gson gson = new Gson();
            if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
                vocabParaJsonArray = jsonObj.getJSONArray("pageList");
            else
                modalParaMainMenu = gson.fromJson(jsonObj.toString(), ModalParaMainMenu.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
            getRandomParaData();
        else
            getDataList();
    }

    @Background
    @Override
    public void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList) {
        int correctWordCount = 0;
        String sttRes = sttResult.get(0);
        String[] splitRes = sttRes.split(" ");
        String word = " ";

        for (int j = 0; j < splitRes.length; j++) {
            splitRes[j] = splitRes[j].replaceAll("[^A-Za-z]+", "");
            for (int i = 0; i < splitWordsPunct.size(); i++) {
                if ((splitRes[j].equalsIgnoreCase(splitWordsPunct.get(i))) && !correctArr[i]) {
                    correctArr[i] = true;
                    word = word + splitWordsPunct.get(i) + "(" + wordsResIdList.get(i) + "),";
                    break;
                }
            }
        }

        correctWordCount = getCorrectCounter();
        String wordTime = FC_Utility.getCurrentDateTime();
        addLearntWords(splitWordsPunct, wordsResIdList);
        addScore(0, "Words:" + word, correctWordCount, correctArr.length, wordTime, " ");

        readingView.setCorrectViewColor();
        pagePerc = getPercentage(correctWordCount);
        if (pagePerc >= 80)
            readingView.allCorrectAnswer();
    }

    private void addLearntWords(List<String> splitWordsPunct, List<String> wordsResIdList) {
        for (int i = 0; i < correctArr.length; i++) {
            if (!checkLearnt(splitWordsPunct.get(i).toLowerCase())) {
                KeyWords learntWords = new KeyWords();
                if (correctArr[i]) {
                    learntWords.setSentFlag(0);
                    learntWords.setStudentId(FC_Constants.currentStudentID);
                    learntWords.setResourceId(resId);
                    learntWords.setKeyWord(splitWordsPunct.get(i).toLowerCase());
                    learntWords.setWordType("word");
                    learntWords.setTopic("Para Words");
                    appDatabase.getKeyWordDao().insert(learntWords);
                }
            }
        }
    }

    public float getPercentage(int count) {
        float perc = 0f;
        try {
            int totLen = correctArr.length - lineBreakCounter;
            if (count > 0)
                perc = ((float) count / (float) totLen) * 100;
            return perc;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getCorrectCounter() {
        int counter = 0;
        try {
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x])
                    counter++;
            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getTotalCount() {
        return correctArr.length;
    }

    @Background
    @Override
    public void addParaVocabProgress() {
        exitDBEntry();
        int ansCnt = getCorrectCounter();
        float perc = getPercentage(ansCnt);
        if (perc >= 90)
            addLearntWords(modalParaMainMenu.getContentTitle());
    }

    public void addLearntWords(String word) {
        if (!checkLearnt(word.toLowerCase())) {
            KeyWords learntWordss = new KeyWords();
            learntWordss.setResourceId("" + resId);
            learntWordss.setSentFlag(0);
            learntWordss.setStudentId(FC_Constants.currentStudentID);
            learntWordss.setKeyWord(word.toLowerCase());
            learntWordss.setTopic("" + resType);
            learntWordss.setWordType("word");
            appDatabase.getKeyWordDao().insert(learntWordss);
        }
        BackupDatabase.backup(context);
    }

    @Background
    @Override
    public void exitDBEntry() {
        int ansCnt = getCorrectCounter();
        float perc = getPercentage(ansCnt);
        String Label = "resourceProgress";
        addContentProgress(perc, Label);
    }

    private void addContentProgress(float perc, String label) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FC_Constants.currentSession);
            contentProgress.setStudentId("" + FC_Constants.currentStudentID);
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            appDatabase.getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Background
    @Override
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
            score.setLevel(0);
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