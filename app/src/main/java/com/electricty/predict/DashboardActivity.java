package com.electricty.predict;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;


public class DashboardActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    private SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String[] country = {"Muscat", "Nizwa", "Sohar"};
    private Interpreter tflite;

    private GoogleSignInClient mGoogleSignInClient;
    SharedPreferences editor;
    String personName;

    String personEmail;

    long start;
    long end;
    long startTime = 0;
    int seconds;
    int minutes;
    private SharedPreferences settings;
    private static final String PREF_MAIN_ACTIVITY = "mainActivityPreference";

    int values = 0;

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), country[position], Toast.LENGTH_LONG).show();

        if (country[position] == "Muscat") {
            values = 1;
            currentconsumption.setText("200 kilowatts");
        } else if (country[position] == "Nizwa") {
            currentconsumption.setText("100 kilowatts");
            values = 2;


        } else if (country[position] == "Sohar") {
            currentconsumption.setText("300 kilowatts");
            values = 3;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    void traineddataset(TextView prediction) {
        if (values == 1) {

            int total = 200 * Integer.valueOf(housenumber.getText().toString());
            prediction.setText(String.valueOf(total) + "kWh");
        } else if (values == 2) {
            int total = 100 * Integer.valueOf(housenumber.getText().toString());
            prediction.setText(String.valueOf(total) + "kWh");

        } else if (values == 3) {
            int total = 300 * Integer.valueOf(housenumber.getText().toString());
            prediction.setText(String.valueOf(total) + "kWh");

        }
    }

    EditText housenumber;
    TextView currentconsumption;


    private static MappedByteBuffer loadModelFile(AssetManager assets)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float predictElectricityRequirements(int currentConsumption, int futureHouseholds) {
        float[] input = new float[]{(float) currentConsumption, (float) futureHouseholds};
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(2 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());
        for (float value : input) {
            inputBuffer.putFloat(value);
        }
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(1 * 4);
        outputBuffer.order(ByteOrder.nativeOrder());
        tflite.run(inputBuffer, outputBuffer);
        outputBuffer.rewind();
        return outputBuffer.getFloat();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomescreen);

        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        startTime = System.currentTimeMillis();
        AssetManager assetManager = DashboardActivity.this.getAssets();
//        ByteBuffer model = null;
        MappedByteBuffer model = null;
        try {
            model = loadModelFile(assetManager);
            tflite = new Interpreter(model);

        } catch (IOException e) {
             Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        currentconsumption = findViewById(R.id.currentconsumption);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(DashboardActivity.this);
        housenumber = findViewById(R.id.housenumber);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        settings = getSharedPreferences(PREF_MAIN_ACTIVITY, 0);

        start = Calendar.getInstance().getTimeInMillis();
        end = Calendar.getInstance().getTimeInMillis();

        sharedPreferences = getSharedPreferences("gmailchecker", MODE_PRIVATE);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        editor = getSharedPreferences("flags", MODE_PRIVATE);
        final SpotsDialog dialog = new SpotsDialog(DashboardActivity.this);

        TextView prediction = findViewById(R.id.prediction);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!housenumber.getText().toString().isEmpty()) {
                    dialog.show();
                    String selectedCity = spin.getSelectedItem().toString();
                    int futureHouseholds = 0;
                    try {
                        futureHouseholds = Integer.parseInt(housenumber.getText().toString());
                    } catch (NumberFormatException e) {
                        futureHouseholds = 0;
                    }

                    if (values == 1) {

//                        int total = 200 * Integer.valueOf(housenumber.getText().toString());
                        int predictedConsumption = (int) predictElectricityRequirements(200, futureHouseholds);
//                        tvPredictedConsumption.setText(predictedConsumption + " Kwh");
                        prediction.setText(String.valueOf(predictedConsumption) + "kWh");

                    } else if (values == 2) {
//                        int total = 100 * Integer.valueOf(housenumber.getText().toString());
//                        prediction.setText(String.valueOf(total) + "kWh");
                        int predictedConsumption = (int) predictElectricityRequirements(100, futureHouseholds);
                        prediction.setText(String.valueOf(predictedConsumption) + "kWh");

                    } else if (values == 3) {
//                        int total = 300 * Integer.valueOf(housenumber.getText().toString());
//                        prediction.setText(String.valueOf(total) + "kWh");
                        int predictedConsumption = (int) predictElectricityRequirements(300, futureHouseholds);
                        prediction.setText(String.valueOf(predictedConsumption) + "kWh");

                    }

//                    int currentConsumption = cityConsumptionMap.getOrDefault(selectedCity, 0);


//                    int currentConsumption = cityConsumptionMap.getOrDefault(selectedCity, 0);


//                    val predictedConsumption = predictElectricityRequirements(currentConsumption, futureHouseholds)
//                    tvPredictedConsumption.text = "${predictedConsumption.toInt()} Kwh"

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            dialog.dismiss();

                        }
                    }, 500);

                } else {
                    Toasty.error(DashboardActivity.this, "Please Enter number of house", Toasty.LENGTH_LONG).show();
                }


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle(getString(R.string.ti)).setMessage(getString(R.string.exit)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        sharedPreferences = getSharedPreferences("gmailchecker", MODE_PRIVATE);

                        int temp = sharedPreferences.getInt("gmailcheckeron", -1);

                        if (temp == 0) {
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(DashboardActivity.this);
                            if (acct != null) {
                                personName = acct.getDisplayName();
                                String personGivenName = acct.getGivenName();
                                String personFamilyName = acct.getFamilyName();
                                personEmail = acct.getEmail();
                                String personId = acct.getId();
                                Uri personPhoto = acct.getPhotoUrl();
                                DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                                dateFormatter.setLenient(false);
                                Date today = new Date();
                                String s = dateFormatter.format(today);


                            }


                        } else if (temp == 1) {
                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                            dateFormatter.setLenient(false);
                            Date today = new Date();
                            String s = dateFormatter.format(today);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
                            String name = preferences.getString("Name", "");
                            if (!name.equalsIgnoreCase("")) {


                            }

                        }


                        finishAffinity();

                    }
                }).setNegativeButton(getString(R.string.no), null)                        //Do nothing on no
                .show();

//        exit();
    }


    /**
     * View pager adapter
     */


    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.action_settings:

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Power Grid");
                    String shareMessage = "\nHi, I am using Power Grid. Check it out!\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();


                }


                return true;
            case R.id.logout:

                sharedPreferences = getSharedPreferences("gmailchecker", MODE_PRIVATE);

                int temp4 = sharedPreferences.getInt("gmailcheckeron", -1);

                if (temp4 == 0) {


                    mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            GoogleSignInAccount account;
                            //account.getEmail();

                            SharedPreferences.Editor mygmailchecker = getSharedPreferences("gmailchecker", MODE_PRIVATE).edit();
                            mygmailchecker.putInt("gmailcheckeron", 1);


                            mygmailchecker.apply();

                            SharedPreferences.Editor editor = getSharedPreferences("flags", MODE_PRIVATE).edit();
                            editor.putInt("flagson", 1);


                            editor.apply();
                            Toasty.success(DashboardActivity.this, "Logout Successfully", Toast.LENGTH_SHORT, true).show();
                            Intent i3 = new Intent(DashboardActivity.this, MyLoginLatest.class);
                            startActivity(i3);
                            finish();
                            DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                            dateFormatter.setLenient(false);
                            Date today = new Date();
                            String s = dateFormatter.format(today);

                        }
                    });


                } else if (temp4 == 1) {

                    SharedPreferences.Editor mygmailchecker = getSharedPreferences("gmailchecker", MODE_PRIVATE).edit();
                    mygmailchecker.putInt("gmailcheckeron", 0);


                    mygmailchecker.apply();

                    SharedPreferences.Editor editor = getSharedPreferences("flags", MODE_PRIVATE).edit();
                    editor.putInt("flagson", 1);


                    editor.apply();
                    Toasty.success(DashboardActivity.this, "Logout Successfully", Toast.LENGTH_SHORT, true).show();
                    Intent i3 = new Intent(DashboardActivity.this, MyLoginLatest.class);
                    startActivity(i3);


                    finish();

                }
                return true;


            case R.id.rateus:


                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}