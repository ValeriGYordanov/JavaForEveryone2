package last.project.javaforeveryone.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by plame_000 on 19-Oct-17.
 */

public class UserModel implements Serializable{

    private String name;
    private String email;
    private int achPts;
    private int stagesID;
    private String image;
    private ArrayList<AchievementModel> arrAch = new ArrayList<>();

    /**
     * Empty public constructor required
     * for firebase getUser method.
     */
    public UserModel() {

    }

    public UserModel(String name, String email, int achPts, int stagesID, String imageURL) {
        this.name = name;
        this.email = email;
        this.achPts = achPts;
        this.stagesID = stagesID;
        this.image = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAchPts() {
        return achPts;
    }

    public int getStagesID() {
        return stagesID;
    }

    public String getImage() {
        return image;
    }

    /**
     * Required public constructor for
     * ArrayList for firebase usage!
     * @return
     */
    public ArrayList<AchievementModel> getArrAch(){
        return arrAch;
    }

    public void setAch(AchievementModel ach) {
        this.achPts = this.achPts + ach.getPts();
        this.arrAch.add(ach);
    }

    public void setAchPts(int achPts) {
        this.achPts = achPts;
    }

    public boolean containsAchByName(String name) {
        for (AchievementModel ach : this.arrAch) {
            if (ach.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void setStagesID(int stagesID) {
        this.stagesID = stagesID;
    }

    public static class CustomComparator implements Comparator<UserModel> {

        @Override
        public int compare(UserModel o1, UserModel o2) {

            if (o2.stagesID - o1.stagesID < 0) {
                return -1;
            } else if (o2.stagesID - o1.stagesID > 0) {
                return 1;
            } else {
                if(o2.achPts - o1.achPts < 0){
                    return -1;
                }else if(o2.achPts - o1.achPts > 0){
                    return 1;
                }else{
                    if(o2.name.compareToIgnoreCase(o1.name) < 0){
                        return -1;
                    }else if(o2.name.compareToIgnoreCase(o1.name) > 0){
                        return 1;
                    }else{
                        return 0;
                    }
                }
            }
        }

    }
}
