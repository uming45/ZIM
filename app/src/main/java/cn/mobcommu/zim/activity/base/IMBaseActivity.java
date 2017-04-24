package cn.mobcommu.zim.activity.base;

import cn.mobcommu.base.BaseActivity;

import android.os.Build;
import android.os.Bundle;

/**
 * 即时通讯基类
 */
public class IMBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initNavigationBarColor();
    }

    private void initNavigationBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(cn.mobcommu.zim.R.color.main_color));
        }
    }

    /**
     * 是否在初始化的时候需要查询数据，并显示查询等待进度条，查询失败显示失败提示
     *
     * @return  if return true, you just to override refreshData() method to loading data
     */
    @Override
    public boolean isLceActivity() {

        return false;
    }
}
