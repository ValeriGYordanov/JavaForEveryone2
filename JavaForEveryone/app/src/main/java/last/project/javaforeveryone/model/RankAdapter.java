package last.project.javaforeveryone.model;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import last.project.javaforeveryone.R;

/**
 * Created by plame_000 on 04-Nov-17.
 */

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context ctx;
    private ArrayList<UserModel> test;
    private String currentUserEmail;
    private View view;

    public RankAdapter(Context context, ArrayList<UserModel> userArray, String email){
        this.mInflater = LayoutInflater.from(context);
        this.ctx = context;
        this.test = userArray;
        this.currentUserEmail = email;
    }

    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.rank_list_row, parent, false);
        RankAdapter.ViewHolder viewHolder = new RankAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.clear();

        String stage = String.valueOf(test.get(position).getStagesID());
        String achPts = String.valueOf(test.get(position).getAchPts());

        holder.rowCounter.setText(String.valueOf(position + 1) + ".");
        holder.username.setText(test.get(position).getName());
        holder.stage.setText(stage);
        holder.achPts.setText(achPts);

        if(currentUserEmail.equals(test.get(position).getEmail())){
           holder.view.setBackgroundColor(ctx.getResources().getColor(R.color.colorRed));
        }
    }


    @Override
    public int getItemCount() {
        return test.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, stage, achPts, rowCounter;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            username = (TextView) itemView.findViewById(R.id.txt_username_rank_row);
            stage = (TextView) itemView.findViewById(R.id.txt_stage_rank_row);
            achPts = (TextView) itemView.findViewById(R.id.txt_points_rank_row);
            rowCounter = (TextView) itemView.findViewById(R.id.txt_id_rank_row);

        }

        public void clear() {
            this.view.setBackgroundColor(ctx.getResources().getColor(R.color.transperent));
        }
    }
}
