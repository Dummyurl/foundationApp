package com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EFragment(R.layout.fragment_select_group)
public class FragmentSelectGroup extends Fragment implements ContractGroup {

    @ViewById(R.id.rv_group)
    RecyclerView rv_group;
    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;

    GroupAdapter groupAdapter;
    ArrayList<Groups> groups;
    Groups groupSelected;

    @AfterViews
    public void initialize() {
        try {
            ArrayList<String> present_groups = new ArrayList<>();
            String groupId1 = AppDatabase.appDatabase.getStatusDao().getValue(FC_Constants.GROUPID1);

            if (!groupId1.equalsIgnoreCase("0")) present_groups.add(groupId1);
            String groupId2 = AppDatabase.appDatabase.getStatusDao().getValue(FC_Constants.GROUPID2);

            if (!groupId2.equalsIgnoreCase("0")) present_groups.add(groupId2);
            String groupId3 = AppDatabase.appDatabase.getStatusDao().getValue(FC_Constants.GROUPID3);

            if (!groupId3.equalsIgnoreCase("0")) present_groups.add(groupId3);
            String groupId4 = AppDatabase.appDatabase.getStatusDao().getValue(FC_Constants.GROUPID4);

            if (!groupId4.equalsIgnoreCase("0")) present_groups.add(groupId4);
            String groupId5 = AppDatabase.appDatabase.getStatusDao().getValue(FC_Constants.GROUPID5);

            if (!groupId5.equalsIgnoreCase("0")) present_groups.add(groupId5);

            if (getArguments().getBoolean(FC_Constants.GROUP_AGE_BELOW_7)) {
                get3to6Groups(present_groups);
            } else {
                get8to14Groups(present_groups);
            }
            setGroups(groups);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get3to6Groups(ArrayList<String> allGroups) {
        groups = new ArrayList<>();
        for (String grID : allGroups) {
            // ArrayList<Student> students = (ArrayList<Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            ArrayList<Student> students = (ArrayList<Student>) AppDatabase.appDatabase.getStudentDao().getGroupwiseStudents(grID);
            for (Student stu : students) {
                if (stu.getAge() < 7) {
                    //Groups group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    Groups group = AppDatabase.appDatabase.getGroupsDao().getGroupByGrpID(grID);
                    groups.add(group);
                    break;
                }
            }
        }
    }

    private void get8to14Groups(ArrayList<String> allGroups) {
        groups = new ArrayList<>();
        for (String grID : allGroups) {
            //ArrayList<Student> students = (ArrayList<Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            ArrayList<Student> students = (ArrayList<Student>) AppDatabase.appDatabase.getStudentDao().getGroupwiseStudents(grID);

            for (Student stu : students) {
                if (stu.getAge() >= 7) {
                    // group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    Groups group = AppDatabase.appDatabase.getGroupsDao().getGroupByGrpID(grID);
                    groups.add(group);
                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setGroups(ArrayList<Groups> groups) {
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(getActivity(), groups, FragmentSelectGroup.this);
            rv_group.setHasFixedSize(true);
            // rv_group.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rv_group.setLayoutManager(mLayoutManager);
            rv_group.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            rv_group.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.setAdapter(adapter);

            rv_group.setAdapter(groupAdapter);
        } else {
            groupAdapter.updateGroupItems(groups);
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Click(R.id.btn_group_next)
    public void setNext(View v) {
        if (groupSelected != null) {
            //todo remove comment
          //  ApplicationClass.bubble_mp.start();
            ArrayList<Student> students = new ArrayList<>();
            students.addAll(AppDatabase.appDatabase.getStudentDao().getGroupwiseStudents(groupSelected.getGroupId()));
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(FC_Constants.STUDENT_LIST, students);
            bundle.putString(FC_Constants.GROUPID, groupSelected.getGroupId());
            FC_Utility.showFragment(getActivity(), new FragmentChildAttendance(), R.id.frame_group,
                    bundle, FragmentChildAttendance.class.getSimpleName());
        } else {
            Toast.makeText(getContext(), "Please select Group !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void groupItemClicked(Groups modalGroup, int position) {
        groupSelected = modalGroup;
        for (Groups gr : groups) {
            if (gr.getGroupId().equalsIgnoreCase(modalGroup.getGroupId())) {
                gr.setSelected(true);
            } else
                gr.setSelected(false);
        }
        setGroups(groups);
    }

    //    public void presentActivity(View view) {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getActivity(), view, "transition");
//        Intent intent = new Intent(getActivity(), ActivityMain.class);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
//        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//    }
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