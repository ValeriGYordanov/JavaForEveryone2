package last.project.javaforeveryone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import last.project.javaforeveryone.model.DBOperations;
import last.project.javaforeveryone.model.ExamAdapter;
import last.project.javaforeveryone.model.QuestionModel;
import last.project.javaforeveryone.R;
import last.project.javaforeveryone.iface.IOnOptionSelected;
import last.project.javaforeveryone.model.AchievementModel;
import last.project.javaforeveryone.model.UserModel;
import last.project.javaforeveryone.utility.Utils;

public class ExamFragment extends AnimatedFragment implements IOnOptionSelected {

    private RecyclerView recycler;
    private Button btnFinish, btnGiveup;
    private View rootView;
    private UserModel currentUserModel;
    private int stageIndex;
    private String currentTitle;
    private ArrayList<QuestionModel> questions = new ArrayList<>();
    private ExamAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recycler_view_fragment,container,false);

        //Setting up buttons
        btnFinish = (Button) rootView.findViewById(R.id.btn_finish_test);
        btnGiveup = (Button) rootView.findViewById(R.id.btn_give_up_test);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        //Getting the currentUser, the stageIDX and the title.
        if(bundle != null){
            currentUserModel = (UserModel) bundle.getSerializable("currentUser");
            stageIndex = bundle.getInt("index");
            currentTitle = bundle.getString("title");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Setting up recycler layout
        recycler = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Setts up arrayList depending on the currentTitle...
        final ArrayList<ArrayList<String>> examFromDB;

        if(currentTitle.equals(getResources().getString(R.string.last_test_txt))){
            examFromDB = DBOperations.getInstance(getActivity()).getFinalTestInfo();
            for (int i = 0; i < examFromDB.size(); i++){
                questions.add(new QuestionModel(examFromDB.get(i).get(0), examFromDB.get(i).get(1), examFromDB.get(i).get(2), examFromDB.get(i).get(3)));
            }
            adapter = new ExamAdapter(getActivity(),getActivity().getApplicationContext(),questions,currentTitle, currentUserModel);
        }else{
            examFromDB = DBOperations.getInstance(getActivity()).getStageInfo(currentTitle);
            for (int i = 0; i < examFromDB.size(); i++){
                questions.add(new QuestionModel(examFromDB.get(i).get(0), examFromDB.get(i).get(1), examFromDB.get(i).get(2), examFromDB.get(i).get(3)));
            }
            adapter = new ExamAdapter(getActivity(),getActivity().getApplicationContext(),questions,currentTitle, currentUserModel);
        }

        adapter.setQuestionModels(questions);
        adapter.setIOnOptionSelected(this);
        recycler.setAdapter(adapter);

        btnGiveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.createToast(getActivity(), R.drawable.ic_btn_devil, "ХА - ХА - ХА");
                Utils.playSound(getContext(), R.raw.devils_laugh);
                endTestAndSave();
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = adapter.getResultFromExam();

                if(result < 50){
                    Utils.createToast(getActivity(), "Не успя да си вземеш теста... Почети още малко...");
                    if(result == 0){
                        if(!(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_fail_title)))){
                            currentUserModel.setAch(new AchievementModel(getResources().getString(R.string.achievement_fail_title),getResources().getString(R.string.achievement_fail_desc),getResources().getInteger(R.integer.achievement_fail_pts)));
                            Utils.createToast(getActivity(), R.drawable.ic_achievement_icon,getString(R.string.SuccsessAch)+ " " +(getResources().getString(R.string.achievement_fail_title)));
                            Utils.playSound(getContext(), R.raw.exam_failed);
                        }
                    }
                    Utils.playSound(getContext(), R.raw.exam_failed);
                }else if(result > 50 && result <= 100){
                    Utils.createToast(getActivity(), "Поздравления, ти успя!");
                    checkUserStage();
                    if(currentUserModel.getStagesID() > 5 && !(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_almost_title))) && !(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_fail_title)))){
                        if(!(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_freak_title)))){
                            currentUserModel.setAch(new AchievementModel(getResources().getString(R.string.achievement_freak_title),getResources().getString(R.string.achievement_freak_desc),getResources().getInteger(R.integer.achievement_freak_pts)));
                            Utils.createToast(getActivity(), R.drawable.ic_achievement_icon,getString(R.string.SuccsessAch)+ " " +(getResources().getString(R.string.achievement_freak_title)));
                            Utils.playSound(getContext(), R.raw.achievement_unlocked);
                        }
                    }
                    if(!(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_first_test_title)))){
                        currentUserModel.setAch(new AchievementModel(getResources().getString(R.string.achievement_first_test_title),getResources().getString(R.string.achievement_first_test_desc),getResources().getInteger(R.integer.achievement_first_test_pts)));
                        Utils.createToast(getActivity(), R.drawable.ic_achievement_icon,getString(R.string.SuccsessAch)+ " " +(getResources().getString(R.string.achievement_first_test_title)));
                        Utils.playSound(getContext(), R.raw.achievement_unlocked);
                    }
                    if(result == 100) {
                        if (!(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_machine_title)))) {
                            currentUserModel.setAch(new AchievementModel(getResources().getString(R.string.achievement_machine_title),getResources().getString(R.string.achievement_machine_desc),getResources().getInteger(R.integer.achievement_machine_pts)));
                            Utils.createToast(getActivity(), R.drawable.ic_achievement_icon,getString(R.string.SuccsessAch)+ " " +(getResources().getString(R.string.achievement_machine_title)));
                            Utils.playSound(getContext(), R.raw.achievement_unlocked);
                        }
                    }else{
                        if(!(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_almost_title)))){
                            currentUserModel.setAch(new AchievementModel(getResources().getString(R.string.achievement_almost_title),getResources().getString(R.string.achievement_almost_desc),getResources().getInteger(R.integer.achievement_almost_pts)));
                            Utils.createToast(getActivity(), R.drawable.ic_achievement_icon,getString(R.string.SuccsessAch)+ " " +(getResources().getString(R.string.achievement_almost_title)));
                            Utils.playSound(getContext(), R.raw.achievement_unlocked);
                        }
                    }
                }else{
                    if(currentTitle.equals(getResources().getString(R.string.last_test_txt))){
                        if(!(currentUserModel.containsAchByName(getResources().getString(R.string.achievement_talent_title)) )&& result == 1000){
                            currentUserModel.setAch(new AchievementModel(getResources().getString(R.string.achievement_talent_title),getResources().getString(R.string.achievement_talent_desc),getResources().getInteger(R.integer.achievement_talent_pts)));
                            Utils.createToast(getActivity(), R.drawable.ic_achievement_icon,getString(R.string.SuccsessAch)+ " " +(getResources().getString(R.string.achievement_talent_title)));
                            Utils.playSound(getContext(), R.raw.achievement_unlocked);
                        }
                    }
                }

                endTestAndSave();
            }

        });

    }


    /**
     * Removes the current fragment and saves the user data
     * in firebase. Not depending on if the user completed
     * the test or not.
     */
    private void endTestAndSave(){
        saveUserResult();
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.fragment_place))
                .commit();
        getActivity().recreate();
    }

    /**
     * Changes the user stageID to next level
     * if he has passed the exam successfully.
     */
    private void checkUserStage() {

        if(currentTitle.equals(getResources().getString(R.string.stages_intro_txt))){
            if(currentUserModel.getStagesID() <= 1){
                currentUserModel.setStagesID(2);
            }
        }
        else if(currentTitle.equals(getResources().getString(R.string.stages_oop_txt))){
            if(currentUserModel.getStagesID() <= 2){
                currentUserModel.setStagesID(3);
            }
        }
        else if(currentTitle.equals(getResources().getString(R.string.stages_collections_txt))){
            if(currentUserModel.getStagesID() <= 3){
                currentUserModel.setStagesID(4);
            }
        }
        else if(currentTitle.equals(getResources().getString(R.string.stages_iterations_txt))){
            if(currentUserModel.getStagesID() <= 4){
                currentUserModel.setStagesID(5);
            }
        }
        else if(currentTitle.equals(getResources().getString(R.string.stages_inner_classes_txt))){
            if(currentUserModel.getStagesID() <= 5){
                currentUserModel.setStagesID(6);
            }
        }

    }


    /**
     * Saves the user's DATA to Firebase.
     */
    private void saveUserResult() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = firebaseUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(userID).setValue(currentUserModel);
    }

    /**
     * Sets up the concrete RadioButton to checked
     * depending on the position of the Row and
     * the item selected.
     *
     * Method required by the IOptionSelected
     * Imitating onCheckedChangeListener.
     *
     * @param position - Exact position.
     * @param itemSelected - The selected item.
     */
    @Override
    public void onOptionSelected(int position, int itemSelected) {
        questions.get(position).setSelectedAnswerPostion(itemSelected);
        switch (itemSelected){
            case 1:
                questions.get(position).setOption1Selected(true);
                break;
            case 2:
                questions.get(position).setOption2Selected(true);
                break;
            case 3:
                questions.get(position).setOption3Selected(true);
                break;
        }
        adapter.setQuestionModels(questions);
        adapter.notifyDataSetChanged();
    }
}

