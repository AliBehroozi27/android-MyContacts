package com.mycontacts.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView tvSetPass;
    private EditText etPassword;
    private EditText etPasswordComfirm;
    private Button bChange;
    private EditText etNewPassword;
    private SharedPreferences userPasswordSP;
    private SharedPreferences.Editor editor;
    private boolean passExistance;
    private TextInputLayout lPassword;
    private TextInputLayout lNewPassword;
    private TextInputLayout lPasswordConfirm;
    private String PASSWORD = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tvSetPass = (TextView) findViewById(R.id.tv_setPassword);
        etNewPassword = (EditText) findViewById(R.id.et_setPassword);
        etPasswordComfirm = (EditText) findViewById(R.id.et_passwordConfirm);
        etPassword = (EditText) findViewById(R.id.et_currentPassword);
        lPassword = (TextInputLayout) findViewById(R.id.currentPassword_lay);
        lPasswordConfirm = (TextInputLayout) findViewById(R.id.passwordConfirm_lay);
        lNewPassword = (TextInputLayout) findViewById(R.id.setPassword_lay);

        bChange = (Button) findViewById(R.id.b_login);

        userPasswordSP = getSharedPreferences(PASSWORD, MODE_PRIVATE);
        editor = userPasswordSP.edit();
        passExistance = userPasswordSP.getString(PASSWORD, getString(R.string.empty)).length() > 0;

        if (!passExistance) {
            tvSetPass.setText(R.string.set_password);
            bChange.setText(getString(R.string.set));
            lPassword.setVisibility(View.INVISIBLE);
        }

        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passExistance) {
                    if (userPasswordSP.getString(PASSWORD, getString(R.string.what)).equals(etPassword.getText().toString())) {
                        if (etNewPassword.getText().toString().equals(etPasswordComfirm.getText().toString())) {
                            if (etNewPassword.getText().toString().equals(getString(R.string.empty))) {
                                Toast.makeText(getBaseContext(), getString(R.string.new_pass_empty), Toast.LENGTH_SHORT).show();
                            } else {
                                editor.putString(PASSWORD, etNewPassword.getText().toString());
                                editor.apply();
                                Toast.makeText(getBaseContext(), getString(R.string.pass_changed), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.new_pass_confirm_match), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.pass_incorrect), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (etNewPassword.getText().toString().equals(etPasswordComfirm.getText().toString())) {
                        if (etNewPassword.getText().toString().equals(getString(R.string.empty))) {
                            Toast.makeText(getBaseContext(), getString(R.string.pass_empty), Toast.LENGTH_SHORT).show();
                        } else {
                            editor.putString(PASSWORD, etNewPassword.getText().toString());
                            editor.apply();
                            Toast.makeText(getBaseContext(), getString(R.string.pass_set), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.pass_confirm_match), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }


}
