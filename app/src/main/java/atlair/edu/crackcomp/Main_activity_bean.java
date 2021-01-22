package atlair.edu.crackcomp;

public class Main_activity_bean implements Comparable{

    String imgurl;
    String name;
    String question;
    String option1;
    String option2;
    String option3;
    String option4;

    String ans;
    Long time;


    public Main_activity_bean()
    {}

    public Main_activity_bean(String imgurl,String name, String question, String option1, String option2, String option3, String option4, String ans, Long time) {
        this.imgurl = imgurl;
        this.name = name;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.ans = ans;
        this.time = time;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    @Override
    public int compareTo(Object o) {
        Main_activity_bean bean = (Main_activity_bean) o;

        if((time)== (bean.getTime()))
        {
            return 0;
        }
        else  if((time)< (bean.getTime()))
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}

