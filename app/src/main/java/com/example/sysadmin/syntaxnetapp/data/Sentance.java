package com.example.sysadmin.syntaxnetapp.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sysadmin on 9/8/16.
 */
public class Sentance {
    public StringBuilder sentence = new StringBuilder("");
    public String words[];
    public String pos[];
    public String posTags[];
    public Integer parentIndex[];
    public String dependency[];
    public int negationInd = -1;
    public int determinerInd = -1;
    public int totalTokens;
    public int subjectInd=-1;
    public int objectInd=-1;
    public int mainverbInd=-1;
    public int typeOfSentence=0;
    public int rootInd=-1;
    public int typeOfResponce;
    public int auxWord = -1;
    public int whWord = -1;
    public int bussinessWord = -1;
    public int containsNum = -1;
    public int containsInterjection = -1;
    public String category;
    public String subCategory;
    public String brand;
    public List<TreeNode<Integer>> topologySentence;//topological tree
    public TreeNode<Integer> root;

    //To make a tree
    public void makeTree(){
        topologySentence  = new ArrayList<TreeNode<Integer>>(totalTokens);
        Log.d("maketree", "makeTree:totaltokens "+totalTokens);
        root = new TreeNode<Integer>(rootInd);
        Log.d("maketree", "sizeoftop "+topologySentence.size());
        for(int k=0;k<totalTokens;++k){
            TreeNode<Integer> node = new TreeNode<Integer>(k);
            topologySentence.add(node);
        }
        int noOfTreeNodeComplete=0;
        while(noOfTreeNodeComplete!=totalTokens){
            int i;
            for(i=0;i<totalTokens;++i){
                if(topologySentence.get(i)!=null && topologySentence.get(i).completed==false){
                    int j;
                    for(j=0;j<totalTokens;++j){
                        if(parentIndex[j]==i+1){
                            topologySentence.get(i).children.add(j);
                        }
                    }
                    topologySentence.get(i).completed=true;
                    ++noOfTreeNodeComplete;
                }

            }
        }
    }
    //To print tree
    public void printTree() {
        for(int i=0;i<totalTokens;++i){
            TreeNode<Integer> node = topologySentence.get(i);
            Log.d("treeprint", "parent: "+node.data);
            for(int j=0;j<node.children.size();++j){
                Log.d("treeprint", "child: "+node.children.get(j));
            }
        }
    }
    //To detect type of responce(setting bits for sub,obj,verb,interogation,negation in this order)
    public void generateNumber() {
        typeOfResponce=0;
        if(negationInd!=-1){
            typeOfResponce = typeOfResponce|(1);
        }
        if(typeOfSentence!=0){
            typeOfResponce = typeOfResponce|(1<<1);
        }
        if(mainverbInd!=-1){
            typeOfResponce = typeOfResponce|(1<<2);
        }
        if(objectInd!=-1){
            typeOfResponce = typeOfResponce|(1<<3);
        }
        if(subjectInd!=-1){
            typeOfResponce = typeOfResponce|(1<<4);
        }
        if(bussinessWord!=-1){
            typeOfResponce = typeOfResponce|(1<<5);
        }
        if(containsNum!=-1){
            typeOfResponce = typeOfResponce|(1<<6);
        }
    }
}
