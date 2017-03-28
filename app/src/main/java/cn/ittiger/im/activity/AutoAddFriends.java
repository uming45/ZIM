package cn.ittiger.im.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.RosterEntry;

import cn.ittiger.im.R;
import cn.ittiger.im.bean.ContactEntity;
import cn.ittiger.im.bean.LoginResult;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.util.ActivityUtil;
import cn.ittiger.util.UIUtil;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 自动添加好友
 */

public class AutoAddFriends extends AppCompatActivity {

    private boolean onCreate_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        this.onCreate_flag = true;

        Uri myUri = getIntent().getData();
        final String user1 = myUri.getQueryParameter("user1");
        final String user2 = myUri.getQueryParameter("user2");

        // 判断用户user1是否已登录
        User user = LoginHelper.getUser();
        final String username = user.getUsername();
        if ("".equals(username) || !username.equals(user1)) { // 未登录

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("user1", user1);
            intent.putExtra("user2", user2);
            startActivity(intent); // 跳转到登录界面

        } else { // 登录过，且参数中用户名与登录过的用户的用户名一致，则自动登录，调转到主页面
            final String password = user.getPassword();
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
                            // LoginHelper.rememberRassword(mCbRememberPassword.isChecked());
                        }
                    })
                    .subscribe(new Action1<LoginResult>() {
                        @Override
                        public void call(LoginResult loginResult) {

                            if (loginResult.isSuccess()) {
                                addFriend(user2, user2);

                                ActivityUtil.skipActivity(AutoAddFriends.this, MainActivity.class);
                            } else {
                                UIUtil.showToast(AutoAddFriends.this, "用户名或密码不正确\n" + loginResult.getErrorMsg());
                            }
                        }
                    });
        }

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

                UIUtil.showToast(AutoAddFriends.this, R.string.hint_add_friend_success);
                ActivityUtil.finishActivity(AutoAddFriends.this);
            }

            @Override
            public void onError(Throwable e) {

                UIUtil.showToast(AutoAddFriends.this, R.string.hint_add_friend_failure);
            }

            @Override
            public void onNext(RosterEntry rosterEntry) {

                EventBus.getDefault().post(new ContactEntity(rosterEntry));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (this.onCreate_flag) { // 如果执行了onCreate()，则允许执行下面的语句
            this.onCreate_flag = false;
        } else { // 否则退出
            super.onBackPressed();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
