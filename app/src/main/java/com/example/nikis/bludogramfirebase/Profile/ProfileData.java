package com.example.nikis.bludogramfirebase.Profile;

import java.util.HashMap;
import java.util.Map;

public class ProfileData {
    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String GENDER_NONE = "none";

    public String firstName, secondName, login, userUid, gender, imageURL;

    public ProfileData(String firstName, String secondName, String login, String gender) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.gender = gender;
    }

    public Map<String, Object> toMap(){
        if(userUid == null) throw new IllegalArgumentException("userUid might not null!");

        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("secondName", secondName);
        result.put("gender", gender);
        result.put("login", login);
        result.put("userUid", userUid);
        result.put("imageURL", imageURL);
        return result;
    }
}
