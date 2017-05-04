package cn.mobcommu.zim.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import com.bm.library.PhotoView;

/**
 * 点击图片后，放大图片
 */
public class EnlargeImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cn.mobcommu.zim.R.layout.enlarge_image);

        Intent intent = getIntent();
        String image_url = intent.getStringExtra("image_url");
        Bitmap bm = BitmapFactory.decodeFile(image_url);

        // 处理过大图片 "Bitmap too large to be uploaded into a texture"
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // 缩放
        float scaleHt =(float) width/bm.getWidth();
        Bitmap scaled = Bitmap.createScaledBitmap(bm, width,
                (int)(bm.getWidth()*scaleHt), true);


        PhotoView photoView = (PhotoView) findViewById(cn.mobcommu.zim.R.id.photoview_id);
        photoView.setImageBitmap(scaled);
        photoView.enable(); // 开启放缩功能
    }
}
