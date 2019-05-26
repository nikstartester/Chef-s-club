package com.example.nikis.bludogramfirebase.Search.Profiles.Parse;

import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Search.Parse.SearchResultJsonParser;

public class ProfilesResultParser extends SearchResultJsonParser<ProfileData> {
    @Override
    protected Class<ProfileData> getDataClass() {
        return ProfileData.class;
    }
}
