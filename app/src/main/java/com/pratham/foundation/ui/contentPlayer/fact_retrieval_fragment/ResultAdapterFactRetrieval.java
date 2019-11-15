package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ResultAdapterFactRetrieval extends RecyclerView.Adapter<ResultAdapterFactRetrieval.MyViewHolder> {
    List<ScienceQuestionChoice> scienceQuestionList;
    Context context;
    String path;


    public ResultAdapterFactRetrieval(ArrayList<ScienceQuestionChoice> optionList, Context context, String path) {
        this.scienceQuestionList = optionList;
        this.context = context;
        this.path = path;
    }


    @NonNull
    @Override
    public ResultAdapterFactRetrieval.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_result_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final ScienceQuestionChoice scienceQuestionChoice = scienceQuestionList.get(i);
        myViewHolder.question.setText(scienceQuestionChoice.getSubQues());
        myViewHolder.questionImg.setVisibility(View.GONE);
        if (scienceQuestionChoice.isTrue()) {
            myViewHolder.iv_correct_wrong_indicator.setImageResource(R.drawable.ic_check_black);
            myViewHolder.iv_correct_wrong_indicator.setBackgroundColor(context.getResources().getColor(R.color.level_1_color));
        } else {
            myViewHolder.iv_correct_wrong_indicator.setImageResource(R.drawable.ic_close_black_24dp);
            myViewHolder.iv_correct_wrong_indicator.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        }
        myViewHolder.correctAnswer.setText(scienceQuestionChoice.getAnsInPassage());
        myViewHolder.userAnswer.setText(scienceQuestionChoice.getUserAns());
    }



    @Override
    public int getItemCount() {
        return scienceQuestionList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView question, userAnswer, correctAnswer, tv_you_answered_label;
        CardView cardView;
        ImageView questionImg, btncorrectAnswer, btnUserAnswer;
        ImageView iv_correct_wrong_indicator;
        LinearLayout ll_correct_ans, ll_user_ans;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.tv_result_question);
            tv_you_answered_label = itemView.findViewById(R.id.tv_you_answered_label);
            questionImg = itemView.findViewById(R.id.question_img);
            userAnswer = itemView.findViewById(R.id.tv_you_answered);
            correctAnswer = itemView.findViewById(R.id.tv_correct_answer);
            btnUserAnswer = itemView.findViewById(R.id.useranswer);
            btncorrectAnswer = itemView.findViewById(R.id.btn_correct_answer);
            cardView = itemView.findViewById(R.id.result_card_view);
            ll_correct_ans = itemView.findViewById(R.id.ll_correct_ans);
            ll_user_ans = itemView.findViewById(R.id.ll_user_ans);
            iv_correct_wrong_indicator = itemView.findViewById(R.id.iv_correct_wrong_indicator);
        }
    }

}