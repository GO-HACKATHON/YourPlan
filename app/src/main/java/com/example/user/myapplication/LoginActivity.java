package com.example.user.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity
{

    Button login_button;
    TextView register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String idUser = (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("Id", null);
        if (idUser != null)
        {
            startActivity(new Intent(LoginActivity.this, ListPlanner.class));
            return;
        }

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = ((EditText)findViewById(R.id.inputEmail)).getText().toString();
                String password = ((EditText)findViewById(R.id.inputPassword)).getText().toString();
                new Login().execute(email, password);
            }
        });

        register_button = (TextView) findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    class Login extends AsyncTask<String, Void, Void>
    {
        ProgressDialog pDialog = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void v)
        {
            super.onPostExecute(v);
            pDialog.dismiss();

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                }
            });
        }

        @Override
        protected Void doInBackground(String... params)
        {
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("email", params[0]));
            values.add(new BasicNameValuePair("password", params[1]));
            InputStream is = null;

            String result = "";
            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getResources().getString(R.string.main_url) + ":8000/api/login");
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
                if (jObj.length() > 1)
                {
                    final SharedPreferences p = getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = p.edit();
                    editor.putString("jwt-auth-token", jObj.get("token").toString());


                    JSONObject userObj = new JSONObject(jObj.get("user").toString());
                    editor.putString("Id", userObj.get("id").toString());
                    editor.putString("Name", userObj.get("name").toString());
                    editor.commit();
                    startActivity(new Intent(LoginActivity.this, ListPlanner.class));
                }
                else
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(LoginActivity.this, "Invalid email or password. Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
