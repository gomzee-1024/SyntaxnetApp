package com.example.sysadmin.syntaxnetapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sysadmin on 9/8/16.
 */
public class Sentance {
    String words[];
    String pos[];
    String posTags[];
    Integer parentIndex[];
    String dependency[];
    int negationInd = -1;
    int determinerInd = -1;
    int totalTokens;
    int subjectInd=-1;
    int objectInd=-1;
    int mainverbInd=-1;
    int typeOfSentence=0;
    int rootInd=-1;
    List<TreeNode<Integer>> topologySentence;//topological tree
    TreeNode<Integer> root;

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

    public void printTree() {
        for(int i=0;i<totalTokens;++i){
            TreeNode<Integer> node = topologySentence.get(i);
            Log.d("treeprint", "parent: "+node.data);
            for(int j=0;j<node.children.size();++j){
                Log.d("treeprint", "child: "+node.children.get(j));
            }
        }
    }
}
