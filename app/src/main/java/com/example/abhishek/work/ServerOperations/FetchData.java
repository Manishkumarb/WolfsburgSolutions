package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FetchData {

    private String serverURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868";
    private Context context;
    private ServerResponse serverResponse = new ServerResponse();
    private JSONObject reqBody = new JSONObject();
    private HashMap<String, String> header;

    public FetchData(Context context) {
        this.context = context;
    }

    public void getCategories() {
        String url = serverURL + "/magento_get_category";
        header = new HashMap<>();
        sendRequest(url,header);
    }

    public void getProducts(String name, int id) {

        String url = serverURL + "/magento_product_display";
        header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("name", name);
        header.put("id_category", String.valueOf(id));
        sendRequest(url,header);

    }

    public void searchProduct(String searchTerm) {
        String url = serverURL + "/magento_search_product";
        header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("searchTerm",searchTerm);
        sendRequest(url,header);
    }

    public void getProductDetails(String product_SKU) {

        String url = serverURL + "/magento_info_product";

        //TODO implement
    }

    private void sendRequest(String url,Map<String,String> headers) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST
                , url
                , new JSONObject(headers)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                serverResponse.saveResponse(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError",error.getMessage().toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    public ServerResponse getServerResponseInstance() {
        return this.serverResponse;
    }
}
