package com.example.sysadmin.syntaxnetapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sysadmin.syntaxnetapp.adapters.ChatRecyclerViewAdapter;
import com.example.sysadmin.syntaxnetapp.adapters.RecyclerItemClickListener;
import com.example.sysadmin.syntaxnetapp.api.ApiRequest;
import com.example.sysadmin.syntaxnetapp.data.ChatMessage;
import com.example.sysadmin.syntaxnetapp.data.Constants;
import com.example.sysadmin.syntaxnetapp.data.DetectedProduct;
import com.example.sysadmin.syntaxnetapp.data.Sentance;
import com.example.sysadmin.syntaxnetapp.data.TreeNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView chat_rview;
    private ChatRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageButton send_button;
    private EditText edit_chat_msg;
    private ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
    private Sentance sentance;
    private boolean advance_stage = false;
    private ApiRequest apiRequestObj = new ApiRequest();
    private DetectedProduct dp = new DetectedProduct();
    private boolean numans = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        send_button = (ImageButton) findViewById(R.id.send_button);
        edit_chat_msg = (EditText) findViewById(R.id.edit_msg);
        chat_rview = (RecyclerView) findViewById(R.id.chat_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ChatRecyclerViewAdapter(this, list);
        chat_rview.setLayoutManager(layoutManager);
        chat_rview.setAdapter(adapter);
        send_button.setOnClickListener(this);
        edit_chat_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handle = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendFunction(textView);
                    handle = true;
                }
                return handle;
            }
        });
        generateTypingResponce();
        generateChatMessageWithDelay("Hello sir ,How may I help you?", false, true, 2000);
        chat_rview.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Intent intent = new Intent(ChatActivity.this, SentanceData.class);
                        intent.putExtra("sentence", sentance.sentence.toString());
                        intent.putExtra("typeOfSentence", sentance.typeOfSentence);
                        intent.putExtra("subj", sentance.subjectInd);
                        intent.putExtra("obj", sentance.objectInd);
                        intent.putExtra("verb", sentance.mainverbInd);
                        intent.putExtra("negation", sentance.negationInd);
                        intent.putExtra("root", sentance.rootInd);
                        intent.putExtra("int", sentance.containsNum);
                        intent.putExtra("interjection", sentance.containsInterjection);
                        intent.putExtra("category", dp.category);
                        intent.putExtra("subcategory", dp.subCategory);
                        intent.putExtra("brand", dp.brand);
                        intent.putExtra("creditpayment", dp.creditPayment);
                        intent.putExtra("creditdays", dp.creditDays);
                        startActivity(intent);
                    }
                })
        );
    }

    private void sendFunction(TextView textView) {
        if (edit_chat_msg.getText().toString().equalsIgnoreCase("")) {
            edit_chat_msg.setError("Enter some text first");
        } else {

        }
    }

    @Override
    public void onClick(View view) {
        if (edit_chat_msg.getText().toString().equalsIgnoreCase("")) {
            edit_chat_msg.setError("Enter some text first");
        } else {
            String message = edit_chat_msg.getText().toString();
            edit_chat_msg.setText("");
            hideKeyboard(view);
            generateChatMessage(message, true, false);
            if (checkforHiHello(message)) {
                return;
            }
            sendSyntaxnetApiRequest(message);
        }
    }

    private boolean checkforHiHello(String message) {
        switch (message) {
            case "hi":
            case " hi":
                generateTypingResponce();
                generateChatMessageWithDelay("Hello sir ,How may I help you?", false, true, 2000);
                return true;
            default:
                return false;
        }
    }

    private void hideKeyboard(View view) {
        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void generateTypingResponce() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatMessage chat_msg = new ChatMessage();
                chat_msg.setMsg("Typing...");
                chat_msg.setIsMe(false);
                adapter.addMsg(chat_msg);
                chat_rview.scrollToPosition(adapter.getItemCount() - 1);
            }
        }, 1000);
    }

    private void generateTypingResponceWithDelay(int delay) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatMessage chat_msg = new ChatMessage();
                chat_msg.setMsg("Typing...");
                chat_msg.setIsMe(false);
                adapter.addMsg(chat_msg);
                chat_rview.scrollToPosition(adapter.getItemCount() - 1);
            }
        }, delay);
    }

    private void generateChatMessage(String s, boolean isMe, boolean replaceLast) {
        ChatMessage msg = new ChatMessage();
        msg.setMsg(s);
        msg.setIsMe(isMe);
        if (replaceLast) {
            adapter.refreshlastmsg(msg);
        } else {
            adapter.addMsg(msg);
        }
        chat_rview.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void generateChatMessageWithDelay(final String s, final boolean isMe, final boolean replaceLast, int delay) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatMessage msg = new ChatMessage();
                msg.setMsg(s);
                msg.setIsMe(isMe);
                if (replaceLast) {
                    adapter.refreshlastmsg(msg);
                } else {
                    adapter.addMsg(msg);
                }
                chat_rview.scrollToPosition(adapter.getItemCount() - 1);
            }
        }, delay);
    }

    private void sendSyntaxnetApiRequest(String s) {
        apiRequestObj.sendJsonRequest(Constants.SYNTAXNET_API_URL, s, new ApiRequest.SyntaxnetCallback() {
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
        Log.d("maketree", "totaltokens: " + newsen.totalTokens);
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
            if (newsen.posTags[i].equalsIgnoreCase("uh") && newsen.containsInterjection == -1) { //To check for Interjection
                newsen.containsInterjection = i;
            }
            if (newsen.pos[i].equalsIgnoreCase("num") && newsen.containsNum == -1) { //To check contains number
                newsen.containsNum = i;
            }
            if (Constants.bussinessWords.contains(newsen.words[i])) {  //To check if contains bussiness words
                newsen.bussinessWord = i;
            }

            if (i == 0 && checkForInterogation(newsen.words[i]) && newsen.whWord == -1) { //To check for interogative type sentence
                newsen.typeOfSentence = Constants.INTEROGATIVE_TYPE_SENTANCE;
                newsen.whWord = i;
            }
            if (i == 0 && checkForAuxWords(newsen.words[i])) {
                newsen.auxWord = i;
            }
            if (subjDependency(newsen.dependency[i]) && newsen.subjectInd == -1) { //To check subj
                newsen.subjectInd = i;
            } else if (objDependency(newsen.dependency[i]) && newsen.objectInd == -1) { // To check obj
                newsen.objectInd = i;
            }
            if (newsen.dependency[i].equalsIgnoreCase("neg") && newsen.negationInd == -1) { //check for negation
                newsen.negationInd = i;
            }
            if (newsen.dependency[i].equalsIgnoreCase("det") && newsen.determinerInd == -1) { //check for determiner
                newsen.determinerInd = i;
                if (newsen.words[i].equalsIgnoreCase("no") && newsen.negationInd == -1) {
                    newsen.negationInd = i;
                }
            }
            if (newsen.dependency[i].equalsIgnoreCase("root")) { // determine root and main verb
                newsen.rootInd = i;
                if (newsen.pos[i].equalsIgnoreCase("verb")) {
                    newsen.mainverbInd = i;
                }
            }
            newsen.sentence.append(newsen.words[i] + " ");
        }
        if (checkForAuxInterogation(newsen)) { // To check Auxiliary Interrogation
            newsen.typeOfSentence = Constants.INTEROGATIVE_TYPE_SENTANCE;
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
        if (Constants.whWords.contains(word)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkForAuxWords(String word) {
        if (Constants.auxWords.contains(word)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkForAuxInterogation(Sentance sen) {
        if (sen.auxWord != -1 && sen.subjectInd != -1 && sen.words[sen.subjectInd].equalsIgnoreCase("you")) {
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
        switch (item.getItemId()) {
            case R.id.chat_act:
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GenerateData extends AsyncTask<JSONObject, String, Void> {

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

            Log.d("data", "doInBackground: " + sentance.category + "|" + sentance.subCategory);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            generateTypingResponce();
            generateChatMessageWithDelay(values[0], false, true, 2000);
        }

        private void generateResponce() {
            switch (sentance.typeOfResponce) {
                case 0:
                    Log.d("responce", "generateResponce: " + "responce0");
                    responce0();
                    break;
                case 32:
                case 36:
                case 40:
                case 44:
                case 48:
                case 52:
                case 56:
                case 60:
                    Log.d("responce", "generateResponce: " + "responce32to60");
                    responce32to60();
                    break;
                case 6:
                case 14:
                case 22:
                case 30:
                    Log.d("responce", "generateResponce: " + "responce6to30");
                    responce6to30();
                    break;
                case 34:
                case 38:
                case 42:
                case 46:
                case 50:
                case 54:
                case 58:
                case 62:
                    Log.d("responce", "generateResponce: " + "responce34to62");
                    responce32to60();
                    break;
                case 4:
                case 12:
                case 28:
                case 20:
                    Log.d("responce", "generateResponce: " + "responce4to20");
                    responce4to20();
                    break;
                case 9:
                case 13:
                case 25:
                case 29:
                    Log.d("responce", "generateResponce: " + "responce9to25");
                    responce9to25();
                    break;
                case 33:
                case 37:
                case 45:
                case 61:
                    Log.d("responce", "generateResponce: " + "responce33");
                    responce33();
                    //responce4();
                    break;
                case 64:
                    Log.d("responce", "generateResponce: " + "responce64");
                    responce64();
                    break;
                case 96:
                case 108:
                case 104:
                case 124:
                    Log.d("responce", "generateResponce: " + "responce96");
                    responce96();
                    break;
                case 97:
                case 109:
                case 105:
                case 125:
                    Log.d("responce", "generateResponce: " + "responce97");
                    responce97();
                    break;
            }
        }

        private void responce0() {
            if (sentance.brand != null) {
                dp.brand = sentance.brand;
            }
            if (checkInterjection()) {
                if (sentance.category == null && sentance.subCategory == null) {
                    publishProgress("Hello Sir, what do you want?");
                } else {
                    Log.d("response", "checkCatSubCat");
                    checkCatSubCat();
                }
            } else {
                if (sentance.category == null && sentance.subCategory == null) {
                    publishProgress("Sorry sir,I didn't get you");
                } else {
                    checkCatSubCat();
                }
            }
        }

        private void responce32to60() {
            if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("product") ||
                    sentance.words[sentance.bussinessWord].equalsIgnoreCase("products")) {
                if (sentance.category == null && sentance.subCategory == null) {
                    showListOfProducts();
                } else {
                    checkCatSubCat();
                }
            } else if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("order") ||
                    sentance.words[sentance.bussinessWord].equalsIgnoreCase("orders")) {
                if (sentance.category == null && sentance.subCategory == null) {
                    publishProgress("Ok, So what is your order?");
                } else {
                    checkCatSubCat();
                }
            } else if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("credit")) {
                if (advance_stage) {
                    dp.creditPayment = true;
                    publishProgress("Ok , Please specify credit from 7,15, or 30 Days");
                    numans = true;
                } else {
                    publishProgress("Sorry sir, I did'nt get you");
                }
            } else if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("advance")) {
                if (advance_stage) {
                    dp.creditPayment = false;
                    dp.creditDays = -1;
                    publishProgress("ok then payment term is advance");
                    numans = false;
                }
            }
        }

        private void responce6to30() {
            if (sentance.category == null && sentance.subCategory == null) {
                showListOfProducts();
            } else {
                checkCatSubCat();
            }
        }

        private void responce4to20() {
            if (sentance.category == null && sentance.subCategory == null) {
                if (sentance.objectInd != -1) {
                    publishProgress("Sir, we don't have " + sentance.words[sentance.objectInd]);
                } else {
                    publishProgress("Sorry sir, I didn't get you");
                }
            } else {
                checkCatSubCat();
            }
        }

        private void responce9to25() {
            if (sentance.category == null && sentance.subCategory == null) {
                publishProgress("We don't sell " + sentance.words[sentance.objectInd]);
            } else {
                if (dp.category != null) {
                    if (sentance.category != null) {
                        if (sentance.category.equalsIgnoreCase(dp.category)) {
                            publishProgress("Ok, then i will cancel your order of " + dp.category +
                                    "\nPlease specify what you want");
                            dp.category = null;
                        } else {
                            publishProgress("Ok,but sir your order was of " + dp.category);
                        }
                    } else {
                        publishProgress("Ok, then please specify what do you want sir");
                    }
                } else {
                    publishProgress("Ok, then please specify what do you want sir");
                }
            }
        }

        private void responce33() {
            if (advance_stage) {
                if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("credit")) {
                    publishProgress("ok then Advance");
                    dp.creditPayment = false;
                    dp.creditDays = -1;
                    numans = false;
                } else {
                    publishProgress("ok then Credit but of how many days 7,15 or 30 days?");
                    dp.creditPayment = true;
                    numans = true;
                }
            } else {
                publishProgress("Sorry sir, I didn't get you");
            }
        }

        private void responce64() {
            if (advance_stage) {
                if (dp.creditPayment) {
                    if (numans) {
                        if (sentance.words[sentance.containsNum].equalsIgnoreCase("7") ||
                                sentance.words[sentance.containsNum].equalsIgnoreCase("15") ||
                                sentance.words[sentance.containsNum].equalsIgnoreCase("30")) {
                            dp.creditDays = Integer.parseInt(sentance.words[sentance.containsNum]);
                            publishProgress("Ok, " + dp.creditDays + " days of credit");
                            numans = false;
                        } else {
                            publishProgress("Sir ,please specify credit from 7,15 or 30 days");
                        }
                    } else {
                        publishProgress("Sorry sir, I didn't get you");
                    }
                } else {
                    publishProgress("Sorry sir, I didn't get you");
                }
            } else {
                publishProgress("Sorry sir, I didn't get you");
            }
        }

        private void responce97() {
            if (advance_stage) {
                if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("credit")) {
                    if (dp.creditPayment) {
                        publishProgress("Ok ,then How many days Credit? PLease specify from 7,15 or 30 days");
                        dp.creditPayment = true;
                        numans = true;
                    } else {
                        publishProgress("But your Payment term is advance sir");
                    }
                } else {
                    publishProgress("Sorry sir, I didn't get you");
                }
            }
        }

        private void responce96() {
            if (advance_stage) {
                if (sentance.words[sentance.bussinessWord].equalsIgnoreCase("credit")) {
                    if (sentance.words[sentance.containsNum].equalsIgnoreCase("7") ||
                            sentance.words[sentance.containsNum].equalsIgnoreCase("15") ||
                            sentance.words[sentance.containsNum].equalsIgnoreCase("30")) {
                        dp.creditPayment = true;
                        dp.creditDays = Integer.parseInt(sentance.words[sentance.containsNum]);
                        numans = false;
                        publishProgress("Ok sir , Credit of " + dp.creditDays + " Days");
                    } else {
                        publishProgress("Please specify credit drom 7,15 or 30 days");
                        dp.creditPayment = true;
                        numans = true;
                    }
                } else {
                    publishProgress("Sorry sir, I did'nt get you");
                }
            }
        }

        private void showListOfProducts() {
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
                        generateTypingResponce();
                        generateChatMessageWithDelay(responce.toString(), false, true, 2000);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void checkCatSubCat() {
            if (sentance.category != null && sentance.subCategory != null) {
                String url;
                if (dp.category != null && dp.subCategory != null) {
                    if (dp.category.equalsIgnoreCase(sentance.category)
                            && dp.subCategory.equalsIgnoreCase(sentance.subCategory)) {
                        if (sentance.brand != null) {
                            dp.brand = sentance.brand;
                            publishProgress("Ok , so you want " + dp.category + " " + dp.subCategory +
                                    " of " + dp.brand);
                            return;
                        } else {
                            publishProgress("Sir we have your order , you are repeating it");
                            return;
                        }
                    }
                }
                if (sentance.category.equalsIgnoreCase("aluminium") &&
                        sentance.subCategory.equalsIgnoreCase("sheet")) {
                    url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?" + "category=" +
                            sentance.category + "&subcategory=" + sentance.category + "+" +
                            sentance.subCategory.replace("-", "+");
                } else {
                    url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?" + "category=" +
                            sentance.category + "&subcategory=" + sentance.subCategory.replace("-", "+");
                }
                apiRequestObj.sendskuapirequest(url, new ApiRequest.SkuApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        int totalrecords = 0;
                        try {
                            totalrecords = result.getInt("TotalRecord");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (totalrecords == 0) {
                            Log.d("responce", "onSuccess: " + "no products found");
                            String url1 = "http://www.power2sme.com/p2sapi/ws/v3/skuSubCategoryList?" +
                                    "category=" + sentance.category;
                            dp.category = sentance.category;                                           //store the category
                            apiRequestObj.sendskuapirequest(url1, new ApiRequest.SkuApiCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    try {
                                        JSONArray data = result.getJSONArray("Data");
                                        StringBuilder responce = new StringBuilder("Sir we have ");
                                        for (int i = 0; i < data.length(); ++i) {
                                            responce.append(data.getString(i) + ",");
                                        }
                                        responce.append(" in " + dp.category + " What do you want?");
                                        generateTypingResponce();
                                        generateChatMessageWithDelay(responce.toString(), false, true, 2000);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            StringBuilder responce = new StringBuilder("");
                            responce.append("ok");
                            dp.category = sentance.category;
                            dp.subCategory = sentance.subCategory; //store the category and subcategory
                            advance_stage = true;
                            generateTypingResponce();
                            generateChatMessageWithDelay(responce.toString(), false, true, 2000);
                            generateTypingResponceWithDelay(2500);
                            generateChatMessageWithDelay("Please specify your payment term \n" +
                                    "Credit\nor\nAdvance", false, true, 3500);
                        }
                    }
                });
            } else if (sentance.category != null && sentance.subCategory == null) {
                String url1 = "http://www.power2sme.com/p2sapi/ws/v3/skuSubCategoryList?" +
                        "category=" + sentance.category;
                dp.category = sentance.category;                                              //store the category
                apiRequestObj.sendskuapirequest(url1, new ApiRequest.SkuApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            JSONArray data = result.getJSONArray("Data");
                            StringBuilder responce = new StringBuilder("");
                            for (int i = 0; i < data.length(); ++i) {
                                responce.append(data.getString(i) + ",");
                            }
                            responce.append("\nWhich type of " + dp.category + " do you want?");
                            generateTypingResponce();
                            generateChatMessageWithDelay(responce.toString(), false, true, 2000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (sentance.category == null && sentance.subCategory != null) {
                if (dp.category != null) {
                    String url;
                    if (dp.category.equalsIgnoreCase("aluminium")) {
                        url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?category=" + dp.category +
                                "&subcategory=" + dp.category + "+" + sentance.subCategory.replace("-", "+");
                    } else {
                        url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?category=" + dp.category +
                                "&subcategory=" + sentance.subCategory.replace("-", "+");
                    }
                    apiRequestObj.sendskuapirequest(url, new ApiRequest.SkuApiCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            int totalrecords = 0;
                            try {
                                totalrecords = result.getInt("TotalRecord");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (totalrecords == 0) {
                                String responce = new String("Sorry that subcategory is not available");
                                generateTypingResponce();
                                generateChatMessageWithDelay(responce.toString(), false, true, 2000);
                            } else {
                                String responce = new String("ok");
                                dp.subCategory = sentance.subCategory; //store the subcategory
                                generateTypingResponce();
                                generateChatMessageWithDelay(responce.toString(), false, true, 2000);
                                advance_stage = true;
                                generateTypingResponceWithDelay(2500);
                                generateChatMessageWithDelay("Please specify your payment term \n" +
                                        "Credit\nor\nAdvance", false, true, 3500);
                            }
                        }
                    });
                } else {
                    String[] categories = new String[2];
                    int cnt = 0;
                    dp.subCategory = sentance.subCategory;
                    StringBuilder responce = new StringBuilder("Which type of " + sentance.subCategory + " ");
                    if (Constants.steelSubCategories.contains(sentance.subCategory)) {
                        categories[cnt++] = "Steel";
                        responce.append("Steel or ");
                    }
                    if (Constants.aluminiumSubCategories.contains(sentance.subCategory)) {
                        categories[cnt++] = "Aluminium";
                        responce.append("Aluminium ");
                    }
                    responce.append("you want?");
                    if (cnt > 1) {
                        publishProgress(responce.toString());
                    } else {
                        publishProgress("ok you want Steel " + sentance.subCategory +
                                "\nPlease specify your payment term \n" +
                                "Credit\n" +
                                "or\n" +
                                "Advance");
                        dp.category = "Steel";
                        advance_stage = true;
                    }
                }
            }
        }

        private boolean checkInterjection() {
            if (sentance.containsInterjection != -1) {
                return true;
            } else {
                return false;
            }
        }

        public void categorize(String[] tokens) {
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-category.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    sentance.category = span[i].getType();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void subcategorize(String[] tokens) {
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-sub-category1.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    sentance.subCategory = span[i].getType();
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void brandDetection(String[] tokens) {
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-brand.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    sentance.brand = span[i].getType();
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
