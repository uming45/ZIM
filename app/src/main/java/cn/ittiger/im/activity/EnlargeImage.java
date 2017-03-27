package cn.ittiger.im.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bm.library.PhotoView;

import cn.ittiger.im.R;

/**
 * 点击图片后，放大图片
 */
public class EnlargeImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enlarge_image);

        Intent intent = getIntent();
        String image_url = intent.getStringExtra("image_url");
        Bitmap bm = BitmapFactory.decodeFile(image_url);

        PhotoView photoView = (PhotoView) findViewById(R.id.photoview_id);
        photoView.setImageBitmap(bm);
        photoView.enable(); // 开启放缩功能
    }
}
