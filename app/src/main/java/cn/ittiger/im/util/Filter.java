package cn.ittiger.im.util;

/**
 * Created by admin on 2017/3/29.
 */

public class Filter {
    public int PositionOfFirstAt,PositionOfSecondAt;
    public String filterFileJid(String Jid){
        PositionOfFirstAt = Jid.indexOf('@');
        PositionOfSecondAt = Jid.lastIndexOf('@');
        if(PositionOfSecondAt == PositionOfFirstAt){
            return Jid;
        }else {
            String SubStringHead = Jid.substring(0,PositionOfFirstAt);
            String SubStringTail = Jid.substring(PositionOfSecondAt,Jid.length());
            return SubStringHead + SubStringTail;
        }

    }
}
