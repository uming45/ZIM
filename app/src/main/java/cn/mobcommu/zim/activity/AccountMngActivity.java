package cn.mobcommu.zim.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import butterknife.BindView;
import butterknife.OnClick;
import cn.mobcommu.base.BaseActivity;
import cn.mobcommu.zim.R;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.util.ActivityUtil;
import cn.mobcommu.util.UIUtil;

/**
 * 账号管理
 */
public class AccountMngActivity extends BaseActivity {
    /**
     * 注销登陆
     */
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    /**
     * 用户状态修改
     */
    @BindView(R.id.rg_user_state)
    RadioGroup mUserState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_mng_layout);

        mUserState.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.rb_user_online://在线
                        changeState(0);
                        break;
                    case R.id.rb_user_busy://忙碌
                        changeState(2);
                        break;
                    case R.id.rb_disconnect://断开连接
                        disconnect();
                        break;
                }
            }
        });
    }

    /**
     * 修改状态
     * @param code
     */
    public void changeState(int code) {
        if(SmackManager.getInstance().updateUserState(code)) {
            UIUtil.showToast(this, "修改状态成功");
        } else {
            UIUtil.showToast(this, "修改状态失败");
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if(SmackManager.getInstance().disconnect()) {
            ActivityUtil.finishActivity(this);
        } else {
            UIUtil.showToast(this, "断开连接失败");
        }
    }

    /**
     * 注销登陆
     * @param v
     */
    @OnClick(R.id.btn_logout)
    public void onLogoutClick(View v) {
        if(SmackManager.getInstance().logout()) {
            ActivityUtil.finishActivity(this);
        } else {
            UIUtil.showToast(this, "注销失败");
        }
    }
}
