package cn.mobcommu.zim.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mobcommu.zim.activity.base.IMBaseActivity;
import cn.mobcommu.zim.bean.ContactEntity;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.util.ActivityUtil;
import cn.mobcommu.util.UIUtil;
import cn.mobcommu.util.ValueUtil;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.RosterEntry;

/**
 * 添加好友
 */
public class AddFriendActivity extends IMBaseActivity {
    @BindView(cn.mobcommu.zim.R.id.toolbar)
    Toolbar mToolbar;
    @BindView(cn.mobcommu.zim.R.id.toolbarTitle)
    TextView mToolbarTitle;
    @BindView(cn.mobcommu.zim.R.id.til_friend_user)
    TextInputLayout mUserTextInput;
    @BindView(cn.mobcommu.zim.R.id.acet_friend_user)
    AppCompatEditText mUserEditText;
    @BindView(cn.mobcommu.zim.R.id.til_friend_nickname)
    TextInputLayout mNickNameTextInput;
    @BindView(cn.mobcommu.zim.R.id.acet_friend_nickname)
    AppCompatEditText mNickNameEditText;
    @BindView(cn.mobcommu.zim.R.id.btn_add_friend)
    Button mBtnAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(cn.mobcommu.zim.R.layout.activity_addfriend_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(getString(cn.mobcommu.zim.R.string.title_add_friend));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    @OnClick(cn.mobcommu.zim.R.id.btn_add_friend)
    public void onAddFriendClick(View v) {

        final String username = mUserEditText.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            mUserTextInput.setError(getString(cn.mobcommu.zim.R.string.error_input_friend_username));
            return;
        }

        final String nickname = mNickNameEditText.getText().toString();
        if (ValueUtil.isEmpty(nickname)) {
            mNickNameTextInput.setError(getString(cn.mobcommu.zim.R.string.error_input_friend_username));
            return;
        }

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

                UIUtil.showToast(mActivity, cn.mobcommu.zim.R.string.hint_add_friend_success);
                ActivityUtil.finishActivity(mActivity);
            }

            @Override
            public void onError(Throwable e) {

                UIUtil.showToast(mActivity, cn.mobcommu.zim.R.string.hint_add_friend_failure);
            }

            @Override
            public void onNext(RosterEntry rosterEntry) {

                EventBus.getDefault().post(new ContactEntity(rosterEntry));
            }
        });
    }
}
