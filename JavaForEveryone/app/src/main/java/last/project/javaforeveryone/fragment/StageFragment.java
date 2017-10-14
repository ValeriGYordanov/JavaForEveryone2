package last.project.javaforeveryone.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import last.project.javaforeveryone.model.DBOperations;
import last.project.javaforeveryone.R;
import last.project.javaforeveryone.iface.IFragmentChangeListener;
import last.project.javaforeveryone.model.UserModel;
import last.project.javaforeveryone.utility.Utils;

public class StageFragment extends AnimatedFragment {

    private View rootView;
    private Bundle args;
    private String title, text;
    private String[] subtitles = new String[5];
    private TextView edtSubtitle, edtText;
    private int idx;
    private Button btnNext;
    private String question;
    private ArrayList<String> answerOptions = new ArrayList();
    private String correctAnswer;
    private UserModel currentUserModel;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("title", title);
        outState.putInt("index", idx);
        outState.putSerializable("currentUser", currentUserModel);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stage, container, false);
        edtText = (TextView) rootView.findViewById(R.id.txt_stages_info);
        if (savedInstanceState != null) {
            String savedTitle = (String) savedInstanceState.getCharSequence("title");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(savedTitle);
            int index = savedInstanceState.getInt("index");
            this.idx = index;
            this.title = savedTitle;
            currentUserModel = (UserModel) savedInstanceState.getSerializable("currentUser");
        }
        setUpButtons();
        setInfo();

        return rootView;
    }


    public static StageFragment newInstance(Bundle args) {
        StageFragment frag = new StageFragment();
        frag.setArguments(args);
        frag.args = args;
        frag.title = (String) args.getCharSequence("title");
        frag.idx = args.getInt("index");
        frag.currentUserModel = (UserModel) args.getSerializable("currentUser");

        return frag;

    }

    /**
     * Setting up the fragment buttons
     * and prompts and window with a simple question
     * if answered correctly and continue button clicked
     * the next Substage is shown.
     */
    private void setUpButtons() {
        edtSubtitle = (TextView) rootView.findViewById(R.id.edt_subtitle_frag);
        btnNext = (Button) rootView.findViewById(R.id.btn_next_frag);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyChoiceDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setTopColorRes(R.color.authui_colorAccent)
                        .setTitle(question)
                        .setTitleGravity(Gravity.LEFT)
                        .setIcon(R.drawable.ic_lightbulbmind)
                        .setItemsMultiChoice(answerOptions, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                            @Override
                            public void onItemsSelected(List<Integer> positions, List<String> questions) {
                                if (questions.isEmpty() || questions.size() >= 2 || (!correctAnswer.equalsIgnoreCase(questions.get(0)))) {
                                    Utils.createToast(getActivity(), "Може би трябва да опиташ отново...");
                                    return;
                                } else {
                                    Utils.createToast(getActivity(), "Браво, ти успя!");
                                    showOtherFragment();
                                }
                            }
                        })
                        .setConfirmButtonText("Предай")
                        .show();
            }
        });

    }

    /**
     * Setts up the info to the question
     * title, and all the answers.
     * Taken the questions info from
     * the Local DB.
     */
    private void setInfo() {
        if (title.equalsIgnoreCase(getResources().getString(R.string.stages_intro_txt))) {
            subtitles[0] = "Класове";
            subtitles[1] = "Обекти";
            if (subtitles[idx] == null || subtitles[idx].isEmpty()) {
                goToTest(title);
                return;
            }
            edtSubtitle.setText(subtitles[idx]);
            ArrayList<String> substageIngo = DBOperations.getInstance(getActivity()).getSubstageInfo(subtitles[idx]);
            //Vrushta v indexi 0-Vupros, 1-tekst, 2-otgovor
            question = substageIngo.get(0);
            text = substageIngo.get(1);
            correctAnswer = substageIngo.get(2);
            if (idx == 0) {
                answerOptions.add("Класът е Обект.");
                answerOptions.add("Съвкупност от Обекти.");
                answerOptions.add("Класа се създава по шаблон от обекта.");
            } else {
                answerOptions.add("Шаблон за създаване на Класове.");
                answerOptions.add("Метод за създаване на инстанция.");
                answerOptions.add("Константа в Java");
            }
            answerOptions.add(new Random().nextInt(answerOptions.size()), correctAnswer);
            //Raboti po dobre s nova versiq no vse pak raboti i sus stari
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                edtText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            } else {
                edtText.setText(Html.fromHtml(text));
            }
            return;
        }
        if (title.equalsIgnoreCase(getResources().getString(R.string.stages_oop_txt))) {
            subtitles[0] = "Капсулация";
            subtitles[1] = "Наследяване";
            subtitles[2] = "Абстракция";
            subtitles[3] = "Полиморфизъм";
            if (subtitles[idx] == null || subtitles[idx].isEmpty()) {
                goToTest(title);
                return;
            }
            edtSubtitle.setText(subtitles[idx]);
            ArrayList<String> substageInfo = DBOperations.getInstance(getActivity()).getSubstageInfo(subtitles[idx]);
            question = substageInfo.get(0);
            text = substageInfo.get(1);
            correctAnswer = substageInfo.get(2);
            if (idx == 0) {
                answerOptions.add("Само до пакета.");
                answerOptions.add("Само до наследниците.");
                answerOptions.add("Само за класа.");
            } else if (idx == 1) {
                answerOptions.add("Много.");
                answerOptions.add("Не може да наследи.");
            } else if (idx == 2) {
                answerOptions.add("Всички обекти в Java.");
                answerOptions.add("Интерфейс.");
                answerOptions.add("В Java няма такава дефиниция.");
            } else {
                answerOptions.add("Не може да постигнем Полиморфизъм.");
                answerOptions.add("Само Overload.");
                answerOptions.add("Constructor Chaining.");
            }
            answerOptions.add(new Random().nextInt(answerOptions.size()), correctAnswer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                edtText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            } else {
                edtText.setText(Html.fromHtml(text));
            }
            return;
        }
        if (title.equalsIgnoreCase(getResources().getString(R.string.stages_collections_txt))) {
            subtitles[0] = "Лист";
            subtitles[1] = "Сет";
            subtitles[2] = "Мап";
            if (subtitles[idx] == null || subtitles[idx].isEmpty()) {
                goToTest(title);
                return;
            }
            edtSubtitle.setText(subtitles[idx]);
            ArrayList<String> substageInfo = DBOperations.getInstance(getActivity()).getSubstageInfo(subtitles[idx]);
            question = substageInfo.get(0);
            text = substageInfo.get(1);
            correctAnswer = substageInfo.get(2);
            if (idx == 0) {
                answerOptions.add("Логаритмична.");
                answerOptions.add("Квадратична.");
                answerOptions.add("Линейна.");
                answerOptions.add("Кубична.");
            } else if (idx == 1) {
                answerOptions.add("Константна.");
                answerOptions.add("Квадратична.");
                answerOptions.add("Линейна.");
                answerOptions.add("Кубична.");
            } else {
                answerOptions.add("Да.");
            }
            answerOptions.add(new Random().nextInt(answerOptions.size()), correctAnswer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                edtText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            } else {
                edtText.setText(Html.fromHtml(text));
            }
            return;
        }
        if (title.equalsIgnoreCase(getResources().getString(R.string.stages_iterations_txt))) {
            subtitles[0] = "Iterator и Foreach";
            subtitles[2] = "Comparator vs. Comparable";
            subtitles[3] = "Wrapper Classes";
            if (subtitles[idx] == null || subtitles[idx].isEmpty()) {
                goToTest(title);
                return;
            }
            edtSubtitle.setText(subtitles[idx]);
            ArrayList<String> substageInfo = DBOperations.getInstance(getActivity()).getSubstageInfo(subtitles[idx]);
            question = substageInfo.get(0);
            text = substageInfo.get(1);
            correctAnswer = substageInfo.get(2);
            if (idx == 0) {
                answerOptions.add("Няма разлика.");
                answerOptions.add("Foreach не се ползва за колекции.");
                answerOptions.add("Iterator не се ползва за колекции.");
            } else if (idx == 1) {
                answerOptions.add("Не.");
            } else {
                answerOptions.add("true.");
                answerOptions.add("false.");
                answerOptions.add("Грешка по време на изпълнение.");
            }
            answerOptions.add(new Random().nextInt(answerOptions.size()), correctAnswer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                edtText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            } else {
                edtText.setText(Html.fromHtml(text));
            }
            return;
        }
        if (title.equalsIgnoreCase(getResources().getString(R.string.stages_inner_classes_txt))) {
            subtitles[0] = "Вложени и Анонимни";
            if (subtitles[idx] == null || subtitles[idx].isEmpty()) {
                goToTest(title);
                return;
            }
            edtSubtitle.setText(subtitles[idx]);
            ArrayList<String> substageInfo = DBOperations.getInstance(getActivity()).getSubstageInfo(subtitles[idx]);
            question = substageInfo.get(0);
            text = substageInfo.get(1);
            correctAnswer = substageInfo.get(2);
            answerOptions.add("Да.");
            answerOptions.add("Вътрешните класове не може да са статични.");
            answerOptions.add(new Random().nextInt(answerOptions.size()), correctAnswer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                edtText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            } else {
                edtText.setText(Html.fromHtml(text));
            }
            return;
        }
    }

    /**
     * Shows the next fragment depending
     * on the length of the stages.
     */
    public void showOtherFragment() {
        Bundle bund = new Bundle();
        bund.putCharSequence("title", title);
        bund.putInt("index", ++idx);
        bund.putSerializable("currentUser", currentUserModel);
        Fragment fr = StageFragment.newInstance(bund);
        IFragmentChangeListener fc = (IFragmentChangeListener) getActivity();
        fc.replaceFragment(fr);

    }

    /**
     * Replacing the current fragment
     * to a exam fragment depending on the
     * stage that was currently showing.
     * Needed for adequate questions to be
     * shown.
     *
     * @param stage - current stage.
     */
    private void goToTest(String stage) {
        edtSubtitle.setVisibility(View.INVISIBLE);
        edtText.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        new LovelyStandardDialog(getContext())
                .setTopColorRes(R.color.colorPrimaryDark)
                .setButtonsColorRes(R.color.colorPrimaryDark)
                .setIcon(R.drawable.ic_exam_icon)
                .setTitle("Яви се на тест.")
                .setMessage("Желаете ли да се явите на тест за " + stage)
                .setCancelable(false)
                .setPositiveButton(R.string.possitive_txt, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bund = new Bundle();
                        bund.putCharSequence("title", title);
                        bund.putSerializable("currentUser", currentUserModel);
                        bund.putInt("index", idx);
                        Fragment fragment = new ExamFragment();
                        fragment.setArguments(bund);
                        IFragmentChangeListener fc = (IFragmentChangeListener) getActivity();
                        fc.replaceFragment(fragment);

                    }
                })
                .setNegativeButton(R.string.negative_txt, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().beginTransaction().remove(StageFragment.this)
                                .commit();
                        getActivity().recreate();
                    }
                })
                .show();
    }
}
