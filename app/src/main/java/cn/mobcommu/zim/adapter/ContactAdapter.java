package cn.mobcommu.zim.adapter;

import cn.mobcommu.zim.R;
import cn.mobcommu.zim.adapter.viewholder.ContactIndexViewHolder;
import cn.mobcommu.zim.adapter.viewholder.ContactViewHolder;
import cn.mobcommu.zim.bean.ContactEntity;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.List;

/**
 * 联系人列表数据适配器
 */
public class ContactAdapter extends IndexStickyViewAdapter<ContactEntity> {
    private Context mContext;
    public ContactAdapter(Context context, List<ContactEntity> originalList) {

        super(originalList);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item_index_view, parent, false);
        return new ContactIndexViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item_view, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

        ContactIndexViewHolder viewHolder = (ContactIndexViewHolder) holder;
        viewHolder.getTextView().setText(indexName);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, ContactEntity itemData) {

        int presence = itemData.getPresence(); // 是否在线
        ContactViewHolder viewHolder = (ContactViewHolder) holder;
        if (presence == 1) {
            viewHolder.getImageView().setImageResource(R.drawable.vector_contact_focus);
        } else {
            viewHolder.getImageView().setImageResource(R.drawable.vector_contact_focus_absence);
        }
        // 依据ofRoster表，联系人显示格式为: nick (jid)
        RosterEntry rosterEntry = itemData.getRosterEntry();
        String contactDisplayText = rosterEntry.getName() + " (" + rosterEntry.getUser() + ")";

        viewHolder.getTextView().setText(contactDisplayText);
    }
}
