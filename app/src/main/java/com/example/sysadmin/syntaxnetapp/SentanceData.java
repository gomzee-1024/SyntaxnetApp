package com.example.sysadmin.syntaxnetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SentanceData extends AppCompatActivity {

    String sentence;
    int typeOfSentence;
    int subj;
    int obj;
    int verb;
    int negation;
    int root;
    int num;
    int interjection;
    String category;
    String subcategory;
    String brand;
    boolean creditpayment;
    int creditDays;

    TextView sentencetv,typeOfSentencetv,subjtv,objtv,verbtv,negationtv,roottv,numtv,interjectiontv
            ,categorytv,subcategorytv,brandtv,paymentermtv,creditDaystv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentance_data);
        Intent intent = getIntent();
        sentence = intent.getStringExtra("sentence");
        typeOfSentence = intent.getIntExtra("typeOfSentence",-2);
        subj = intent.getIntExtra("subj",-2);
        obj = intent.getIntExtra("obj",-2);
        verb = intent.getIntExtra("verb",-2);
        negation = intent.getIntExtra("negation",-2);
        root = intent.getIntExtra("root",-2);
        num = intent.getIntExtra("int",-2);
        interjection = intent.getIntExtra("interjection",-2);
        category = intent.getStringExtra("category");
        subcategory = intent.getStringExtra("subcategory");
        brand = intent.getStringExtra("brand");
        creditpayment = intent.getBooleanExtra("creditpayment",false);
        creditDays = intent.getIntExtra("creditdays",-2);

        sentencetv = (TextView) findViewById(R.id.sentence);
        typeOfSentencetv = (TextView) findViewById(R.id.sentence_type);
        subjtv = (TextView) findViewById(R.id.subject);
        objtv = (TextView) findViewById(R.id.object);
        verbtv = (TextView) findViewById(R.id.verb);
        negationtv = (TextView) findViewById(R.id.negation);
        roottv = (TextView) findViewById(R.id.root);
        numtv = (TextView) findViewById(R.id.num);
        interjectiontv = (TextView) findViewById(R.id.interjection);
        categorytv = (TextView) findViewById(R.id.category);
        subcategorytv = (TextView) findViewById(R.id.subcategory);
        brandtv = (TextView) findViewById(R.id.brand);
        paymentermtv = (TextView) findViewById(R.id.paymentterm);
        creditDaystv = (TextView) findViewById(R.id.credit_days);

        sentencetv.setText(sentence);
        typeOfSentencetv.setText("Type of sentence:- "+typeOfSentence+"");
        subjtv.setText("Subject:- "+subj+"");
        objtv.setText("Object:- "+obj+"");
        verbtv.setText("Main verb:- "+verb+"");
        negationtv.setText("Negation:- "+negation+"");
        roottv.setText("Root:- "+root+"");
        numtv.setText("Contains number:- "+num+"");
        interjectiontv.setText("Contains interjection:- "+interjection+"");
        if(category!=null){
            categorytv.setText("Category:-"+category);
        }else{
            categorytv.setText("Category:-");
        }
        if(subcategory!=null){
            subcategorytv.setText("SubCategory:-"+subcategory);
        }else{
            subcategorytv.setText("SubCategory:-");
        }
        if(brand!=null){
            brandtv.setText("Brand:-"+brand);
        }else{
            brandtv.setText("Brand:-");
        }
        paymentermtv.setText(creditpayment+"");
        creditDaystv.setText("Credit Days:- "+creditDays+"");
    }
}
