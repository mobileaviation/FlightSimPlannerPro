package com.mobileaviationtools.flightsimplannerpro.Route;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mobileaviationtools.flightsimplannerpro.Database.RouteDataSource;
import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.ArrayList;

public class FlightPlanActivateActivity extends ActionBarActivity {
    public Route selectedRoute;
    private ListView routeList;
    private String TAG = "FlightPlanActivateActivity";

    private EditText windDirEdit;
    private EditText windSpeedEdit;
    private Button activateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_plan_activate);

        selectedRoute = null;

        LoadFlightplans();
        SetupButtons();
    }

    private void LoadFlightplans()
    {
        RouteDataSource routeDataSource = new RouteDataSource(this);
        routeDataSource.open();
        ArrayList<Route> routes = routeDataSource.GetAllRoutes();
        routeDataSource.close();

        routeList = (ListView) findViewById(R.id.flightplanList);
        RouteAdapter adapter = new RouteAdapter(routes);
        routeList.setAdapter(adapter);

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (selectedFlightplan != null)
//                {
//                    if (selectedFlightplan.track != null)
//                        selectedFlightplan.track.remove();
//                    selectedFlightplan = null;
//                }

                routeList.setSelection(i);
                RouteAdapter adapter1 = (RouteAdapter) adapterView.getAdapter();
                selectedRoute = adapter1.GetRoute(i);
                Log.i(TAG, "Selected flightplan: " + selectedRoute.name);

                adapter1.setSelectedIndex(i);
                adapter1.notifyDataSetChanged();

                windSpeedEdit.setText(selectedRoute.wind_speed.toString());
                windDirEdit.setText(Float.toString(selectedRoute.wind_direction));
                activateBtn.setEnabled(true);
            }
        });
    }

    private void SetupButtons()
    {
        activateBtn = (Button) findViewById(R.id.activateFpActivateBtn);
        activateBtn.setEnabled(false);
        Button cancelBtn = (Button) findViewById(R.id.activateFpCancelBtn);

        windDirEdit = (EditText) findViewById(R.id.activateFpWindDirEdit);
        windSpeedEdit = (EditText) findViewById(R.id.activateFpWindSpeedEdit);

        activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRoute != null) {
                    Intent intent = new Intent();
                    intent.putExtra("id", selectedRoute.id);



                    if (storeWind()) {
                        setResult(301, intent);
                        finish();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(302);
                finish();
            }
        });
    }

    private boolean storeWind()
    {
        boolean ret = true;
        String message = "";
        if (selectedRoute != null)
        {
            if (windDirEdit.getText().equals(""))
            {
                ret = false;
                message = "Please supply wind direction!";
                ShowAlertDialog(message, "Error");
            }
            else
                selectedRoute.wind_direction = Float.parseFloat(windDirEdit.getText().toString());

            if (windSpeedEdit.getText().equals(""))
            {
                ret = false;
                message = "Please supply wind speed!";
                ShowAlertDialog(message, "Error");
            }
            else
                selectedRoute.wind_speed = Integer.parseInt(windSpeedEdit.getText().toString());
        }

        if (ret)
        {
            //store the wind in the database...
            RouteDataSource routeDataSource = new RouteDataSource(this);
            routeDataSource.open();
            routeDataSource.UpdateFlightplanWind(selectedRoute);
            routeDataSource.close();
        }

        return ret;
    }

    private void ShowAlertDialog(String Title, String Message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle(Title);

        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
