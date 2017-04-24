package cn.mobcommu.zim.adapter;

import cn.mobcommu.zim.adapter.viewholder.ContactIndexViewHolder;
import cn.mobcommu.zim.adapter.viewholder.CreateMultiChatViewHolder;
import cn.mobcommu.zim.bean.CheckableContactEntity;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 可选联系人列表适配器
 */
public class CheckableContactAdapter extends IndexStickyViewAdapter<CheckableContactEntity> {
    private Context mContext;

    public CheckableContactAdapter(Context context, List<CheckableContactEntity> originalList) {

        super(originalList);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(cn.mobcommu.zim.R.layout.contact_item_index_view, parent, false);
        return new ContactIndexViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(cn.mobcommu.zim.R.layout.checkable_contact_item_view, parent, false);
        return new CreateMultiChatViewHolder(view);
    }

    @Override
    public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

        ContactIndexViewHolder viewHolder = (ContactIndexViewHolder) holder;
        viewHolder.getTextView().setText(indexName);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, CheckableContactEntity itemData) {

        CreateMultiChatViewHolder viewHolder = (CreateMultiChatViewHolder) holder;
        if(itemData.isChecked()) {
            viewHolder.getCheckImageView().setImageResource(cn.mobcommu.zim.R.drawable.vector_checked);
        } else {
            viewHolder.getCheckImageView().setImageResource(cn.mobcommu.zim.R.drawable.vector_uncheck);
        }
        viewHolder.getImageView().setImageResource(cn.mobcommu.zim.R.drawable.vector_contact_focus);
        viewHolder.getTextView().setText(itemData.getRosterEntry().getName());
    }
}
