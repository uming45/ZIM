package cn.mobcommu.zim.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 联系人列表Item项ViewHolder
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    @BindView(cn.mobcommu.zim.R.id.contact_avatar)
    ImageView avatar;
    @BindView(cn.mobcommu.zim.R.id.contact_name)
    TextView name;

    public ContactViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public ImageView getImageView() {

        return avatar;
    }

    public TextView getTextView() {

        return name;
    }
}
