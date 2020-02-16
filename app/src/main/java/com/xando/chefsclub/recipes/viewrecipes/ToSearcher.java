package com.xando.chefsclub.recipes.viewrecipes;

public interface ToSearcher {

    int LOOK_FOR_RECIPES = 0;
    int LOOK_FOR_PROFILES = 1;

    void toSearch(int lookFor, int searchFrom);
}
