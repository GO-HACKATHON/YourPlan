package com.example.user.myapplication;

/**
 * Created by Immanuel on 25/03/2017.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {
    public HttpHandler()
    {}

    public String makeCallService(String reqUrl)
    {
        String response = "response";
        try
        {
            URL url = new URL(reqUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            // Read The Response
            InputStream in = new BufferedInputStream(httpConn.getInputStream());
            response = convertStreamToString(in);
        }
        catch (Exception e) { return e.getMessage(); }
        return response;
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        return sb.toString();
    }
}