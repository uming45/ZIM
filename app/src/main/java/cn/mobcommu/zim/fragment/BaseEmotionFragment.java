package cn.mobcommu.zim.fragment;

import cn.mobcommu.zim.constant.EmotionType;

import android.support.v4.app.Fragment;
import android.widget.EditText;

public class BaseEmotionFragment extends Fragment {
    protected EmotionType mEmotionType;
    protected EditText mEditText;

    public BaseEmotionFragment() {

    }

    public void setEmotionType(EmotionType emotionType) {

        mEmotionType = emotionType;
    }

    public void bindToEditText(EditText editText) {

        mEditText = editText;
    }
}
