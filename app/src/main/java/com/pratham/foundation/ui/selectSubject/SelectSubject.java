package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_select_subject)
public class SelectSubject extends BaseActivity implements SelectSubjectContract.View {

    @Bean(SelectSubjectPresenter.class)
    SelectSubjectContract.Presenter presenter;

    @ViewById(R.id.subject_recycler)
    RecyclerView subject_recycler;
    @ViewById(R.id.name)
    TextView name;
    private Context context;
    SelectSubjectAdapter subjectAdapter;
    String studName;

    @AfterViews
    protected void initiate() {
        // super.onCreate(savedInstanceState);
        //setContentView();
        studName = getIntent().getStringExtra("studName");
        context = SelectSubject.this;
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        List<ContentTable> subjectList = presenter.getSubjectList();

        name.setText("Welcome "+studName+".");

        subjectAdapter = new SelectSubjectAdapter(this, subjectList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        subject_recycler.setLayoutManager(mLayoutManager);
        int dp = 12;
        if (FC_Constants.TAB_LAYOUT)
            dp = 20;

        subject_recycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(dp), true));
        subject_recycler.setItemAnimator(new DefaultItemAnimator());
        subject_recycler.setAdapter(subjectAdapter);

    }

    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + avatar) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + avatar) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}