package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 6/10/16.
 */

public class SpecialityData {

    private static SpecialityData mInstance = null;

    public List specialityList;

    public List specialityListIcon;

    private ProgressDialog mProgressDialog;

    public String webServiceUrl = AppConfig.getWebServiceUrl();

    private SpecialityData() {
        setSpecialityList();
        Log.v("Execute", "From constructor");
    }

    public static SpecialityData getmInstance() {
        if(mInstance == null) {
            mInstance = new SpecialityData();
        }
         return mInstance;
    }
    public void setSpecialityList() {
        class SpecialityJSON extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "Speciality/index";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lang", LocalInformation.getLocaleLang()));
                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    return mClient.execute(httpPost, responseHandler);
                } catch (ClientProtocolException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    specialityList = new ArrayList();
                    specialityListIcon = new ArrayList();
                    specialityListIcon.add("");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject spec = jsonArray.getJSONObject(i);
                        specialityList.add(spec.getString("SPEC"));
                        specialityListIcon.add(spec.getString("Mipmap"));
                    }
                    Log.v("JSON", jsonArray.toString());
                    Log.v("Inside", specialityList.toString());
                    Log.v("Lang", LocalInformation.getLocaleLang());
                    Log.v("URL", URL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new SpecialityJSON().execute();
    }
}
