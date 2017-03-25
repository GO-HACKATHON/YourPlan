package com.example.user.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListPlanner extends AppCompatActivity
{
    private ProgressDialog pDialog;
    private ListView listView = null;
    private static ArrayList<HashMap<String, String>> listItem;
    BaseAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ListPlanner.this, CrudActivity.class);
                startActivity(intent);
            }
        });

        listItem = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new SimpleAdapter(
                ListPlanner.this,
                listItem,
                R.layout.singleplan,
                new String[]{"plan_name", "date", "time"},
                new int[]{R.id.plan_name, R.id.date, R.id.time});
        listView.setAdapter(listAdapter);

//        new GetData().execute();
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        new GetData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_planner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        if (id == R.id.action_logout)
        {
            SharedPreferences sf = getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putString("jwt-auth-token", "");
            editor.putString("Id", "");
            editor.putString("Nama", "");

            Intent intent = new Intent(ListPlanner.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_refresh)
        {
            new GetData().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListPlanner.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
            listItem.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            listAdapter.notifyDataSetChanged();
        }

        String jsonStr = "";

        @Override
        protected Void doInBackground(Void... params)
        {
            HttpHandler httpHandler = new HttpHandler();
            String idUser = (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("Id", null);
            jsonStr = httpHandler.makeCallService(getResources().getString(R.string.main_url) + ":8000/api/plan/" + idUser + "?token="
                    + (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("jwt-auth-token", null));

            if (jsonStr != null)
            {
                try
                {
                    JSONArray jArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jArray.length(); i++)
                    {
                        JSONObject jObj = jArray.getJSONObject(i);
                        String plan_name = jObj.getString("plan_name");
                        String date = jObj.getString("date");
                        String time = jObj.getString("time");

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("plan_name", plan_name);
                        hashMap.put("date", date + " - ");
                        hashMap.put("time", time);

                        listItem.add(hashMap);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Gagal Mendapatkan Data JSON Dari Server!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }
}
