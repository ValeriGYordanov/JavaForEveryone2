package last.project.javaforeveryone.utility;

/**
 * Created by plame_000 on 21-Oct-17.
 */

public class DBUtil {
    public static abstract class Achievements{
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESC = "desc";
        public static final String PTS = "pts";
        public static final String TABLE_NAME = "Achievements";
    }

    public static abstract class FinalTest{
        public static final String ID = "id";
        public static final String QUESTIONS = "question";
        public static final String TRUE_ANSWERS = "answers";
        public static final String TABLE_NAME = "FinalTest";
    }

    public static abstract class Substage{
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String QUESTION = "question";
        public static final String TRUE_ANSWER = "answer";
        public static final String TABLE_NAME = "Substages";
    }

    public static abstract class Stage{
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String QUESTIONS = "question";
        public static final String TRUE_ANSWERS = "answers";
        public static final String TABLE_NAME = "Stage";
    }
}
