package com.example.sysadmin.syntaxnetapp;

import android.content.Intent;
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
import com.example.sysadmin.syntaxnetapp.data.TreeNode;

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
        }
    }

}
