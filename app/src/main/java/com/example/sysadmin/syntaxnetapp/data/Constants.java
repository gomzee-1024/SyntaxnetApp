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
            "advance","term","products","product","order","orders"));

    public static final Set<String> categoryList = new HashSet<String>(Arrays.asList("aluminium",
            "chemical","commodity polymer","engineering polymer","kotastone","rubber additives",
            "steel","tiles"));

    public static final Set<String> steelSubCategories = new HashSet<String>(Arrays.asList("Angle",
            "Beam","Billet","Channel", "Coil","Column","Flat","Ingot","Joist","Pipe","Plate",
            "Purlin","Sheet","Strip","Tmt"));

    public static final Set<String> aluminiumSubCategories = new HashSet<String>(Arrays.asList("Sheet"));

    public static final String[] set = new String[]{"Steel","Aluminium"};

    public static final int INTEROGATIVE_TYPE_SENTANCE = 1;

    public static final String SYNTAXNET_API_URL = "http://192.168.0.175:8080/com.vogella.jersey.first/rest/hello";

}
