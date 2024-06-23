
package com.electricty.predict;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

  import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;


public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password;
    private Button signup;
    private TextView signin;
    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private SharedPreferences sharedPreferences;
    int tempenable = 0;
    int tempenable1 = 0;
    EditText name;
    String name1;
    public int mytermscondition = 0;
    String newname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPreferences = getSharedPreferences("flags", MODE_PRIVATE);
        name = findViewById(R.id.name);
        int temp = sharedPreferences.getInt("flagson", -1);

        if (temp == 0) {
            Intent i = new Intent(RegistrationActivity.this, DashboardActivity.class);
            startActivity(i);
        }
        initializeGUI();
        final EditText confirmpassword = findViewById(R.id.confirmpassword);

        email.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (!isValidEmail(email.getText().toString())) {
                    email.setError("Enter a valid address");
                }
            }
        });
        final ImageView pastepin = findViewById(R.id.pastePinfirst);
        pastepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempenable == 0) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    password.setTransformationMethod(null);
                    tempenable = 1;
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(pastepin.getDrawable()),
                            ContextCompat.getColor(RegistrationActivity.this, R.color.primary)
                    );
                } else {
                    tempenable = 0;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(pastepin.getDrawable()),
                            ContextCompat.getColor(RegistrationActivity.this, R.color.mytextcolor)
                    );
                }

            }
        });
        final ImageView pastepin1 = findViewById(R.id.pastePinfirst_second);
        pastepin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempenable1 == 0) {
                    confirmpassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmpassword.setTransformationMethod(null);
                    tempenable1 = 1;
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(pastepin.getDrawable()),
                            ContextCompat.getColor(RegistrationActivity.this, R.color.primary)
                    );
                } else {
                    tempenable1 = 0;
                    confirmpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(pastepin.getDrawable()),
                            ContextCompat.getColor(RegistrationActivity.this, R.color.mytextcolor)
                    );
                }

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText atvPasswordReg = findViewById(R.id.atvPasswordReg);
                EditText confirmpassword = findViewById(R.id.confirmpassword);
                String a = atvPasswordReg.getText().toString().trim();
                String b = confirmpassword.getText().toString().trim();
                name1 = name.getText().toString().trim();
                if (name1.length() >= 1) {
                    newname = name1;
                } else {
                    Toast.makeText(RegistrationActivity.this, "please write more character", Toast.LENGTH_SHORT).show();
                }

                if (a.equals(b)) {
                    final String inputPw = password.getText().toString().trim();
                    final String inputEmail = email.getText().toString().trim();

                    if (validateInput(inputPw, inputEmail))
                        registerUser(inputPw, inputEmail);

                } else {
                    Toasty.error(RegistrationActivity.this, "Sorry Password is not Match", Toast.LENGTH_LONG, true).show();
                }
            }
        });

        CheckBox chk = (CheckBox) findViewById(R.id.chk1);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                if (checked) {
                    mytermscondition = 1;
                } else {
                    mytermscondition = 0;
                }
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MyLoginLatest.class));
            }
        });

    }

    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            //android Regex to check the email address Validation
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void initializeGUI() {

        email = findViewById(R.id.atvEmailReg);
        password = findViewById(R.id.atvPasswordReg);
        signin = findViewById(R.id.tvSignIn);
        signup = findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);

    }

    private void registerUser(final String inputPw, final String inputEmail) {

        if (mytermscondition == 1) {
            Toast.makeText(this, "please Wait", Toast.LENGTH_SHORT).show();
            firebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = firebaseAuth.getCurrentUser();
            if (inputPw.equals("") && inputEmail.equals("")) {
                Toast.makeText(this, "Sorry User and Password is Empty ", Toast.LENGTH_SHORT).show();
            } else {

                final SpotsDialog dialog = new SpotsDialog(RegistrationActivity.this);
                dialog.show();
                DateFormat dateFormatter = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
                dateFormatter.setLenient(false);
                Date today = new Date();
                String s = dateFormatter.format(today);


                firebaseAuth.createUserWithEmailAndPassword(inputEmail, inputPw)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final Users user1 = new Users(newname, inputEmail, "Login with Firebase", s);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    dialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                    } else {

                                                    }
                                                }
                                            });

                                    dialog.dismiss();
                                    SharedPreferences.Editor editor = getSharedPreferences("flags", MODE_PRIVATE).edit();
                                    editor.putInt("flagson", 0);
                                    editor.apply();
                                    SharedPreferences.Editor mygmailchecker = getSharedPreferences("gmailchecker", MODE_PRIVATE).edit();
                                    mygmailchecker.putInt("gmailcheckeron", 1);


                                    mygmailchecker.apply();


                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);
                                    SharedPreferences.Editor myeditor = preferences.edit();
                                    myeditor.putString("Name", newname);
                                    myeditor.apply();
                                    Toasty.success(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
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
                                    Intent i = new Intent(RegistrationActivity.this, DashboardActivity.class);
                                    startActivity(i);




                                    //                            updateUI(user);
                                } else {

                                    dialog.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Toasty.error(RegistrationActivity.this, "Authentication failed.", Toasty.LENGTH_LONG, true).show();

                                }

                                // ...
                            }
                        });


            }

        } else {
            Toasty.error(RegistrationActivity.this, "Please Accept the Terms and Conditions box..", Toasty.LENGTH_LONG, true).show();

        }

    }

    private boolean validateInput(String inPw, String inEmail) {

        if (inPw.isEmpty()) {
            password.setError("Password is empty.");
            return false;
        }
        if (inEmail.isEmpty()) {
            email.setError("Email is empty.");
            return false;
        }

        return true;
    }


}
