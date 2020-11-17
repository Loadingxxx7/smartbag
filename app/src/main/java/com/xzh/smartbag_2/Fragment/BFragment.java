package com.xzh.smartbag_2.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xzh.smartbag_2.MainActivity;
import com.xzh.smartbag_2.R;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cn.bmob.v3.BmobUser;
import xyz.hydrion.onenethelper.Device;


/**
 * Created by Lenovo on 2019/11/16.
 */
public class BFragment extends Fragment {

    private View rootView;
    private Button mBtnLogout;
    private TextView mTvUser,mTvequip,mTvOnline,mTvAnomalous;

    private String equipageName,isOnline,anomalous;
    private static final int COMPLETED = 0;

    private Timer mTimer;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_b,container,false);
        mBtnLogout = rootView.findViewById(R.id.btn_logout);
        mTvUser = rootView.findViewById(R.id.tv_user);
        mTvequip = rootView.findViewById(R.id.tv_1);
        mTvOnline = rootView.findViewById(R.id.tv_2);
        mTvAnomalous = rootView.findViewById(R.id.tv_3);

        mTvAnomalous.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"书包的安全状态",Toast.LENGTH_LONG).show();
                return true;
            }
        });


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                new WorkThread1().start();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(timerTask,0,2000);


        if (BmobUser.isLogin()){
            BmobUser user = BmobUser.getCurrentUser(BmobUser.class);
            String name = user.getUsername();
            mTvUser.setText("用户名:"+name);
        }else {
            mTvUser.setText("未登录");
        }

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();
                Toast.makeText(rootView.getContext(),"退出登录",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(rootView.getContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        initUi();
        return rootView;
    }


    private void initUi() {
    }

    private class WorkThread1 extends Thread{
        @Override
        public void run() {
            Device device = new Device("556919582","NgO4Wf6bzwyEmijaKrfPlmUK8NE=");
            equipageName = device.getDeviceTitle();
            isOnline = String.valueOf(device.isOnline());
            anomalous = String.valueOf(device.getCurrentDataPoint("MPU").getValue());

            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            mTvequip.setText("设备名称: "+equipageName);
            if (msg.what == COMPLETED){
                if (isOnline.equals("false")){
                    //设备离线
                    mTvOnline.setText("设备状态: 离线");
                }else {
                    //设备在线
                    mTvOnline.setText("设备状态: 在线");
                }
                if (anomalous.equals("1")){
                    //碰撞
                    mTvAnomalous.setText("安全状态:撞击");
                }else if (anomalous.equals("2")){
                    //溺水
                    mTvAnomalous.setText("安全状态:溺水");
                }
                else {
                    //安全
                    mTvAnomalous.setText("安全状态:安全");
                }
            }
        }
    };
}
