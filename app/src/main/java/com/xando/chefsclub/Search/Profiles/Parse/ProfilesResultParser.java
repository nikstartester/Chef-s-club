package com.xando.chefsclub.Search.Profiles.Parse;

import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Search.Parse.SearchResultJsonParser;

public class ProfilesResultParser extends SearchResultJsonParser<ProfileData> {
    @Override
    protected Class<ProfileData> getDataClass() {
        return ProfileData.class;
    }
}
