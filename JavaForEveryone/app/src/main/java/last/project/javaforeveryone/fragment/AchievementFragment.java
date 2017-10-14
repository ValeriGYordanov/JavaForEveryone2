package last.project.javaforeveryone.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import last.project.javaforeveryone.model.AchievementAdapter;
import last.project.javaforeveryone.model.DBOperations;
import last.project.javaforeveryone.R;
import last.project.javaforeveryone.model.UserModel;

public class AchievementFragment extends AnimatedFragment {

    private View parentView;
    private RecyclerView recycler;

    public AchievementFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_achievement, container, false);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Getting the recycler view id.
        recycler = (RecyclerView) getActivity().findViewById(R.id.achievement_rec_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Getting the arraylist of achievements from the Locak DB.
        ArrayList arrWithAchievemnts = DBOperations.getInstance(getContext()).getAchievements();

        //Getting the current User from bundle.
        UserModel userModel = (UserModel) getArguments().getSerializable("user");

        //Setting up the AchievementAdapter.
        AchievementAdapter adapter = new AchievementAdapter(getActivity(),arrWithAchievemnts, userModel);
        recycler.setAdapter(adapter);

    }
}
