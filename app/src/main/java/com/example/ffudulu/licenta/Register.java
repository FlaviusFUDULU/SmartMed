package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

public class Register extends Activity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mRepeatePasswordField;
    private Button mRegisterBtn;
    private Button mBackBtn;
    private ProgressBar mProgressBarRegister;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private final FileCacher<String> userCacherType = new FileCacher<>(Register.this, "type");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        mEmailField = (EditText) findViewById(R.id.txtEmail);
        mPasswordField = (EditText) findViewById(R.id.txtPassword);
        mRepeatePasswordField = (EditText) findViewById(R.id.txtRepeatPassword);
        mRegisterBtn = (Button) findViewById(R.id.btnRegister);

        mProgressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);

        mAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                disableAll();
                mProgressBarRegister.setVisibility(View.VISIBLE);
                tryregister();
            }
        });
    }

    private void tryregister(){

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String repeatPassword = mRepeatePasswordField.getText().toString();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(repeatPassword)){
            mProgressBarRegister.setVisibility(View.GONE);
            Toast.makeText(Register.this, "Unul sau mai multe câmpuri sunt goale!", Toast.LENGTH_LONG).show();
            enableAll();
            mProgressBarRegister.setVisibility(View.GONE);
        }
        else {

            if (password.equals(repeatPassword)) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Nu s-a reușit înregistrarea!\n " +
                                                    "Vă rugăm încercați din nou!",
                                            Toast.LENGTH_SHORT).show();
                                    enableAll();
                                    mProgressBarRegister.setVisibility(View.GONE);
                                }
                                else{
                                    mProgressBarRegister.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Înregistrare reușită!" ,
                                            Toast.LENGTH_SHORT).show();
                                    mProgressBarRegister.setVisibility(View.GONE);
                                    try {
                                        if( userCacherType.readCache().contains("Staff")) {
                                            goToStaffPersonalData();
                                        }
                                        if( userCacherType.readCache().contains("Pacient")) {
                                            goToPacientPersonalData();
                                        }
                                        if( userCacherType.readCache().contains("Family")) {
                                            goToFamilyPersonalData();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
            } else {
                enableAll();
                mProgressBarRegister.setVisibility(View.GONE);
                Toast.makeText(Register.this, "Parolele trebuie să fie la fel!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void disableAll(){
        mRepeatePasswordField.setEnabled(false);
        mPasswordField.setEnabled(false);
        mEmailField.setEnabled(false);
        mRegisterBtn.setEnabled(false);
    }

    private void enableAll(){
        mRepeatePasswordField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mEmailField.setEnabled(true);
        mRegisterBtn.setEnabled(true);
    }

    private void goToStaffPersonalData(){
        Intent mSubmit = new Intent(Register.this, SubmitPersonalData.class);
        startActivity(mSubmit);
    }

    private void goToPacientPersonalData(){
        Intent mSubmitPacient = new Intent(Register.this, SubmitPersonalDataPacient.class);
        startActivity(mSubmitPacient);
    }

    private void goToFamilyPersonalData(){
        Intent mSubmitFamily = new Intent(Register.this, SubmitPersonalDataFamily.class);
        startActivity(mSubmitFamily);
    }

}
