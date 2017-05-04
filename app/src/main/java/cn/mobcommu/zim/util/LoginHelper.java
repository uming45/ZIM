package cn.mobcommu.zim.util;

import cn.mobcommu.zim.bean.LoginResult;
import cn.mobcommu.zim.bean.User;
import cn.mobcommu.util.PreferenceHelper;

public class LoginHelper {

    private static final String KEY_REMEMBER_PASSWORD = "pre_key_remember_password";
    private static final String KEY_USER = "pre_key_user";
    private static LoginResult loginResult;

    /**
     * 是否记住密码
     *
     * @return
     */
    public static boolean isRememberPassword() {

        return PreferenceHelper.getBoolean(KEY_REMEMBER_PASSWORD, true); // 设置默认记住密码
    }

    public static void rememberRassword(boolean isRemember) {

        PreferenceHelper.putBoolean(KEY_REMEMBER_PASSWORD, isRemember);
    }

    public static User getUser() {

        User user = (User) PreferenceHelper.get(KEY_USER);
        if(user == null) {
            user = new User("", "");
        }
        return user;
    }

    public static void saveUser(User user) {

        PreferenceHelper.put(KEY_USER, user);
    }

    public static LoginResult getLoginResult() {
        return loginResult;
    }

    public static void setLoginResult(LoginResult lr) {
        loginResult = lr;
    }
}
