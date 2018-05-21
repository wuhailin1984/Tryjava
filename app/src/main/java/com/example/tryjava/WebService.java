package com.example.tryjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hwu on 2018-03-11.
 */

public class WebService {

    private static String IP = "35.204.196.49:8080";

    // Throug get method to get data from http server
    public static String executeHttpGet(String username, String password) {

        HttpURLConnection conn = null;
        InputStream is = null;

        try {
            // user name and password
            // URL address
            String path = "http://" + IP + "/AppWebServer1/LogLet";
            path = path + "?username=" + username + "&password=" + password;

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // set over time
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // set method to get data
            conn.setRequestProperty("Charset", "UTF-8"); // set data format

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                return parseInfo(is);
            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // set lost connection protection when app exits abnormally
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        //return null;
        return "something to show";
    }

    // Turn input stream to String type
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // parse to string
        return new String(data, "UTF-8");
    }

    // urn input stream to byte type
    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}
