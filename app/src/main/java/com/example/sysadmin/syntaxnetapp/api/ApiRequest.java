package com.example.sysadmin.syntaxnetapp.api;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sysadmin.syntaxnetapp.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sysadmin on 9/8/16.
 */
public class ApiRequest {

    public void sendJsonRequest(String url, final String input, final SyntaxnetCallback callback) {
        String urlfinal = url + "?" + "step=" + input.replace(" ", "+");
        String tag_json_obj = "json_obj_req";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                urlfinal,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("response", "Error: " + error.getMessage());
                    }
                }
        );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void sendskuapirequest(String url, final SkuApiCallback callback){
        String tag_json_obj = "json_obj_req";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responce", response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("responce", "Error: " + error.getMessage());
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                String creds = String.format("%s:%s","admin","admin");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public interface SyntaxnetCallback{
        public void onSuccess(JSONObject result);
    }

    public interface SkuApiCallback{
        public void onSuccess(JSONObject result);
    }
}
