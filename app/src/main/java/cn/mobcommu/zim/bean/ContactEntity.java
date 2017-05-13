package cn.mobcommu.zim.bean;

import cn.ittiger.indexlist.entity.BaseEntity;

import org.jivesoftware.smack.roster.RosterEntry;

/**
 * 联系人实体
 */
public class ContactEntity implements BaseEntity {
    private RosterEntry mRosterEntry;
    private int presence;

    public ContactEntity(RosterEntry rosterEntry) {

        mRosterEntry = rosterEntry;
        presence = -1;
    }

    public ContactEntity(RosterEntry rosterEntry, int presence) {

        mRosterEntry = rosterEntry;
        this.presence = presence;
    }

    @Override
    public String getIndexField() {

        return mRosterEntry.getName();
    }

    public RosterEntry getRosterEntry() {

        return mRosterEntry;
    }

    public int getPresence() {
        return presence;
    }
}
