package com.xzh.smartbag_2;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mEtuser,mEtpassword;
    private Button mBtnlogin,mBtnregister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this,"9ffdd09cd195d70377bd25d8bcde1b64");

        mEtuser = findViewById(R.id.et_1);
        mEtpassword = findViewById(R.id.et_2);

        mBtnlogin = findViewById(R.id.btn_login);
        mBtnregister = findViewById(R.id.btn_register);

        mBtnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mBtnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            moveTaskToBack(true);
        }
        return true;
    }

    private void login(){
        final String name = mEtuser.getText().toString();
        final String password = mEtpassword.getText().toString();

        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this,"请输入用户名或密码",Toast.LENGTH_SHORT).show();
        }else {
            final BmobUser user = new BmobUser();
            user.setUsername(name);
            user.setPassword(password);
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e == null){
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
