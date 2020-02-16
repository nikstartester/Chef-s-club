package com.xando.chefsclub.search.parser;

import com.google.gson.Gson;
import com.xando.chefsclub.dataworkers.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class SearchResultJsonParser<Data extends BaseData> {

    public List<Data> parseResults(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;
        List<Data> results = new ArrayList<>();

        JSONArray hits = jsonObject.optJSONArray("hits");

        if (hits == null)
            return null;

        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;
            Data data = new Gson().fromJson(hit.toString(), (Type) getDataClass());
            if (data == null)
                continue;
            results.add(data);
        }
        return results;
    }

    protected abstract Class<Data> getDataClass();
}
