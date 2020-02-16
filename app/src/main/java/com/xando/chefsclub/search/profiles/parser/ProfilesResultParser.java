package com.xando.chefsclub.search.profiles.parser;

import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.search.parser.SearchResultJsonParser;

public class ProfilesResultParser extends SearchResultJsonParser<ProfileData> {

    @Override
    protected Class<ProfileData> getDataClass() {
        return ProfileData.class;
    }
}
