package cn.mobcommu.zim.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.mobcommu.zim.activity.base.IMBaseActivity;
import cn.mobcommu.zim.constant.Constant;
import cn.mobcommu.zim.fragment.ContactFragment;
import cn.mobcommu.zim.fragment.MessageFragment;
import cn.mobcommu.zim.smack.SmackListenerManager;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.zim.ui.FragmentSaveStateTabHost;
import cn.mobcommu.zim.util.IntentHelper;
import cn.mobcommu.zim.util.LoginHelper;
import cn.mobcommu.zim.util.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 主页面
 */
public class MainActivity extends IMBaseActivity
        implements TabHost.OnTabChangeListener, Toolbar.OnMenuItemClickListener {
    static {
        /**
         * 此方法必须必须引用appcompat-v7:23.4.0
         *
         * Button类控件使用vector必须使用selector进行包装才会起作用，不然会crash
         * 并且使用selector时必须调用下面的方法进行设置，否则也会crash
         * */
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(false);
    }

    private static final Class[] TABBAR_CLASSES = {MessageFragment.class, ContactFragment.class};
    private static final int[] TABBAR_DRAWABLES = {cn.mobcommu.zim.R.drawable.ic_tabbar_message, cn.mobcommu.zim.R.drawable.ic_tabbar_contact};
    private static final int[] TABBAR_NAMES = {cn.mobcommu.zim.R.string.text_message, cn.mobcommu.zim.R.string.text_contact};
    private static final int[] TABBAR_TAGS = {cn.mobcommu.zim.R.string.text_message, cn.mobcommu.zim.R.string.text_contact};

    @BindView(cn.mobcommu.zim.R.id.toolbar)
    Toolbar mToolbar;
    @BindView(cn.mobcommu.zim.R.id.toolbarTitle)
    TextView mToolbarTitle;

    /**
     * 抽屉导航DrawerLayout
     */
    @BindView(cn.mobcommu.zim.R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
//    @BindView(R.id.nav_container)
//    NavigationView mNavigationView;
    @BindView(android.R.id.tabhost)
FragmentSaveStateTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(cn.mobcommu.zim.R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initTabHost();

        //普通消息接收监听
        SmackListenerManager.addGlobalListener();
    }

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /**
         * 注释掉切换抽屉代码
         */
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawerLayout.setDrawerListener(toggle);
//        toggle.syncState();

//        mNavigationView.setNavigationItemSelectedListener(this);
        mToolbar.setNavigationIcon(cn.mobcommu.zim.R.drawable.ic_toolbar_avatar);
        mToolbar.setOnMenuItemClickListener(this);
//        mDrawerLayout.addDrawerListener(new NavDrawerListener());
    }

    /**
     * 主页底部Tab
     */
    private void initTabHost() {

        mTabHost.setup(this, getSupportFragmentManager(), cn.mobcommu.zim.R.id.tabItemContent);
        mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTabHost.setOnTabChangedListener(this);

        for (int i = 0; i < TABBAR_CLASSES.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(TABBAR_TAGS[i]))
                    .setIndicator(getTabHostIndicator(i));
            mTabHost.addTab(tabSpec, TABBAR_CLASSES[i], null);
        }
    }

    private View getTabHostIndicator(int tabIndex) {

        View view = LayoutInflater.from(this).inflate(cn.mobcommu.zim.R.layout.tabbar_item_view, null);

        TextView tabName = ButterKnife.findById(view, cn.mobcommu.zim.R.id.tabbar_name);
        tabName.setText(TABBAR_NAMES[tabIndex]);

        ImageView tabIcon = ButterKnife.findById(view, cn.mobcommu.zim.R.id.tabbar_icon);
        tabIcon.setBackgroundResource(TABBAR_DRAWABLES[tabIndex]);
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {

        mToolbarTitle.setText(tabId);
    }

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//
//        if(item.isChecked()) {
//            mDrawerLayout.closeDrawer(GravityCompat.START);
//            return true;
//        }
//
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.nav_share:
//                ShareHelper.shareApp(mActivity);
//                break;
//            case R.id.nav_about:
//                ActivityUtil.startActivity(mActivity, AboutActivity.class);
//                break;
//        }
//        mDrawerLayout.closeDrawer(GravityCompat.START);
//
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(cn.mobcommu.zim.R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
//            case R.id.toolbar_add_contact:
//                ActivityUtil.startActivity(mActivity, AddFriendActivity.class);
//                break;
//            case R.id.toolbar_create_multi_chat:
//                ActivityUtil.startActivity(mActivity, CreateMultiChatActivity.class);
//                break;
            case cn.mobcommu.zim.R.id.app_exit:
                super.myExitApp();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean doubleExitAppEnable() {

        return true;
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 为抽屉内部设置监听器
     */
//    class NavDrawerListener implements DrawerLayout.DrawerListener {
//
//        @Override
//        public void onDrawerSlide(View drawerView, float slideOffset) {
//
//        }
//
//        @Override
//        public void onDrawerOpened(View drawerView) {
//
//        }
//
//        @Override
//        public void onDrawerClosed(View drawerView) {
//
//            int size = mNavigationView.getMenu().size();
//            for(int i = 0; i < size; i++) {
//                if(mNavigationView.getMenu().getItem(i).isChecked()) {
//                    mNavigationView.getMenu().getItem(i).setChecked(false);
//                    break;
//                }
//            }
//        }
//
//        @Override
//        public void onDrawerStateChanged(int newState) {
//
//        }
//    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (SmackManager.issSmackManager()) {
            try {
                SmackListenerManager.getInstance().destroy(); // 必须删除监听，否则登录另一个用户，第一次登录不上
                SmackManager.getInstance().logout();
                SmackManager.getInstance().disconnect();
            } catch (Exception e) {
                // 第一行会有异常，暂不对异常进行处理
            }
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // 异步调用，查询当前用户的类型 user or doctor
        getUserType(LoginHelper.getUser().getUsername(), Constant.API_SERVER_IP);
    }

    /**
     * 异步调用，获取当前用户的用户类型，user store, doctor
     */
    private void getUserType(String username, String ip) {

        final String url = "http://" + ip + "/b2b2c/api/mobile/doctor/getUserCategery.do?username=" + username;
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtils.get(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String userType = jsonObject.getString("data");
                            LoginHelper.setUserType(userType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchTabFragmentEvent(Integer index) {

        if(index == IntentHelper.CONTACT_TAB_INDEX || index == IntentHelper.MESSAGE_TAB_INDEX) {
            mTabHost.setCurrentTab(index);
        }
    }
}
