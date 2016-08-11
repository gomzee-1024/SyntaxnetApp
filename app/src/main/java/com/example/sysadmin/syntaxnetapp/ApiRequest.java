package com.example.sysadmin.syntaxnetapp;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sysadmin on 9/8/16.
 */
public class ApiRequest {

    public void sendJsonRequest(String url, final String input, final VolleyCallback callback) {
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

    public interface VolleyCallback{
        public void onSuccess(JSONObject result);
    }
}
