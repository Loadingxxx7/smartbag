package com.xzh.smartbag_2.Fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.xzh.smartbag_2.Datapoints;
import com.xzh.smartbag_2.Datastreams;
import com.xzh.smartbag_2.JsonRootBean;
import com.xzh.smartbag_2.R;
import com.xzh.smartbag_2.TestData;
import com.xzh.smartbag_2.value;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.hydrion.onenethelper.Device;

/**
 * Created by Lenovo on 2019/11/16.
 */
public class AFragment extends Fragment implements GeocodeSearch.OnGeocodeSearchListener{

    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    private CameraUpdate mCameraUpdate;
    public String latData,lngData,anomalous,Gps,num;
    private double lat,lng;
    private int Num;
    private static final int COMPLETED = 0;

    private Timer mTimer;
    String followMove;

    String addressName ;
    GeocodeSearch geocodeSearch;


    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_a,container,false);
        initUi();


        mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null){
            aMap = mMapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setCompassEnabled(true);//指南针
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if ( !followMove.equals(false)){
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
                }
            }
        });
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                followMove = "false";
            }
        });

        //循环函数调用子线程并添加Marker
        TimerTask timerTask =new TimerTask() {
            @Override
            public void run() {
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat,lng)));
                drawMarkers();
                getAddress(new LatLonPoint(lat,lng));
                new WorkThread().start();

            }
        };

        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                aMap.clear();
                savePost();
            }
        };

        mTimer = new Timer();
        mTimer.schedule(timerTask,0,1000);//1秒刷新一次，并放置一个marker
        mTimer.schedule(timerTask1,0,60000);//一分钟清空一次marker,一分钟记录一次坐标数据

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "background";
            String channelName = "后台运行";
            int important = NotificationManager.IMPORTANCE_MAX;
            createNotificationChannel(channelId,channelName,important);

            channelId = "anomalous";
            channelName = "碰撞异常";
            important = NotificationManager.IMPORTANCE_MAX;
            createNotificationChannel(channelId,channelName,important);

        }



        Running();
        return rootView;
    }



    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int important) {
        NotificationChannel notificationChannel  = new NotificationChannel(channelId,channelName,important);
        NotificationManager notificationManager = (NotificationManager)rootView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //生成marker
    private void drawMarkers() {
        Marker marker = aMap.addMarker(new MarkerOptions()
        .position(new LatLng(lat,lng))
        .title("设备ID:"+Num+"\n"+addressName)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        .draggable(true));
        marker.showInfoWindow();
    }

    private void initUi() {
    }



    //逆地理编码
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 1000){
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null){
                addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    public void getAddress(LatLonPoint latLonPoint){
        geocodeSearch = new GeocodeSearch(getContext());
        geocodeSearch.setOnGeocodeSearchListener(this);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint,20,GeocodeSearch.AMAP);

        geocodeSearch.getFromLocationAsyn(query);
    }


    private class WorkThread extends Thread{
        @Override
        public void run() {
            Device device = new Device("556919582","NgO4Wf6bzwyEmijaKrfPlmUK8NE=");
//            latData = String.valueOf(device.getCurrentDataPoint("lat").getValue());
//            lngData = String.valueOf(device.getCurrentDataPoint("lon").getValue());
            anomalous = String.valueOf(device.getCurrentDataPoint("MPU").getValue());
//            Gps = String.valueOf(device.getCurrentDataPoint("GPS"));





                //获取数据
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.heclouds.com/devices/589099681/datapoints")
                        .header("api-key","SMOdLIKm2eeAMEgkxR==8M2nraU=").build();
                Response response = client.newCall(request).execute();
                Gps = response.body().string();

                Gson gson = new Gson();
                TestData testData =gson.fromJson(Gps,TestData.class);

                latData = String.valueOf(testData.getData().getDatastreams().get(0).getDatapoints().get(0).getValue().getLat());
                lngData = String.valueOf(testData.getData().getDatastreams().get(0).getDatapoints().get(0).getValue().getLon());
                num = String.valueOf(testData.getData().getDatastreams().get(0).getDatapoints().get(0).getValue().getNumber());

                Message msg = new Message();
                msg.what = COMPLETED;
                handler.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }




        }
    }

 /*   private void parseJSONWithGSON(String jsonData) {
        JsonRootBean app =new Gson().fromJson(jsonData,JsonRootBean.class);
        List<Datastreams> datastreams = app.getData().getDatastreams();
        List<Datapoints> datapoints = datastreams.get(0).getDatapoints();
        List<value> values = datapoints.get(0).getValue();
        for (int i = 0;i < datapoints.size();i++) {
                    lngData = values.get(i).getLon();
                    latData = values.get(i).getLat();


        }
    }
*/

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {


            lat = Double.parseDouble(latData);
            lng = Double.parseDouble(lngData);
            Num = Integer.parseInt(num);
            if (msg.what == COMPLETED){
                if (anomalous.equals("0")){
                    //安全

                }else {
                    ReceiveAnomalous();
                }
            }
        }
    };

    //"后台运行"通知栏
    private void Running() {
        NotificationManager manager = (NotificationManager) rootView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(rootView.getContext(),"background")
                .setContentTitle("SmartBag正在运行")
//                .setContentText("点击进入")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_bag)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_bag))
                .setOngoing(true)
                .setAutoCancel(true)//打开程序后图标消失
                .build();
        manager.notify(1,notification);
    }

    //"碰撞异常"通知栏
    private void ReceiveAnomalous() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(getContext(),AFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Context mContext = getContext();

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,0);
        NotificationManager manager = (NotificationManager) rootView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(rootView.getContext(),"anomalous")
                .setContentTitle("安全提醒")
                .setContentText("书包安全状态警告")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_bag)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_bag))
                .setAutoCancel(true)//打开程序后图标消失
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(2,notification);
    }

    //生命周期
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private class Post extends BmobObject {
        private String Geo_Coordinates;

        private BmobUser username;

        public String getGeo_Coordinates(){
            return Geo_Coordinates;
        }
        public Post setGeo_Coordinates(String geo_coordinates){
            this.Geo_Coordinates = geo_coordinates;
            return this;
        }

        public BmobUser getUsername(){
            return username;
        }
        public Post setUsername(BmobUser username){
            this.username = username;
            return this;
        }
    }

    private void savePost() {
        if (BmobUser.isLogin()){
            Post post = new Post();
            post.setGeo_Coordinates(("纬度:"+lat+",经度:"+lng).toString());
            post.setUsername(BmobUser.getCurrentUser(BmobUser.class));
            post.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null){
//                        Toast.makeText(rootView.getContext(),"fasong",Toast.LENGTH_SHORT).show();
                    }else {

                    }
                }
            });

        }else {

        }
    }



}

//http://api.heclouds.com/devices/535237825/datapoints?datastream&start=2019-07-10T08:00:35&limit=30&sort=DESC