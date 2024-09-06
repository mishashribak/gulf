package Model;

public class QAData {
    private String strQuestion;
    private String strAnswer;

    public QAData(String q, String a){
        strAnswer = a;
        strQuestion = q;
    }

    public void setQuestion(String str){
        strQuestion = str;
    }

    public void setAnswer(String str){
        strAnswer = str;
    }

    public String getQuestion(){
        return strQuestion;
    }

    public String getAnswer(){
        return strAnswer;
    }
}
