package com.example.nikis.bludogramfirebase;


import com.example.nikis.bludogramfirebase.UserData.UserData.Gender;

import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.GENDER_FEMALE;
import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.GENDER_MALE;
import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.INDEFINITE;

public class GenderUtils {
    public static String genderToString(Gender gender) {
        switch (gender) {
            case GENDER_MALE:
                return "male";
            case GENDER_FEMALE:
                return "female";
            case INDEFINITE:
                return "unknown";
            default:
                return "unknown";
        }
    }
    public static Gender stringToGender(String genderString){
        switch (genderString) {
            case "male": return GENDER_MALE;
            case "female": return GENDER_FEMALE;
            case "unknown": return INDEFINITE;
            default: return INDEFINITE;
        }
    }
}
