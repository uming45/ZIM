package cn.mobcommu.zim.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 在线状态帮助类
 * 参考：http://blog.csdn.net/newjueqi/article/details/8613125
 */

public class PresenceUtil {

    /**
     * 判断用户是否在线
     * @param strUrl http://localhost:9090/plugins/presence/status?jid=602@laboratory&type=xml
     * @return 返回值: 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
     */
    public  static int IsUserOnLine(String strUrl) {

        int state = 0;

        try {
            URL oUrl = new URL(strUrl);
            URLConnection oConn = oUrl.openConnection();
            if (oConn != null) {
                BufferedReader oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
                if (null != oIn) {
                    String strFlag = oIn.readLine();
                    oIn.close();
                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
                        state = 2;
                    }
                    if (strFlag.indexOf("type=\"error\"") >= 0) {
                        state = 0;
                    } else if (strFlag.indexOf("priority") >= 0 || strFlag.indexOf("id=\"") >= 0) {
                        state = 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }
}
