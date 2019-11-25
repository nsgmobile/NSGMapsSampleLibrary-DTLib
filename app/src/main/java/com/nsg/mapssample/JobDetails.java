package com.nsg.mapssample;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

/**
 * Created by sailaja.ch NSGI on 27/09/2019 *
 * Modified on 14/10/2019
 *
 */
public class JobDetails extends Activity {
 // private double srcLatitude=55.067291;
 // private double srcLongitude=24.978782;
 // private double destLatitude=55.067205;
//  private double desLongitude=24.979878;

   private int enteredMode=1;
   private int bufferSize=10;
   private String charlsisNumber;
   private ImageButton draw_route;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_details);
      //  draw_route=(ImageButton)findViewById(R.id.route);
        Spinner Spinner_Ch_Number = (Spinner)findViewById(R.id.Spinner_Ch_Number);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(JobDetails.this,
                R.array.Spinner_Ch_Number, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_Ch_Number.setAdapter(adapter);
        Spinner_Ch_Number.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                charlsisNumber = (String) parentView.getItemAtPosition(position);
                Log.e("SpinnerDT :", " Selected Item From SpinnerDT " + charlsisNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Log.e("SpinnerDT :", " Selected Item From SpinnerDT " + charlsisNumber);
             //   if (charlsisNumber.isEmpty()) {
                    if (charlsisNumber.equals("1HGBH41JXMN109185")) {
                        double srcLatitude=55.067291;
                        double srcLongitude=24.978782;
                        double destLatitude=55.067205;
                        double desLongitude=24.979878;

                        //24.979878,55.067205
                        //24.978488,55.066921 24.981227,55.070077
                       // double srcLatitude = 55.072528;
                      //  double srcLongitude = 24.986486;
                      //  double destLatitude = 55.073878;
                      //  double desLongitude = 24.986097;

                        Intent NSGIIntent = new Intent(JobDetails.this, NSGApiActivity.class);
                        Bundle NSGIBundle = new Bundle();
                        NSGIBundle.putString("charlsisNumber", "1HGBH41JXMN109185");
                        NSGIBundle.putDouble("srcLatitude", srcLatitude);
                        NSGIBundle.putDouble("srcLongitude", srcLongitude);
                        NSGIBundle.putDouble("destLatitude", destLatitude);
                        NSGIBundle.putDouble("desLongitude", desLongitude);
                        NSGIBundle.putInt("enteredMode", enteredMode);
                        NSGIBundle.putInt("bufferSize", bufferSize);
                        NSGIIntent.putExtras(NSGIBundle);
                        startActivity(NSGIIntent);
                    } else if (charlsisNumber.equals("1HGBH41JXMN109186")) {
                        Intent NSGIIntent = new Intent(JobDetails.this, NSGApiActivity.class);
                        double srcLatitude = 55.076897;
                        double srcLongitude = 24.989081;
                        double destLatitude = 55.077639;
                        double desLongitude = 24.988377;
                        Bundle NSGIBundle = new Bundle();
                        NSGIBundle.putString("charlsisNumber", "1HGBH41JXMN109186");
                        NSGIBundle.putDouble("srcLatitude", srcLatitude);
                        NSGIBundle.putDouble("srcLongitude", srcLongitude);
                        NSGIBundle.putDouble("destLatitude", destLatitude);
                        NSGIBundle.putDouble("desLongitude", desLongitude);
                        NSGIBundle.putInt("enteredMode", enteredMode);
                        NSGIBundle.putInt("bufferSize", bufferSize);
                        NSGIIntent.putExtras(NSGIBundle);
                        startActivity(NSGIIntent);
                    } else if (charlsisNumber.equals("1HGBH41JXMN109187")) {
                        double srcLatitude = 55.066921;
                        double srcLongitude = 24.978488;
                        double destLatitude = 55.070077;
                        double desLongitude = 24.981227;

                        Intent NSGIIntent = new Intent(JobDetails.this, NSGApiActivity.class);
                        Bundle NSGIBundle = new Bundle();
                        NSGIBundle.putString("charlsisNumber", "1HGBH41JXMN109187");
                        NSGIBundle.putDouble("srcLatitude", srcLatitude);
                        NSGIBundle.putDouble("srcLongitude", srcLongitude);
                        NSGIBundle.putDouble("destLatitude", destLatitude);
                        NSGIBundle.putDouble("desLongitude", desLongitude);
                        NSGIBundle.putInt("enteredMode", enteredMode);
                        NSGIBundle.putInt("bufferSize", bufferSize);
                        NSGIIntent.putExtras(NSGIBundle);
                        startActivity(NSGIIntent);
                    }
                    /*
                }else{

                    final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(JobDetails.this);

                    alert.setTitle("ALERT").setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(JobDetails.this);
                                    builder.setMessage("Please select CHALSIS NUMBER")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                    alert.show();

                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(JobDetails.this);
                    builder.setTitle("Alert");
                    builder.setView(R.id.alert_chalsis);
                    builder.setMessage("Please select CHALSIS NUMBER");
                    builder.setNegativeButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.slide_from_top; //style id



                }
                */
            }
        });

    }
}
