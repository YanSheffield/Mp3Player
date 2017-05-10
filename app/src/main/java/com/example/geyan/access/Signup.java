package com.example.geyan.access;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geyan.model.UserInfo;
import com.example.geyan.mp3player.MainActivity;
import com.example.geyan.mp3player.R;

/**
 * Created by geyan on 06/05/2017.
 */

public class Signup extends Activity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private Button sign_up_btn;

    private String nameStr;
    private String emailStr;
    private String passwordStr;
    private String password_confirm_Str;

    private UserInfo usersinfo = null;
    private DatabaseHelper helper = new DatabaseHelper(Signup.this);
    private Button cancelbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        name = (EditText) findViewById(R.id.name_edit);
        email = (EditText) findViewById(R.id.email_edit);
        password = (EditText) findViewById(R.id.password_edit);
        confirm_password = (EditText) findViewById(R.id.confirm_password_edit);
        sign_up_btn = (Button) findViewById(R.id.sign_up_btn_confirm);
        cancelbtn = (Button) findViewById(R.id.cancel);
        usersinfo = new UserInfo();
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            int id = 1;
            @Override
            public void onClick(View v) {
                id = id +1;
                nameStr = name.getText().toString();
                emailStr = email.getText().toString();
                passwordStr = password.getText().toString();
                password_confirm_Str = confirm_password.getText().toString();

                if (nameStr.length()==0){
                    Toast.makeText(Signup.this, "please enter your name",Toast.LENGTH_LONG).show();
                }else if(!emailStr.contains("@")){
                    Toast.makeText(Signup.this, "email is invalid",Toast.LENGTH_LONG).show();
                }else if (passwordStr.length()<5){
                    Toast.makeText(Signup.this, "the length of password is short!",Toast.LENGTH_LONG).show();
                }else if (!passwordStr.equals(password_confirm_Str)){
                    Toast.makeText(Signup.this, "inputted passwords are different",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Signup.this, "sign up success!",Toast.LENGTH_LONG).show();
                    usersinfo.setName(nameStr);
                    usersinfo.setEmail(emailStr);
                    usersinfo.setPassword(passwordStr);
                    helper.insert(usersinfo,id);
                    Intent intent = new Intent();
                    intent.setClass(Signup.this, MainActivity.class);
                    intent.putExtra("user_name",nameStr);
                    intent.putExtra("islogin",true);
                    startActivity(intent);
                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Signup.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
