package cn.ittiger.im.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bm.library.PhotoView;

import java.io.File;

import cn.ittiger.im.R;

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


        // photoView.setImageURI(Uri.fromFile(new File(image_url)));
        photoView.enable();
    }
}
