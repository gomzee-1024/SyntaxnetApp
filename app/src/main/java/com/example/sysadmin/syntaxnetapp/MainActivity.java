package com.example.sysadmin.syntaxnetapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sysadmin.syntaxnetapp.api.ApiRequest;
import com.example.sysadmin.syntaxnetapp.data.Constants;
import com.example.sysadmin.syntaxnetapp.data.DetectedProduct;
import com.example.sysadmin.syntaxnetapp.data.Sentance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SYNTAXNET_API_URL = "http://192.168.0.175:8080/com.vogella.jersey.first/rest/hello";
    private EditText inputBox;
    private Button submit_btn;
    private TextView responseTv;
    private Sentance sentance;
    private boolean advance_stage=false;
    private  ApiRequest apiRequestObj = new ApiRequest();
    private DetectedProduct dp = new DetectedProduct();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = (EditText) findViewById(R.id.input);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        responseTv = (TextView) findViewById(R.id.respont_tv);
        submit_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (inputBox.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter any sentence first", Toast.LENGTH_SHORT).show();
        } else {
            sendSyntaxnetApiRequest(inputBox.getText().toString());
        }
    }

    private void sendSyntaxnetApiRequest(String s) {
        apiRequestObj.sendJsonRequest(SYNTAXNET_API_URL, s, new ApiRequest.SyntaxnetCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                new GenerateData().execute(result);

               /* try {
                    sentance = modeliseSyntaxnetResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sentance != null) {
                    printSentence(sentance);
                }*/

            }
        });
    }

    private void printSentence(Sentance sentance) {
        Log.d("response", "printSentence: " + sentance.typeOfSentence + "|" + sentance.subjectInd + "|" +
                sentance.objectInd + "|" + sentance.mainverbInd + "|" + sentance.negationInd);
    }

    //Stores json responce in Sentence object and extracts info like type of sentance,subj,obj,verb
    private Sentance modeliseSyntaxnetResult(JSONObject result) throws JSONException {
        Sentance newsen = new Sentance();
        newsen.totalTokens = result.getInt("Total Tokens");
        Log.d("maketree", "totaltokens: "+newsen.totalTokens);
        newsen.words = new String[newsen.totalTokens];
        newsen.dependency = new String[newsen.totalTokens];
        newsen.pos = new String[newsen.totalTokens];
        newsen.posTags = new String[newsen.totalTokens];
        newsen.parentIndex = new Integer[newsen.totalTokens];
        JSONArray data = result.getJSONArray("Data");
        for (int i = 0; i < newsen.totalTokens; ++i) {
            JSONObject dataobj;
            dataobj = data.getJSONObject(i);
            newsen.words[i] = dataobj.getString("Word").toLowerCase();
            newsen.pos[i] = dataobj.getString("Pos");
            newsen.posTags[i] = dataobj.getString("Pos Tag");
            newsen.parentIndex[i] = dataobj.getInt("Parent Index");
            newsen.dependency[i] = dataobj.getString("dependency");
            if (i == 0 && checkForInterogation(newsen.words[i])) {
                newsen.typeOfSentence = Constants.INTEROGATIVE_TYPE_SENTANCE;
            }
            if (subjDependency(newsen.dependency[i]) && newsen.subjectInd == -1) {
                newsen.subjectInd = i;
            } else if (objDependency(newsen.dependency[i]) && newsen.objectInd == -1) {
                newsen.objectInd = i;
            }
            if(newsen.dependency[i].equalsIgnoreCase("neg") && newsen.negationInd==-1){ //check for negation
                newsen.negationInd = i;
            }
            if(newsen.dependency[i].equalsIgnoreCase("det") && newsen.determinerInd==-1){ //check for determiner
                newsen.determinerInd = i;
                if(newsen.words[i].equalsIgnoreCase("no") && newsen.negationInd==-1){
                    newsen.negationInd = i;
                }
            }
            if (newsen.dependency[i].equalsIgnoreCase("root")) { // determine root and main verb
                newsen.rootInd = i;
                if (newsen.pos[i].equalsIgnoreCase("verb")) {
                    newsen.mainverbInd = i;
                }
            }
        }
        newsen.generateNumber(); // generate number for responce type
        newsen.makeTree();
        newsen.printTree();
        return newsen;
    }

    // To check if it is a main object
    private boolean objDependency(String s) {
        if (s.equalsIgnoreCase("dobj")) {
            return true;
        } else if (s.equalsIgnoreCase("iobj")) {
            return true;
        } else if (s.equalsIgnoreCase("pobj")) {
            return true;
        } else {
            return false;
        }

    }

    // To check if it is a main subject
    private boolean subjDependency(String s) {
        if (s.equalsIgnoreCase("nsubj")) {
            return true;
        } else if (s.equalsIgnoreCase("csubj")) {
            return true;
        } else if (s.equalsIgnoreCase("nsubjpass")) {
            return true;
        } else if (s.equalsIgnoreCase("csubjpass")) {
            return true;
        } else {
            return false;
        }
    }

    //To check Interogation Type of Sentance
    private boolean checkForInterogation(String word) {
        if (Constants.whWords.contains(word) || Constants.auxWords.contains(word)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class GenerateData extends AsyncTask<JSONObject,String,Void>{

        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                sentance = modeliseSyntaxnetResult(jsonObjects[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            categorize(sentance.words);
            subcategorize(sentance.words);
            brandDetection(sentance.words);
            generateResponce();

            Log.d("data", "doInBackground: "+sentance.category +"|"+ sentance.subCategory );

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            responseTv.setText(values[0]);
        }

        private void generateResponce() {
            switch(sentance.typeOfResponce){
                case 0 :case 12:case 28:
                    Log.d("responce", "generateResponce: "+"responce1");
                    responce1();
                    break;
                case 22:case 30:
                    Log.d("responce", "generateResponce: "+"responce2");
                    responce2();
                    break;
                case 29:case 1:
                    Log.d("responce", "generateResponce: "+"responce3");
                    responce3();
                    break;
            }
        }

        private void responce3() {
            if(!advance_stage){
                String responce = new String("Then , What do you want?");
                publishProgress(responce);
            }else{
                //payment term code
            }
        }

        private void responce2() {
            if((sentance.words[sentance.subjectInd].equalsIgnoreCase("you") ||
                    sentance.words[sentance.subjectInd].equalsIgnoreCase("company")) &&
                    (sentance.words[sentance.mainverbInd].equalsIgnoreCase("have") ||
                            sentance.words[sentance.mainverbInd].equalsIgnoreCase("sell") ||
                            sentance.words[sentance.mainverbInd].equalsIgnoreCase("provide")) && sentance.category==null) {
                String url = "http://www.power2sme.com/p2sapi/ws/v3/skuCategoryList";
                apiRequestObj.sendskuapirequest(url, new ApiRequest.SkuApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        JSONArray data = null;
                        try {
                            data = result.getJSONArray("Data");
                            StringBuilder responce = new StringBuilder("Sir we have these products:- \n");
                            for (int i = 0; i < data.length(); ++i) {
                                responce.append(data.getString(i) + ",");
                            }
                            responce.append("\nWhat do you want?");
                            responseTv.setText(responce.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }else if((sentance.words[sentance.subjectInd].equalsIgnoreCase("you") ||
                    sentance.words[sentance.subjectInd].equalsIgnoreCase("company")) &&
                    (sentance.words[sentance.mainverbInd].equalsIgnoreCase("have") ||
                            sentance.words[sentance.mainverbInd].equalsIgnoreCase("sell") ||
                            sentance.words[sentance.mainverbInd].equalsIgnoreCase("provide")) && sentance.category!=null){
                dp.category=sentance.category;
                String url = "http://www.power2sme.com/p2sapi/ws/v3/skuSubCategoryList?category="+sentance.category;
                apiRequestObj.sendskuapirequest(url, new ApiRequest.SkuApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        JSONArray data = null;
                        try {
                            data = result.getJSONArray("Data");
                            StringBuilder responce = new StringBuilder("Sir we have these products:- \n");
                            for (int i = 0; i < data.length(); ++i) {
                                responce.append(data.getString(i) + ",");
                            }
                            responce.append("\nWhat do you want?");
                            responseTv.setText(responce.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        private void responce1() {
            if(sentance.brand!=null){
                dp.brand=sentance.brand;
            }
            if(sentance.category!=null && sentance.subCategory!=null){
                String url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?"+"category="+
                        sentance.category+"&subcategory="+sentance.subCategory.replace("-","+");
                apiRequestObj.sendskuapirequest(url, new ApiRequest.SkuApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        int totalrecords=0;
                        try {
                            totalrecords = result.getInt("TotalRecord");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(totalrecords==0){
                            Log.d("responce", "onSuccess: "+"no products found");
                            String url1 = "http://www.power2sme.com/p2sapi/ws/v3/skuSubCategoryList?"+
                                    "category="+sentance.category;
                            dp.category = sentance.category;                                           //store the category
                            apiRequestObj.sendskuapirequest(url1, new ApiRequest.SkuApiCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    try {
                                        JSONArray data = result.getJSONArray("Data");
                                        StringBuilder responce = new StringBuilder("");
                                        for(int i=0;i<data.length();++i){
                                            responce.append(data.getString(i)+",");
                                        }
                                        responseTv.setText(responce.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else{
                            StringBuilder responce = new StringBuilder("");
                            responce.append("ok");
                            dp.category=sentance.category; dp.subCategory=sentance.subCategory; //store the category and subcategory
                            responseTv.setText(responce.toString());
                        }
                    }
                });
            }else if(sentance.category!=null && sentance.subCategory==null){
                String url1 = "http://www.power2sme.com/p2sapi/ws/v3/skuSubCategoryList?"+
                        "category="+sentance.category;
                dp.category=sentance.category;                                              //store the category
                apiRequestObj.sendskuapirequest(url1, new ApiRequest.SkuApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            JSONArray data = result.getJSONArray("Data");
                            StringBuilder responce = new StringBuilder("");
                            for(int i=0;i<data.length();++i){
                                responce.append(data.getString(i)+",");
                            }
                            responce.append("\nWhich type of "+dp.category+" do you want?");
                            responseTv.setText(responce.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else if(sentance.category==null && sentance.subCategory!=null){
                if(dp.category!=null){
                    String url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?category="+dp.category+
                            "&subcategory="+sentance.subCategory.replace("-","+");
                    apiRequestObj.sendskuapirequest(url, new ApiRequest.SkuApiCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            int totalrecords=0;
                            try {
                                totalrecords = result.getInt("TotalRecord");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(totalrecords==0){
                                String responce = new String("Sorry that subcategory is not available");
                                responseTv.setText(responce);
                            }else{
                                String responce = new String("ok");
                                dp.subCategory=sentance.subCategory; //store the subcategory
                                responseTv.setText(responce.toString());
                            }
                        }
                    });
                }else{
                    String responce = new String("Sorry,I didn't get you?");
                    publishProgress(responce);
                }
            }else{
                String responce = new String("Sorry,I didn't get you?");
                publishProgress(responce);
            }
        }

        public void categorize(String[] tokens){
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-category.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    sentance.category=span[i].getType();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void subcategorize(String[] tokens){
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-sub-category1.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    sentance.subCategory=span[i].getType();
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void brandDetection(String[] tokens){
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-brand.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    sentance.brand=span[i].getType();
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
