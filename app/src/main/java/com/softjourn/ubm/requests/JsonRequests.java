package com.softjourn.ubm.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.softjourn.ubm.application.AppController;
import com.softjourn.ubm.beans.Internat;
import com.softjourn.ubm.beans.Need;
import com.softjourn.ubm.beans.about.About;
import com.softjourn.ubm.database.DataSource;
import com.softjourn.ubm.utils.Consts;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inet on 30.05.2016.
 */
public class JsonRequests implements Consts {

    private String TAG = JsonRequests.class.getSimpleName();
    private String internats_json_array = "internats_json_array";
    private String needs_json_array = "needs_json_array";
    private String about_json = "about_json";

    public interface OnTaskCompleted {
        void onLoadAllNeedsCompleted(String allNeedsUrl);

        void onLoadOneInternatNeedsCompleted(String oneInternatNeeds, int internatId);

        void onLoadMoreCompleted(String moreNeedsUrl);

        void onLoadSearchCompleted(String searchUrl);

        void onLoadInternatsDataComplited(String internatsUrl);

        void onLoadUpdateCompleted(String lastUrls);

        void onLoadAboutInfo();
    }
    private boolean isResponseDataChanged(NetworkResponse response){
//        if(response!=null && response.headers!=null){
//            if(response.headers.get(MODIFIED_HEADER) == null){
//                isResponseChanged = false;
//            }else{
//                isResponseChanged = Boolean.valueOf(response.headers.get(MODIFIED_HEADER)) ? true : false;
//            }
//        }else {
//            isResponseChanged = false;
//        }

        boolean isResponseChanged = false;

        return isResponseChanged;
    }

    public void loadInternstsRequest(final Context context, final OnTaskCompleted listener) {

        final boolean isResponseChanged = true;

        JsonArrayRequest req = new JsonArrayRequest(Consts.ALL_INTERTATS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        System.out.print(isResponseChanged);
                        if(isResponseChanged == true){

                            ArrayList<Internat> internats = null;
                            try {
                                internats = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<List<Internat>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            new DataSource(context).createInternatsList(internats);
                        }

                        listener.onLoadInternatsDataComplited(Consts.ALL_INTERTATS_URL);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, internats_json_array);
    }

    public void loadAllNeedsRequest(final Context context, final OnTaskCompleted listener) {

        final boolean isResponseChanged = true;

        JsonArrayRequest req = new JsonArrayRequest(Consts.ALL_NEEDS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if(isResponseChanged == true){
                            ArrayList<Need> needs = null;
                            try {
                                needs = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<List<Need>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            new DataSource(context).createNeedsList(needs, Consts.ALL_NEEDS_URL);
                        }

                        listener.onLoadAllNeedsCompleted(Consts.ALL_NEEDS_URL);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, needs_json_array);
    }

    public void loadOneInternatNeedsRequest(final Context context, final int internatId, final OnTaskCompleted listener) {

        final boolean isResponseChanged = true;

        //TODO
        final String oneInternatNeedsUrl = Consts.ONE_INTERNAT_NEEDS_URL /*+ internatId*/;

        JsonArrayRequest req = new JsonArrayRequest(oneInternatNeedsUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if(isResponseChanged == true){
                            ArrayList<Need> needs = null;
                            try {
                                needs = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<List<Need>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            new DataSource(context).createNeedsList(needs, oneInternatNeedsUrl);
                        }
                        listener.onLoadOneInternatNeedsCompleted(oneInternatNeedsUrl, internatId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, needs_json_array);
    }

    public void updateRequest(final Context context, final String lastUrl, final OnTaskCompleted listener) {

        final boolean isResponseChanged = true;

        JsonArrayRequest req = new JsonArrayRequest(lastUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if(isResponseChanged == true){

                            ArrayList<Need> needs = null;
                            try {
                                needs = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<List<Need>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new DataSource(context).writeNeedsTodb(lastUrl, needs);

                        }
                        listener.onLoadUpdateCompleted(lastUrl);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, needs_json_array);
    }

    public void searchRequest(final Context context, final OnTaskCompleted listener) {
        final boolean isResponseChanged = true;

        JsonArrayRequest req = new JsonArrayRequest(Consts.SEARCH_NEEDS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if(isResponseChanged == true){
                            ArrayList<Need> needs = null;
                            try {
                                needs = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<List<Need>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            new DataSource(context).writeNeedsTodb(Consts.SEARCH_NEEDS_URL, needs);
                        }
                        listener.onLoadSearchCompleted(Consts.SEARCH_NEEDS_URL);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, needs_json_array);
    }

    public void loadMoreRequest(final Context context, final OnTaskCompleted listener) {
        final boolean isResponseChanged = false;

        JsonArrayRequest req = new JsonArrayRequest(Consts.MORE_NEEDS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(isResponseChanged == true){
                            Log.d(TAG, response.toString());

                            ArrayList<Need> needs = null;
                            try {
                                needs = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<List<Need>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            new DataSource(context).writeNeedsTodb(Consts.MORE_NEEDS_URL, needs);
                        }
                        listener.onLoadMoreCompleted(Consts.MORE_NEEDS_URL);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, needs_json_array);
    }

    public void loadAboutRequest(final Context context, final OnTaskCompleted listener) {
        final boolean isResponseChanged = false;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                Consts.ABOUT_US_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
//                        if(isResponseChanged == true){

                            About about = null;
                            try {
                                about = new ObjectMapper().readValue(String.valueOf(response), new TypeReference<About>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            new DataSource(context).createAbout(about);
//                        }
                        listener.onLoadAboutInfo();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Response <JSONObject> parseNetworkResponse(NetworkResponse response) {
                isResponseDataChanged(response);
                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(req, about_json);
    }
}
