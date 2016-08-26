package com.example.sysadmin.syntaxnetapp.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sysadmin on 11/8/16.
 */
public class TreeNode<T> {
    T data;
    boolean completed =false;
    List<T> children;

    public TreeNode(T data){
        this.data = data;
        children = new LinkedList<T>();
    }

}
