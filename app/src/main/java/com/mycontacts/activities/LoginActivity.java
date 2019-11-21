package com.mycontacts.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.R;

public class LoginActivity extends AppCompatActivity {

    private Intent intent;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private boolean setPassFlag = false;
    private SharedPreferences userPasswordSP;
    private SharedPreferences.Editor editor;
    private boolean passExistance;
    private TextView tvSetPass;
    private EditText etPassword;
    private EditText etPasswordComfirm;
    private TextInputLayout lPassword , lPasswordConfirm;
    private Button bLogin;
    private static final String PASSWORD = "password";
    private CharSequence YES = "yes";
    private CharSequence NO = "no";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        tvSetPass = (TextView) findViewById(R.id.tv_setPassword);
        etPassword = (EditText) findViewById(R.id.et_setPassword);
        etPasswordComfirm = (EditText) findViewById(R.id.et_passwordConfirm);
        lPassword = (TextInputLayout) findViewById(R.id.setPassword_lay);
        lPasswordConfirm = (TextInputLayout) findViewById(R.id.passwordConfirm_lay);

        bLogin = (Button) findViewById(R.id.b_login);

        tvSetPass.setVisibility(View.INVISIBLE);
        lPasswordConfirm.setVisibility(View.INVISIBLE);
        lPassword.setVisibility(View.INVISIBLE);
        bLogin.setVisibility(View.INVISIBLE);

        userPasswordSP = getSharedPreferences(PASSWORD, MODE_PRIVATE);
        editor = userPasswordSP.edit();
        passExistance = userPasswordSP.getString(PASSWORD, getString(R.string.empty)).length() > 0;

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.pass_set_ask))
                .setPositiveButton(YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!passExistance) {
                            tvSetPass.setText(getString(R.string.set_password));
                            tvSetPass.setVisibility(View.VISIBLE);
                            lPassword.setVisibility(View.VISIBLE);
                            lPasswordConfirm.setVisibility(View.VISIBLE);
                            bLogin.setVisibility(View.VISIBLE);
                        } else {
                            tvSetPass.setText(getString(R.string.enter_pass));
                            tvSetPass.setVisibility(View.VISIBLE);
                            lPassword.setVisibility(View.VISIBLE);
                            bLogin.setVisibility(View.VISIBLE);
                        }
                        setPassFlag = true;
                    }
                })
                .setNegativeButton(NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        alertDialog = builder.create();
        if (!passExistance)
            alertDialog.show();
        else {
            tvSetPass.setText(getString(R.string.enter_pass));
            tvSetPass.setVisibility(View.VISIBLE);
            lPassword.setVisibility(View.VISIBLE);
            bLogin.setVisibility(View.VISIBLE);
        }



        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passExistance) {
                    if (etPassword.getText().toString().equals(etPasswordComfirm.getText().toString())) {
                        if (etPassword.getText().toString().equals("")) {
                            Toast.makeText(getBaseContext(), getString(R.string.pass_empty), Toast.LENGTH_SHORT).show();
                        } else {
                            editor.putString(PASSWORD, etPassword.getText().toString());
                            editor.apply();
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.pass_confirm_match), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (userPasswordSP.getString(PASSWORD, getString(R.string.empty)).equals(etPassword.getText().toString())) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), R.string.pass_incorrect, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
