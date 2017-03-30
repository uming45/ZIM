package cn.ittiger.im.util;

/**
 * 解决xxx@x.x.x.x@x.x.x.x中@域出现两次的问题
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
