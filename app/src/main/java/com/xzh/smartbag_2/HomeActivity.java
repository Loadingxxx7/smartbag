package com.xzh.smartbag_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.xzh.smartbag_2.Fragment.AFragment;
import com.xzh.smartbag_2.Fragment.BFragment;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题的集合
    private Fragment aFragment,bFragment,cFragment;//页卡视图
    private ArrayList<Fragment> mViewList = new ArrayList<>();//页卡视图的集合

    private long exitTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bmob.initialize(this,"9ffdd09cd195d70377bd25d8bcde1b64");

        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tab);



        //通过缓存的用户信息实现记忆登录
        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        if (bmobUser != null){

        }else {
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
        }

        aFragment = new AFragment();
        bFragment = new BFragment();
//        cFragment = new CFragment();

        //添加页卡视图
        mViewList.add(aFragment);
        mViewList.add(bFragment);
//        mViewList.add(cFragment);

        //添加页卡标题
        mTitleList.add("");
        mTitleList.add("");
//        mTitleList.add("");

        //设置Tab模式 默认为fixed
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);


        //添加Tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mViewList.get(position);
            }

            @Override
            public int getCount() {
                return mTitleList.size();
            }

            public CharSequence getPageTitle(int position){
                return mTitleList.get(position);
            }
        });

        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);

        //设置Tablaout图标
        mTabLayout.getTabAt(0).setIcon(R.drawable.icon_map);
        mTabLayout.getTabAt(1).setIcon(R.drawable.icon_me);
//        mTabLayout.getTabAt(2).setIcon(R.drawable.icon_me);
    }

    //重写返回键点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(HomeActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
