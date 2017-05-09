package com.example.geyan.access;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geyan.mp3player.MainActivity;
import com.example.geyan.mp3player.PlayerActivity;
import com.example.geyan.mp3player.R;

/**
 * Created by geyan on 06/05/2017.
 */

public class Login extends AppCompatActivity {

    private Button login_btn;
    private Button signup_btn;
    private EditText userName_edit;
    private EditText password_edit;
    private String user_name;
    private String password;
    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login_btn = (Button) findViewById(R.id.login_btn);
        signup_btn = (Button) findViewById(R.id.sign_up_btn);
        userName_edit = (EditText) findViewById(R.id.user_name_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = userName_edit.getText().toString();
                password = password_edit.getText().toString();
                DatabaseHelper databaseHelper = new DatabaseHelper(Login.this);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("usersInfos",new String[]{"name,password"},null,null,null,null,null);
                while (cursor.moveToNext()){
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String passwordI = cursor.getString(cursor.getColumnIndex("password"));
                    if (name.equals(user_name)&&passwordI.equals(password)){
                        Intent intent = new Intent();
                        intent.putExtra("user_name",user_name);
                        intent.setClass(Login.this,MainActivity.class);
                        isLogin = true;
                        intent.putExtra("islogin",isLogin);
                        startActivity(intent);
                        break;
                    }else {
                        Toast.makeText(Login.this,"user name or password is incorrect!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login.this,Signup.class);
                startActivity(intent);
            }
        });
    }
}
