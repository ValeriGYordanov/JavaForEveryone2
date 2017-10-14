package last.project.javaforeveryone.model;

import java.io.Serializable;

/**
 * Created by plame_000 on 31-Oct-17.
 */

public class AchievementModel implements Serializable {
    private String name;
    private String subname;
    private int pts;

    /**
     * Empty public constructor required
     * for firebase getUser method.
     */
    public AchievementModel(){

    }

    public AchievementModel(String name, String subname, int pts){
        this.name = name;
        this.subname = subname;
        this.pts = pts;
    }

    public String getName() {
        return name;
    }

    public int getPts() {
        return pts;
    }
}
