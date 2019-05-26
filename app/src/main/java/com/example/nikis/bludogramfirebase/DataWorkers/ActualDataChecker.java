package com.example.nikis.bludogramfirebase.DataWorkers;

import java.util.List;

public abstract class ActualDataChecker<Data> {

    public abstract boolean hasChanged(Data data1, Data data2);

    public static boolean compareArrayList(List a, List b) {

        /*
         * If both reference points to same object
         */
        if (a == b)
            return true;

        /*
         * If one of them is null
         * they are not equal
         */
        if (a == null || b == null)
            return false;

        /*
         * If they differ in size,
         * they are not equal
         */
        if (a.size() != b.size())
            return false;

        /*
         * Compare them using equals method
         * which compares individual elements
         * of both ArrayList objects
         */
        return a.equals(b);
    }
}
