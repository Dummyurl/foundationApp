package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public interface SequeanceLayoutContract {
    public interface SequenceLayoutView {
        void loadUI();
    }

    public interface SequenceLayoutPresenter {
        void getData();

        void setView(SequeanceLayoutContract.SequenceLayoutView sequenceLayoutView);
    }

    interface clickListner{
        void contentClicked(ContentTable contentTable);
    }
}
