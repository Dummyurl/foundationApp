package com.pratham.foundation.ui.app_home.profile_new.display_image_ques_list;
import com.pratham.foundation.database.domain.Score;

import java.util.List;

public interface ImageQuesContract {

    interface ImageQuesItemClicked {
        void gotoQuestions(Score score);
    }

    interface ImageQuesView {
        void addToAdapter(List<Score> scoreList);

        void showNoData();
    }

    interface ImageQuesPresenter {
        void setView(ImageQuesContract.ImageQuesView imageQuesView);

        void showQuestion();
    }
}
