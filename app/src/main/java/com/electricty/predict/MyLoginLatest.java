package com.electricty.predict;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class MyLoginLatest extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;

    private EditText email_user, password_user;
    private TextView forgotPass, signUp;
    private Button btnSignIn;

    public static int loginchecker = 0;
    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    public static int flag = 0;
    int tempenable = 0;

    int templogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_user = findViewById(R.id.atvEmailLog);
//        String inEmail = email.getText().toString();

//        Analytics.with(LoginActivity.this).identify(new Traits().putEmail(inEmail));


        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        GoogleSignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);


        sharedPreferences = getSharedPreferences("flags", MODE_PRIVATE);

        int temp = sharedPreferences.getInt("flagson", -1);

        if (temp == 0) {
            Intent i = new Intent(MyLoginLatest.this, DashboardActivity.class);
            startActivity(i);
        } else {

            initializeGUI();
            firebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mFirebaseUser = firebaseAuth.getCurrentUser();


            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String inEmail = email_user.getText().toString();
                    String inPassword = password_user.getText().toString();


                    signUser(inEmail, inPassword);


                }
            });

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


            final ImageView pastepin = findViewById(R.id.pastePin);
            pastepin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tempenable == 0) {
                        password_user.setTransformationMethod(new PasswordTransformationMethod());
                        password_user.setTransformationMethod(null);

                        DrawableCompat.setTint(
                                DrawableCompat.wrap(pastepin.getDrawable()),
                                ContextCompat.getColor(MyLoginLatest.this, R.color.primary)
                        );
                        tempenable = 1;
                    } else {
                        tempenable = 0;
                        password_user.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        DrawableCompat.setTint(
                                DrawableCompat.wrap(pastepin.getDrawable()),
                                ContextCompat.getColor(MyLoginLatest.this, R.color.mytextcolor)
                        );
                    }
                }
            });
            final TextView forgetpassword = findViewById(R.id.forgetpassword);
            forgetpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MyLoginLatest.this, ForgetPasswordActivity.class);
                    startActivity(i);
                }
            });


            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MyLoginLatest.this, RegistrationActivity.class));
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
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
    // [END onActivityResult]

    // [START handleSignInResult]
    @RequiresApi(api = Build.VERSION_CODES.N)
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
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    // [START revokeAccess]

    // [END revokeAccess]

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));


            account.getEmail();
            SharedPreferences.Editor editor = getSharedPreferences("flags", MODE_PRIVATE).edit();
            editor.putInt("flagson", 0);


            editor.apply();
            SharedPreferences.Editor mygmailchecker = getSharedPreferences("gmailchecker", MODE_PRIVATE).edit();
            mygmailchecker.putInt("gmailcheckeron", 0);


            mygmailchecker.apply();


            String personName = account.getDisplayName();

            String personEmail = account.getEmail();

            DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String s = dateFormatter.format(today);


//            Users user1 = new Users(account.getDisplayName(), account.getEmail(), "Login with Gmail", s);
//
//            FirebaseDatabase.getInstance().
//
//                    getReference("Users")
//                    .
//
//                            setValue(user1).
//
//                    addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(MyLoginLatest.this, "Success", Toast.LENGTH_LONG).show();
//                            } else {
//
//                            }
//                        }
//                    });


            Toasty.success(MyLoginLatest.this, "Login Successfully ", Toast.LENGTH_SHORT, true).

                    show();



            Intent i = new Intent(MyLoginLatest.this, DashboardActivity.class);

            startActivity(i);

            flag = 1;

//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//
        } else {
            //            mStatusTextView.setText(R.string.signed_out);

//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//
        }

    }


    public void signUser(final String email, String password) {
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();


        if (email_user.getText().toString().equals("") && password_user.getText().toString().equals("") && email_user.getText().toString().equals("") || password_user.getText().toString().equals("")) {
            Toasty.info(MyLoginLatest.this, "Sorry please fill the Credentials", Toast.LENGTH_SHORT, true).show();

        } else {

            final SpotsDialog dialog = new SpotsDialog(MyLoginLatest.this);
            dialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                // Initially when you only know the user's name


                                 SharedPreferences.Editor editor = getSharedPreferences("flags", MODE_PRIVATE).edit();
                                editor.putInt("flagson", 0).apply();

                                SharedPreferences.Editor mygmailchecker = getSharedPreferences("gmailchecker", MODE_PRIVATE).edit();
                                mygmailchecker.putInt("gmailcheckeron", 1);


                                mygmailchecker.apply();

                                if (user != null) {
                                    editor.putString("email", user.getEmail()).apply();
                                }
                                if (user != null) {
                                    editor.putString("name", user.getDisplayName()).apply();
                                }
                                DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                                dateFormatter.setLenient(false);
                                Date today = new Date();
                                String s = dateFormatter.format(today);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLoginLatest.this);
                                String name = preferences.getString("Name", "");
                                if (!name.equalsIgnoreCase("")) {
                                    final Users user1 = new Users(name, email, "Login with Firebase", s);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MyLoginLatest.this, "Success", Toast.LENGTH_LONG).show();
                                            } else {

                                            }
                                        }
                                    });


                                }


                                Toasty.success(MyLoginLatest.this, "Login Successfully ", Toast.LENGTH_SHORT, true).

                                        show();
//Latest
                                loginchecker = 1;



                                Intent i = new Intent(MyLoginLatest.this, DashboardActivity.class);

                                startActivity(i);

                                flag = 1;

                            } else {
                                dialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toasty.error(MyLoginLatest.this, "Login failed", Toast.LENGTH_SHORT, true).show();
                                Toasty.error(MyLoginLatest.this, "Try Again", Toast.LENGTH_SHORT, true).show();
                            }
                        }

                    });


        }
    }

    private void initializeGUI() {

        email_user = findViewById(R.id.atvEmailLog);
        password_user = findViewById(R.id.atvPasswordLog);
        signUp = findViewById(R.id.tvSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressDialog = new ProgressDialog(this);


    }


    public boolean validateInput(String inemail, String inpassword) {

        if (inemail.isEmpty()) {
            email_user.setError("Email field is empty.");
            return false;
        }
        if (inpassword.isEmpty()) {
            password_user.setError("Password is empty.");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(MyLoginLatest.this);
        builder
                .setTitle(getString(R.string.ti))
                .setMessage(getString(R.string.exit))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        finishAffinity();

                    }
                })
                .setNegativeButton(getString(R.string.no), null)                        //Do nothing on no
                .show();

//        exit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }

    }
}
