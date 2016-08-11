package com.example.sysadmin.syntaxnetapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SYNTAXNET_API_URL = "http://192.168.0.175:8080/com.vogella.jersey.first/rest/hello";
    private EditText inputBox;
    private Button submit_btn;
    private Sentance sentance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = (EditText) findViewById(R.id.input);
        submit_btn = (Button) findViewById(R.id.submit_btn);
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
        ApiRequest apiRequestObj = new ApiRequest();
        apiRequestObj.sendJsonRequest(SYNTAXNET_API_URL, s, new ApiRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    sentance = modeliseSyntaxnetResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sentance != null) {
                    printSentence(sentance);
                }
            }
        });
    }

    private void printSentence(Sentance sentance) {
        Log.d("response", "printSentence: " + sentance.typeOfSentence + "|" + sentance.subjectInd + "|" +
                sentance.objectInd + "|" + sentance.mainverbInd);
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
            newsen.words[i] = dataobj.getString("Word");
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
            }
            if (newsen.dependency[i].equalsIgnoreCase("root")) { // determine root and main verb
                newsen.rootInd = i;
                if (newsen.pos[i].equalsIgnoreCase("verb")) {
                    newsen.mainverbInd = i;
                }
            }
        }
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
}
