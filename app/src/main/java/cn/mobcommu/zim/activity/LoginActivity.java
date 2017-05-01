package cn.mobcommu.zim.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mobcommu.zim.R;
import cn.mobcommu.zim.activity.base.IMBaseActivity;
import cn.mobcommu.zim.bean.ContactEntity;
import cn.mobcommu.zim.bean.LoginResult;
import cn.mobcommu.zim.bean.User;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.zim.ui.ClearEditText;
import cn.mobcommu.zim.util.LoginHelper;
import cn.mobcommu.util.ActivityUtil;
import cn.mobcommu.util.UIUtil;
import cn.mobcommu.util.ValueUtil;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.RosterEntry;

/**
 * 登陆openfire服务器
 */
public class LoginActivity extends IMBaseActivity {
    /**
     * 登陆用户
     */
    @BindView(R.id.et_login_username)
    ClearEditText mEditTextUser;
    /**
     * 登陆密码
     */
    @BindView(R.id.et_login_password)
    ClearEditText mEditTextPwd;
    /**
     * 登陆按钮
     */
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    /**
     * 记住密码
     */
    @BindView(R.id.cb_remember_password)
    AppCompatCheckBox mCbRememberPassword;

    private String user1;
    private String friend_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        user1 = intent.getStringExtra("user1");
        friend_name = intent.getStringExtra("user2");

        initViews();
        initUserInfo();
    }


    private void initViews() {

        mEditTextUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mEditTextPwd.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initUserInfo() {

        boolean isRemember = LoginHelper.isRememberPassword();
        if (isRemember) {
            User user = LoginHelper.getUser();
            mEditTextUser.setText(user.getUsername());
            mEditTextPwd.setText(user.getPassword());
        }
        mCbRememberPassword.setChecked(isRemember);
    }

    /**
     * 登陆响应
     *
     * @param v
     */
    @OnClick(R.id.btn_login)
    public void onLoginClick(View v) {

        final String username = mEditTextUser.getText().toString();
        final String password = mEditTextPwd.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            UIUtil.showToast(this, getString(R.string.login_error_user));
            return;
        }
        if (ValueUtil.isEmpty(password)) {
            UIUtil.showToast(this, getString(R.string.login_error_password));
            return;
        }

        mBtnLogin.setEnabled(false);
        mBtnLogin.setText(getString(R.string.login_button_login_loading));
        Observable.just(new User(username, password))
            .subscribeOn(Schedulers.io())//指定下面的flatMap线程
            .flatMap(new Func1<User, Observable<LoginResult>>() {
                @Override
                public Observable<LoginResult> call(User user) {

                    LoginResult loginResult = SmackManager.getInstance().login(username, password);
                    return Observable.just(loginResult);
                }
            })
            .observeOn(AndroidSchedulers.mainThread())//给下面的subscribe设定线程
            .doOnNext(new Action1<LoginResult>() {
                @Override
                public void call(LoginResult loginResult) {

                    LoginHelper.rememberRassword(mCbRememberPassword.isChecked());
                }
            })
            .subscribe(new Action1<LoginResult>() {
                @Override
                public void call(LoginResult loginResult) {

                    if (loginResult.isSuccess()) {
                        if (mCbRememberPassword.isChecked()) {
                            LoginHelper.saveUser(loginResult.getUser());
                        }

                        // 必须是user1登录才可以添加好友
                        if (friend_name != null && loginResult.getUser().getUsername().equals(user1)) {
                            addFriend(friend_name, friend_name);
                        }

                        ActivityUtil.skipActivity(LoginActivity.this, MainActivity.class);
                    } else {
                        mBtnLogin.setEnabled(true);
                        mBtnLogin.setText(getString(R.string.login_button_unlogin_text));
                        UIUtil.showToast(LoginActivity.this, "用户名或密码不正确\n" + loginResult.getErrorMsg());
                    }
                }
            });
    }

    protected void addFriend(final String username, final String nickname) {
        Observable.create(new Observable.OnSubscribe<RosterEntry>() {
            @Override
            public void call(Subscriber<? super RosterEntry> subscriber) {

                boolean flag = SmackManager.getInstance().addFriend(username, nickname, null);
                if(flag) {
                    RosterEntry entry = SmackManager.getInstance().getFriend(username);
                    subscriber.onNext(entry);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new IllegalArgumentException());
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<RosterEntry>() {
            @Override
            public void onCompleted() {

                UIUtil.showToast(LoginActivity.this, R.string.hint_add_friend_success);
                ActivityUtil.finishActivity(LoginActivity.this);
            }

            @Override
            public void onError(Throwable e) {

                UIUtil.showToast(LoginActivity.this, R.string.hint_add_friend_failure);
            }

            @Override
            public void onNext(RosterEntry rosterEntry) {

                EventBus.getDefault().post(new ContactEntity(rosterEntry));
            }
        });
    }

    /**
     * 用户注册
     *
     * @param v
     */
//    @OnClick(R.id.tv_login_register)
//    public void onRegisterClick(View v) {
//
//        ActivityUtil.startActivity(this, RegisterActivity.class);
//    }
}
