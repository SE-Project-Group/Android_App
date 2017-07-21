package com.example.android.track.Model;

/**
 * Created by thor on 2017/7/20.
 */


public class ClientInfo {
    private String user_name;
    private String gender;
    private String birthday;
    private String email;
    private String portrait_url;
    private String career;
    private String education;
    private String hobbyone;
    private String hobbytwo;
    private String hobbythree;

    public ClientInfo(String user_name, String gender, String birthday, String email, String portrait_url, String career, String education, String hobbyone, String hobbytwo, String hobbythree) {
        this.user_name = user_name;
        this.gender = gender;
        this.birthday = birthday;
        this.email = email;
        this.portrait_url = portrait_url;
        this.career = career;
        this.education = education;
        this.hobbyone = hobbyone;
        this.hobbytwo = hobbytwo;
        this.hobbythree = hobbythree;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPortrait_url() {
        return portrait_url;
    }

    public void setPortrait_url(String portrait_url) {
        this.portrait_url = portrait_url;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getHobbyone() {
        return hobbyone;
    }

    public void setHobbyone(String hobbyone) {
        this.hobbyone = hobbyone;
    }

    public String getHobbytwo() {
        return hobbytwo;
    }

    public void setHobbytwo(String hobbytwo) {
        this.hobbytwo = hobbytwo;
    }

    public String getHobbythree() {
        return hobbythree;
    }

    public void setHobbythree(String hobbythree) {
        this.hobbythree = hobbythree;
    }
}
