package searchnative.com.topdoctors;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 14/10/16.
 */

public class DoctorList {

    private static DoctorList mInstance = null;

    public List doctorList, doctorIds;

    public String webServiceUrl = AppConfig.getWebServiceUrl();

    public String appLang = LocalInformation.getLocaleLang();

    private DoctorList() {
        getDoctorList();
    }

    public static DoctorList getmInstance() {
        if(mInstance == null) {
            mInstance = new DoctorList();
        }

        return mInstance;
    }

    public void getDoctorList() {

        class DoctorJSON extends AsyncTask<Void, Void, String> {

            final String URL = webServiceUrl + "doctor/doctorList";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lang", appLang));
                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    doctorList = new ArrayList();
                    doctorIds = new ArrayList();
                    doctorIds.add("");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject doctor = jsonArray.getJSONObject(i);
                        doctorList.add(doctor.getString("name"));
                        doctorIds.add(doctor.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        new DoctorJSON().execute();
    }
}
