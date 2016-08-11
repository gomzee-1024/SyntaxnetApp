package com.example.sysadmin.syntaxnetapp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sysadmin on 10/8/16.
 */
public class Constants {
    public static final Set<String> whWords = new HashSet<String>(Arrays.asList("what","when","where",
            "whome","why","who","how"));

    public static final Set<String> auxWords = new HashSet<String>(Arrays.asList("do","have","had",
            "has","was","were","is","m","did","may","can","will","would","can"));

    public static final int INTEROGATIVE_TYPE_SENTANCE = 1;
}
