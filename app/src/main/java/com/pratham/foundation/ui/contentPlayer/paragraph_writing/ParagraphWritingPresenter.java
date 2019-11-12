package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;

@EBean
public class ParagraphWritingPresenter implements ParagraphWritingContract.ParagraphWritingPresenter {
    private ScienceQuestion questionModel;
    private ParagraphWritingContract.ParagraphWritingView view;
    private Context context;
    private List<ScienceQuestion> quetionModelList;
    private float perc;
    private int totalWordCount, learntWordCount;
    private String gameName, resId, contentTitle, readingContentPath;

    public ParagraphWritingPresenter(Context context) {
        this.context = context;

    }

    public void setView(ParagraphWritingContract.ParagraphWritingView view, String resId, String readingContentPath) {
        this.view = view;
        this.resId = resId;
        this.readingContentPath = readingContentPath;
    }

    @Override
    public void getData() {
        questionModel = new ScienceQuestion();
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, "CWiritng.json");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScienceQuestion>>() {
        }.getType();

        quetionModelList = gson.fromJson(text, type);
        getDataList();
       /* getDataList();
        try {
            JSONArray jsonArray = new JSONArray(text);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            questionModel.setParagraph((String) jsonObject.get("paragraph"));
            JSONArray array = (JSONArray) jsonObject.get("keywords");
            for (int i = 0; i < array.length(); i++) {
                instrumentNames.add(array.getString(i));
            }
            questionModel.setKeywords(instrumentNames);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        //get data
       /* questionModel = new QuestionModel();
        String[] instrumentNames = {"Firstly", "activity", "MainActivityPresenter", "through", "Interface"};
        questionModel.setParagraph("Firstly, This activity implements the MainActivityPresenter.View Interface through which it’s overridden method will be called. Secondly, we have to create the MainActivityPresenter object with view as a constructor. We use this presenter object to listen the user input and update the data as well as view. I think that's better to leverage the xml shape drawable resource power if that fits your needs. With a from scratch project (for android-8), define res/layout/main.xml");
        questionModel.setKeywords(new ArrayList(Arrays.asList(instrumentNames)));*/
        //view.showParagraph(questionModel);
    }

    public void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(quetionModelList);
            for (int i = 0; i < quetionModelList.size(); i++) {
                if (perc < 95) {
                    questionModel = quetionModelList.get(i);
                    break;
                    /*if (!checkWord("" + quetionModelList.get(i).getTitle())) {
                        questionModel = quetionModelList.get(i);
                        break;
                    }*/
                } else {
                    questionModel = quetionModelList.get(i);
                    break;
                }
            }
            view.showParagraph(questionModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = quetionModelList.size();
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

    /*private boolean checkWord(String wordStr) {
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
    }*/

    private int getLearntWordsCount() {
        int count = 0;
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        return count;
    }

    public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {
            File direct = new File(activityPhotoPath);
            File file = new File(direct, fileName);
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // isPhotoSaved = true;
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLearntWords(ScienceQuestion questionModel, String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FC_Constants.currentStudentID);
            keyWords.setKeyWord(questionModel.getTitle());
            keyWords.setWordType("word");
            addScore(GameConstatnts.getInt(questionModel.getQid()), GameConstatnts.PARAGRAPH_WRITING, 0, 0, FC_Utility.getCurrentDateTime(), imageName);
//            appDatabase.getKeyWordDao().insert(keyWords);
            Toast.makeText(context, "inserted succussfully", Toast.LENGTH_LONG).show();
            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);

        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);

        }
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
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(Word + "___" + Label);
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
                assessment.setLabel("test: ___" + Label);
                assessment.setSentFlag(0);
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
