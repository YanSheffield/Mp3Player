package com.example.geyan.mp3player;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geyan.access.Login;
import com.example.geyan.access.Signup;
import com.example.geyan.util.PageAdapter;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    PageAdapter adapter = null;
    private TextView userText;
    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Remote"));
        Intent intent = getIntent();
        isLogin = intent.getBooleanExtra("islogin",false);
        if (isLogin){
            userText = (TextView) findViewById(R.id.username);
            userText.setText("Welcome ! "+intent.getStringExtra("user_name"));
            tabLayout.addTab(tabLayout.newTab().setText("Local"));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        if (!isLogin){
            menu.add(0,1,1,R.string.sign_in);
            menu.add(0,2,2, R.string.sign_up);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (!isLogin){
            if (item.getItemId()==1){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Login.class);
                startActivity(intent);
            }
            if (item.getItemId()==2){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
