package last.project.javaforeveryone.model;

/**
 * Created by HP on 3.11.2017 г..
 */

public class QuestionModel {

    private String question;
    private int selectedAnswerPostion;
    private boolean option1Selected, option2Selected, option3Selected;
    private String option1Text, option2Text, option3Text, correctAnswer;

    public QuestionModel(String questionText, String option1Text, String option2Text, String option3Text){
        this.question = questionText;
        this.option1Text = option1Text;
        this.option2Text = option2Text;
        this.option3Text = option3Text;
        this.correctAnswer = option1Text;
    }

    public boolean isOption1Selected() {
        return option1Selected;
    }

    public void setOption1Selected(boolean option1Selected) {
        this.option1Selected = option1Selected;
        if(option1Selected){ // To make sure only one option is selected at a time
            setOption2Selected(false);
            setOption3Selected(false);
        }
    }

    public boolean isOption2Selected() {
        return option2Selected;
    }

    public void setOption2Selected(boolean option2Selected) {
        this.option2Selected = option2Selected;
        if(option2Selected){
            setOption1Selected(false);
            setOption3Selected(false);
        }
    }

    public boolean isOption3Selected() {
        return option3Selected;
    }

    public void setOption3Selected(boolean option3Selected) {
        this.option3Selected = option3Selected;
        if(option3Selected){
            setOption2Selected(false);
            setOption1Selected(false);
        }
    }

    public int getSelectedAnswerPostion() {
        return selectedAnswerPostion;
    }

    public void setSelectedAnswerPostion(int selectedAnswerPostion) {
        this.selectedAnswerPostion = selectedAnswerPostion;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1Text() {
        return option1Text;
    }

    public String getOption2Text() {
        return option2Text;
    }

    public String getOption3Text() {
        return option3Text;
    }

    public String getCorrectAns() {
        return correctAnswer;
    }
}