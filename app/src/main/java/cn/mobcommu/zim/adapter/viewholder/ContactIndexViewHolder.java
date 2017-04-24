package cn.mobcommu.zim.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ContactIndexViewHolder extends RecyclerView.ViewHolder {
    @BindView(cn.mobcommu.zim.R.id.contact_index_name)
    TextView mTextView;

    public ContactIndexViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getTextView() {

        return mTextView;
    }
}
