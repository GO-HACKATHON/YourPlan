package com.example.user.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View v)
    {
        String name = ((EditText)findViewById(R.id.regName)).getText().toString();
        String email = ((EditText)findViewById(R.id.regEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.regPassword)).getText().toString();
        String repassword = ((EditText)findViewById(R.id.regConfirmPassword)).getText().toString();

        new Register().execute(name, email, password, repassword);
    }

    class Register extends AsyncTask<String, Void, Void>
    {
        ProgressDialog pDialog = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void v)
        {
            super.onPostExecute(v);
            pDialog.dismiss();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("name", params[0]));
            values.add(new BasicNameValuePair("email", params[1]));
            values.add(new BasicNameValuePair("password", params[2]));
            values.add(new BasicNameValuePair("repassword", params[3]));
            InputStream is = null;

            String result = "";
            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getResources().getString(R.string.main_url) + ":8000/api/register");
                httpPost.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            try
            {
                JSONObject jObj = new JSONObject(result);
                final SharedPreferences p = getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = p.edit();
                editor.putString("jwt-auth-token", jObj.get("token").toString());

                JSONObject userObj = new JSONObject(jObj.get("user").toString());
                editor.putString("Id", userObj.get("id").toString());
                editor.putString("Name", userObj.get("name").toString());
                editor.commit();
                startActivity(new Intent(RegisterActivity.this, ListPlanner.class));
            }
            catch (JSONException e)
            {
                Log.e(null, e.getMessage());
            }
            return null;
        }
    }
}
