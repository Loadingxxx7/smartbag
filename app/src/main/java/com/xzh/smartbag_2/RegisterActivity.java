package com.xzh.smartbag_2;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEduser1,mEdpassword1,mEdpassword2;
    private Button mBtnregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bmob.initialize(this,"9ffdd09cd195d70377bd25d8bcde1b64");

        mEduser1 = findViewById(R.id.et_11);
        mEdpassword1 = findViewById(R.id.et_12);
        mEdpassword2 = findViewById(R.id.et_13);

        mBtnregister = findViewById(R.id.btn_register1);
        mBtnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        final String name = mEduser1.getText().toString();
        final String password = mEdpassword1.getText().toString();
        final String password2 = mEdpassword2.getText().toString();

        if (!password.equals(password2)){
            Toast.makeText(RegisterActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(name)|| TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
        }else {
            BmobUser user = new BmobUser();
            user.setUsername(name);
            user.setPassword(password);
            user.signUp(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e ==null){
                        Toast.makeText(RegisterActivity.this,"注册成功，请登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
