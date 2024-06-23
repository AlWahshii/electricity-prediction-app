package com.electricty.predict;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;


public class UserWelcomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    public static int flag = 0;
    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywelcom);


        sharedPreferences = getSharedPreferences("flags", MODE_PRIVATE);

        int temp = sharedPreferences.getInt("flagson", -1);

        if (temp == 0) {
            Intent i = new Intent(UserWelcomeActivity.this, DashboardActivity.class);
            startActivity(i);
        } else {


            GoogleSignInButton googleSignInButton = findViewById(R.id.sign_in_button);
            googleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();

                }
            });
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            updateUI(account);
            sharedPreferences = getSharedPreferences("flags", MODE_PRIVATE);

            firebaseAuth = FirebaseAuth.getInstance();
             mFirebaseUser = firebaseAuth.getCurrentUser();

            Button login = findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(UserWelcomeActivity.this, MyLoginLatest.class);
                    startActivity(i);
                }
            });
            Button reg = findViewById(R.id.btnSignUp);
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(UserWelcomeActivity.this, RegistrationActivity.class);
                    startActivity(i);

                }
            });



            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));

            account.getEmail();
            SharedPreferences.Editor editor = getSharedPreferences("flags", MODE_PRIVATE).edit();
            editor.putInt("flagson", 0);


            editor.apply();
            SharedPreferences.Editor mygmailchecker = getSharedPreferences("gmailchecker", MODE_PRIVATE).edit();
            mygmailchecker.putInt("gmailcheckeron", 0);
            DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String s = dateFormatter.format(today);

            mygmailchecker.apply();



            Toasty.success(UserWelcomeActivity.this, "Login Successfully ", Toast.LENGTH_SHORT, true).show();


            Intent i = new Intent(UserWelcomeActivity.this, DashboardActivity.class);
            startActivity(i);
            flag = 1;


        } else {


        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onBackPressed() {
        exit();

    }

    public void exit() {

         AlertDialog.Builder builder = new AlertDialog.Builder(UserWelcomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialogbox, null);

        // Set the alert_dialogbox layout as alert dialog view
        builder.setView(dialogView);

        // Get the alert_dialogbox alert dialog view widgets reference
        Button positive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button neutral = dialogView.findViewById(R.id.exit);


        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

            }
        });

        //Neutral Button
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                int temp = sharedPreferences.getInt("gmailcheckeron", -1);

                if (temp == 0) {
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(UserWelcomeActivity.this);
                    if (acct != null) {

                        DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                        dateFormatter.setLenient(false);
                        Date today = new Date();
                        String s = dateFormatter.format(today);

                    }


                } else if (temp == 1) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseUser = firebaseAuth.getCurrentUser();
                    DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                    dateFormatter.setLenient(false);
                    Date today = new Date();
                    String s = dateFormatter.format(today);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserWelcomeActivity.this);
                    String name = preferences.getString("Name", "");


                }

            }
        });


        dialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();


    }
}
