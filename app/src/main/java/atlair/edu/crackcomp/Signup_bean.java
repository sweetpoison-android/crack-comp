package atlair.edu.crackcomp;

public class Signup_bean {
    public Signup_bean()
    {}

    String name;
    String email;
    String password;
    String mobile;
    String time;

    public Signup_bean(String name, String email, String password, String mobile, String time) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
