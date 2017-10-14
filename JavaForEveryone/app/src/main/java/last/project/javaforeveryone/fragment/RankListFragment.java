package last.project.javaforeveryone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.TreeSet;

import last.project.javaforeveryone.R;
import last.project.javaforeveryone.model.RankAdapter;
import last.project.javaforeveryone.model.UserModel;

/**
 * Created by plame_000 on 04-Nov-17.
 */

public class RankListFragment extends AnimatedFragment {

    private View parentView;
    private RecyclerView recycler;
    private UserModel currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.rank_recycler_fragment, container, false);
        currentUser = (UserModel) this.getArguments().get("user");

        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recycler = (RecyclerView) getActivity().findViewById(R.id.recycler_view_rank);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        final TreeSet<UserModel> userRank = new TreeSet(new UserModel.CustomComparator());
        final ArrayList<UserModel> arrModels = new ArrayList<>();

        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDB.getReference();

        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();

                for (DataSnapshot data : childrens) {
                    UserModel user = data.getValue(UserModel.class);
                    userRank.add(user);
                }

                arrModels.addAll(userRank);

                RankAdapter adapter = new RankAdapter(getContext(),arrModels, currentUser.getEmail());
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
