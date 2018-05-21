package com.example.tryjava;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hwu on 2018-03-11.
 */

public class WebServicePost {

    private static String IP = "35.204.196.49:8080";

    // get data from server through POST method
    public static String executeHttpPost(String username, String password) {

        try {
            String path = "http://" + IP + "/AppWebServer1/LogLet";

            // send command and infomation
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("password", password);

            return sendPOSTRequest(path, params, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // deal with POST request
    private static String sendPOSTRequest(String path, Map<String, String> params, String encoding) throws Exception {

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, encoding);

        HttpPost post = new HttpPost(path);
        post.setEntity(entity);
        DefaultHttpClient client = new DefaultHttpClient();
        // set request timeout
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        // set read timeout
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        HttpResponse response = client.execute(post);

        // judge if receive data successfully
        if (response.getStatusLine().getStatusCode() == 200) {
            return getInfo(response);
        }

        // doesn't receive data successfully, return null
        return null;
    }

    // get data
    private static String getInfo(HttpResponse response) throws Exception {

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        // set input stream to byte
        byte[] data = WebService.read(is);
        // set input stream to string
        return new String(data, "UTF-8");
    }
}