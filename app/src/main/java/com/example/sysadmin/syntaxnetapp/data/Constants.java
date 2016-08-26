package com.example.sysadmin.syntaxnetapp.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sysadmin on 10/8/16.
 */
public class Constants {
    public static final Set<String> whWords = new HashSet<String>(Arrays.asList("what","when","where",
            "whom","why","who","how","whome"));

    public static final Set<String> auxWords = new HashSet<String>(Arrays.asList("do","have","had",
            "has","was","were","is","m","did","may","can","will","would","can"));

    public static final Set<String> bussinessWords = new HashSet<String>(Arrays.asList("credit",
            "advance","delivery","deliver","delivered"));

    public static final Set<String> categoryList = new HashSet<String>(Arrays.asList("aluminium",
            "chemical","commodity polymer","engineering polymer","kotastone","rubber additives",
            "steel","tiles"));

    public static final int INTEROGATIVE_TYPE_SENTANCE = 1;
}
