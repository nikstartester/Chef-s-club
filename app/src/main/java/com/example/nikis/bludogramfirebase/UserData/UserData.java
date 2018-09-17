package com.example.nikis.bludogramfirebase.UserData;

import java.util.HashMap;
import java.util.Map;

import static com.example.nikis.bludogramfirebase.GenderUtils.genderToString;
import static com.example.nikis.bludogramfirebase.GenderUtils.stringToGender;
import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.*;

public class UserData {
    public String firstName, secondName, login, userUid;
    private Gender gender;

    public UserData() {
    }

    public UserData(String firstName, String secondName, String login, Gender gender) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.gender = gender;
    }

    public void emptyData(){
        firstName = "";
        secondName = "";
        login = "";
        userUid = "";
        gender = INDEFINITE;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("secondName", secondName);
        result.put("gender", genderToString(gender));
        result.put("login", login);
        result.put("userUid", userUid);
        return result;
    }
    public void setGender(String genderString){
        gender = stringToGender(genderString);
    }

    /*public void setGender(Gender gender){
        this.gender = gender;
    }*/

    public Gender getGender(){
        return gender;
    }

    public enum Gender {
        GENDER_MALE, GENDER_FEMALE, INDEFINITE
    }
}
