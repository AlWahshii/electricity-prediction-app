package com.electricty.predict;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ForgetPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        final EditText atvEmailLog = findViewById(R.id.atvEmailLog);
        Button i = findViewById(R.id.btnSignIn);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!atvEmailLog.getText().toString().isEmpty())
                {
                    final SpotsDialog dialog = new SpotsDialog(ForgetPasswordActivity.this);
                    dialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(atvEmailLog.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toasty.success(ForgetPasswordActivity.this, "Your password send to your Email Id", Toast.LENGTH_LONG, true).show();
                                    } else {
                                        dialog.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toasty.error(ForgetPasswordActivity.this, "Email Id is not matched", Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                            });
                }else {
                     Toasty.error(ForgetPasswordActivity.this, "lease Write Email Id to", Toast.LENGTH_SHORT, true).show();

                }

            }
        });
    }
}