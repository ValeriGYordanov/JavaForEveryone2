package last.project.javaforeveryone.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import last.project.javaforeveryone.R;


public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private ArrayList<ArrayList<Object>> achievemntsList;
    private LayoutInflater mInflater;
    private UserModel currentUserModel;

    public AchievementAdapter(Context ctx, ArrayList arrayList, UserModel userModel) {
        this.mInflater = LayoutInflater.from(ctx);
        this.achievemntsList = arrayList;
        this.currentUserModel = userModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.achievements_recycler_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String title;
        String desc;
        String points;

        title = (String) achievemntsList.get(position).get(0);
        desc = (String) achievemntsList.get(position).get(1);
        points = (String) achievemntsList.get(position).get(2).toString();

        if (currentUserModel.containsAchByName(title)){
            holder.img.setVisibility(View.VISIBLE);
        }

        holder.title.setText(title);
        holder.desc.setText(desc);
        holder.points.setText(points);
    }

    @Override
    public int getItemCount() {
        return this.achievemntsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, desc, points;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txt_title_rec_view);
            desc = (TextView) itemView.findViewById(R.id.txt_desc_rec_view);
            points = (TextView) itemView.findViewById(R.id.txt_points_rec_view);

            img = (ImageView) itemView.findViewById(R.id.img_ach_rec_view);

        }
    }
}