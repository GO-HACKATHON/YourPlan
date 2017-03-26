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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListPlanner extends AppCompatActivity
{
    private ProgressDialog pDialog;
    private ListView listView = null;
    private static ArrayList<HashMap<String, String>> listItem;
    BaseAdapter listAdapter;

    String dplan_id = "";
    String dplan_name = "";
    String dplan_address = "";
    String dplan_description = "";
    String dplan_datetime = "";
    String dlat = "";
    String dlng = "";

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
                new String[]{"plan_id", "plan_name", "date", "time"},
                new int[]{R.id.plan_id, R.id.plan_name, R.id.date, R.id.time});
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String viewPlanId = ((TextView)(view.findViewById(R.id.plan_id))).getText().toString();
                new GetDataItem().execute(viewPlanId);
                startActivity(new Intent(ListPlanner.this, PlanDetail.class));
            }
        });
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        SharedPreferences sf = getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE);
        if (sf.getString("jwt-auth-token",null).equals("") || sf.getString("jwt-auth-token",null) == null)
        {
            this.finish();
            startActivity(new Intent(ListPlanner.this, LoginActivity.class));
        }
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
        if (id == R.id.action_logout)
        {
            SharedPreferences sf = getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.clear();
            editor.commit();

            startActivity(new Intent(ListPlanner.this, LoginActivity.class));
        }
        else if (id == R.id.action_refresh)
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
            pDialog.setCancelable(false);
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

            Log.e(null, jsonStr);
            if (jsonStr != null)
            {
                try
                {
                    JSONArray jArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jArray.length(); i++)
                    {
                        JSONObject jObj = jArray.getJSONObject(i);
                        String id_plan = jObj.getString("id");
                        String plan_name = jObj.getString("plan_name");
                        String date = jObj.getString("date");
                        String time = jObj.getString("time");

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("plan_id", id_plan);
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

    class GetDataItem extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListPlanner.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            Intent intent = new Intent(ListPlanner.this, PlanDetail.class);
            intent.putExtra("_id", dplan_id);
            intent.putExtra("_name", dplan_name);
            intent.putExtra("_address", dplan_address);
            intent.putExtra("_lat", dlat);
            intent.putExtra("_lng", dlng);
            intent.putExtra("_datetime", dplan_datetime);
            intent.putExtra("_description", dplan_description);
            startActivity(intent);
        }

        String jsonStr = "";

        @Override
        protected Void doInBackground(String... params)
        {
            HttpHandler httpHandler = new HttpHandler();
            String idUser = (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("Id", null);
            jsonStr = httpHandler.makeCallService(getResources().getString(R.string.main_url) + ":8000/api/plan/detail/" + params[0] + "?token="
                    + (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("jwt-auth-token", null));

            if (jsonStr != null)
            {
                try
                {
                    JSONObject jObj = new JSONObject(jsonStr);
                    dplan_id = jObj.getString("id");
                    dplan_name = jObj.getString("plan_name");
                    dplan_address = jObj.getString("plan_address");
                    dlat = jObj.getString("lat");
                    dlng = jObj.getString("lng");
                    dplan_datetime = jObj.getString("date") + " , " + jObj.getString("time");
                    dplan_description = jObj.getString("description");
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
