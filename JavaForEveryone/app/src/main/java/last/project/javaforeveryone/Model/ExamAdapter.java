package last.project.javaforeveryone.model;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import last.project.javaforeveryone.R;
import last.project.javaforeveryone.iface.IOnOptionSelected;
import last.project.javaforeveryone.utility.Utils;

/**
 * Created by plame_000 on 28-Oct-17.
 */

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private List<QuestionModel> questionModels;
    private IOnOptionSelected IOnOptionSelected;
    private Context ctx;
    private Activity act;
    private HashMap<Integer, String > userAnswers = new HashMap<>();
    private ArrayList<String> correctAnswers = new ArrayList<>();
    private String currentTitle;
    private UserModel userModel;
    private int availableHelpTimes;

    public void setIOnOptionSelected(IOnOptionSelected IOnOptionSelected) {
        this.IOnOptionSelected = IOnOptionSelected;
    }

    public void setQuestionModels(List<QuestionModel> questionModels) {
        this.questionModels = questionModels;
    }

    public ExamAdapter(Activity act, Context ctx, ArrayList<QuestionModel> questionModels, String currentTitle, UserModel userModel) {
        this.questionModels = questionModels;
        this.currentTitle = currentTitle;
        this.userModel = userModel;
        this.ctx = ctx;
        this.act = act;
        for (int i = 0; i < questionModels.size(); i++){
            correctAnswers.add(questionModels.get(i).getCorrectAns());
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_recycler_row, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.question.setText(questionModels.get(position).getQuestion());
        final ViewHolder holder = viewHolder;
        if (currentTitle.equals(ctx.getString(R.string.last_test_txt))){
            viewHolder.helpLayout.setVisibility(View.VISIBLE);
            float scale = ctx.getResources().getDisplayMetrics().density;
            int pixels = (int) (450 * scale + 0.5f);
            viewHolder.parentLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
        }

        availableHelpTimes = (userModel.getAchPts()+1)/10;
        viewHolder.btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(act)
                        .setTopColorRes(R.color.colorAccentTransition)
                        .setIcon(R.drawable.ic_btn_help)
                        .setTitle("Молиш ли се за помощ?!")
                        .setButtonsColorRes(R.color.colorPrimaryDark)
                        .setPositiveButton(R.string.possitive_txt, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int userPts = userModel.getAchPts();
                                if (userPts >= 10) {
                                    userModel.setAchPts(userPts - 10);
                                    availableHelpTimes = (userModel.getAchPts()+1)/10;
                                    holder.helpCounter.setText("Остават " + availableHelpTimes + " помощни точки.");
                                    notifyDataSetChanged();
                                    if (holder.chkAnswer1.getText().toString().equals(correctAnswers.get(position))) {
                                        holder.chkAnswer1.setChecked(true);
                                        questionModels.get(position).setOption1Selected(true);
                                    }
                                    if (holder.chkAnswer2.getText().toString().equals(correctAnswers.get(position))) {
                                        holder.chkAnswer2.setChecked(true);
                                        questionModels.get(position).setOption2Selected(true);
                                    }
                                    if (holder.chkAnswer3.getText().toString().equals(correctAnswers.get(position))) {
                                        holder.chkAnswer3.setChecked(true);
                                        questionModels.get(position).setOption3Selected(true);
                                    }
                                } else {
                                    Utils.createToast(act,"Нямате достатъчно точки за помощ! Постарай се!");
                                }
                            }
                        })
                        .setNegativeButton(R.string.negative_txt,null)
                        .show();

            }
        });
        viewHolder.helpCounter.setText("Остават " + availableHelpTimes + " помощни точки.");

        viewHolder.chkAnswer1.setText(questionModels.get(position).getOption1Text());
        viewHolder.chkAnswer2.setText(questionModels.get(position).getOption2Text());
        viewHolder.chkAnswer3.setText(questionModels.get(position).getOption3Text());

        if (questionModels.get(position).isOption1Selected()){
            userAnswers.put(position, questionModels.get(position).getOption1Text());
        }else if (questionModels.get(position).isOption2Selected()){
            userAnswers.put(position, questionModels.get(position).getOption2Text());
        }else if (questionModels.get(position).isOption3Selected()){
            userAnswers.put(position, questionModels.get(position).getOption3Text());
        }

        viewHolder.chkAnswer1.setChecked(questionModels.get(position).isOption1Selected());
        viewHolder.chkAnswer2.setChecked(questionModels.get(position).isOption2Selected());
        viewHolder.chkAnswer3.setChecked(questionModels.get(position).isOption3Selected());

    }

    @Override
    public int getItemCount() {
        if (questionModels != null) {
            return questionModels.size();
        }
        return 0;
    }

    public int getResultFromExam() {
        int count = 0;

        for (int i = 0; i < correctAnswers.size(); i++) {
            if (userAnswers.get(i) == null){
                continue;
            }
            if (userAnswers.get(i).equals(correctAnswers.get(i))) {
                count++;
            }
        }

        if (count == 0) {
            return 0;
        } else if (count == 1) {
            return 33;
        } else if (count == 2) {
            return 66;
        } else if (count == 3){
            return 100;
        } else if (count == 15){
            return 1000;
        } else {
            return 500;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView question, helpCounter;
        private LinearLayout helpLayout, parentLayout;
        private RadioButton chkAnswer1, chkAnswer2, chkAnswer3;
        private Button btnHelp;

        ViewHolder(View view) {
            super(view);
            question = (TextView) view.findViewById(R.id.txt_recycler_question);
            helpLayout = (LinearLayout) view.findViewById(R.id.layout_help_exam);
            parentLayout = (LinearLayout) view.findViewById(R.id.exam_row_parent_layout);
            chkAnswer1 = (RadioButton) view.findViewById(R.id.chk_recycler_answer1);
            chkAnswer2 = (RadioButton) view.findViewById(R.id.chk_recycler_answer2);
            chkAnswer3 = (RadioButton) view.findViewById(R.id.chk_recycler_answer3);
            helpCounter = (TextView) view.findViewById(R.id.txt_count_exam);
            btnHelp = (Button) view.findViewById(R.id.btn_help);

            chkAnswer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOnOptionSelected.onOptionSelected(getAdapterPosition(), 1);
                }
            });
            chkAnswer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOnOptionSelected.onOptionSelected(getAdapterPosition(), 2);
                }
            });
            chkAnswer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOnOptionSelected.onOptionSelected(getAdapterPosition(), 3);
                }
            });
        }
    }
}
