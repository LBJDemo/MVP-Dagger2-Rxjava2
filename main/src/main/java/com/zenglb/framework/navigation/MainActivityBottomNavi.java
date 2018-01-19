package com.zenglb.framework.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.zenglb.framework.R;
import com.zenglb.framework.demo.main.AreUSleepFragmentList;
import com.zenglb.framework.base.mvp.BaseMVPActivity;
import com.zenglb.framework.navigation.fragment.DemoFragment;
import com.zenglb.framework.navigation.fragment.MeProfileFragment;
import com.zenglb.framework.navigation.fragment.Rxjava2DemoFragment;
import com.zenglb.framework.persistence.SPDao;
import com.zenglb.framework.service.MyIntentService1;

import javax.inject.Inject;
import es.dmoral.toasty.Toasty;

/**
 * 这里应该就是一个App 的主控区域，是灵魂吧，优化主要是这里
 *
 * 本来挺好的，但是4/5 个bottom navi 的时候 不能定制样式啊，反射XX
 * Sadly, there isn't any way to force enable or disable this behaviour which may not work with every design.
 * It also doesn't allow populating the Bottom Navigation View with more than five items - as per the design spec
 * (it throws an IllegalArgumentException if you try to).
 *
 *  Created by anylife.zlb@gmail.com on 2017/3/24.
 */
public class MainActivityBottomNavi extends BaseMVPActivity {
    private ViewPager viewPager;
    private MenuItem menuItem;

    @Inject
    SPDao spDao;

    @Inject
    DemoFragment demoFragment;  // Lazy<DemoFragment>
    @Inject
    AreUSleepFragmentList areUSleepFragmentList;
    @Inject
    Rxjava2DemoFragment rxjava2DemoFragment;
    @Inject
    MeProfileFragment meProfileFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    setTitle("主页");
                    return true;

                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    setTitle("I will be confirm");
                    return true;

                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    setTitle("消息");
                    return true;

                case R.id.navigation_set:
                    viewPager.setCurrentItem(3);
                    setTitle("设置");
                    return true;

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Main");

        spDao.toString();


        Intent myIntentService1=new Intent(this,MyIntentService1.class);
        startService(myIntentService1);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main_bottom_navi;
    }

    public void initViews() {
        getToolbar().setNavigationIcon(null);

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setAccessibilityLiveRegion(BottomNavigationView.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("sss",position+" =");
            }

            @Override
            public void onPageSelected(int position) {
                //setTitle(position),
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //禁止ViewPager滑动
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3); //123456789--97534567
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(demoFragment);
        adapter.addFragment(areUSleepFragmentList);
        adapter.addFragment(rxjava2DemoFragment);
        adapter.addFragment(meProfileFragment);

        viewPager.setAdapter(adapter);
    }

    protected boolean isShowBacking() {
        return false;
    }

    /**
     * 快速按2次退出
     *
     */
    private long exitTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime < 2000) {
                finish();
            } else {
                exitTime = System.currentTimeMillis();
                Toasty.info(this.getApplicationContext(), "再按一次退出！", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event); // 按下其他按钮，调用父类进行默认处理
        }
    }


}
