package com.fclassroom.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fclassroom.appstudentclient.R;

/**
 * Created by Administrator on 2015/4/30 0030.
 */
public class DelImage extends RelativeLayout {

    public ImageView MainView;
    public ImageView DelView;

    public DelImage(Context context) {
        this(context, null);
    }

    public DelImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.del_imageview,this);
        MainView = (ImageView)findViewById(R.id.image_main);
        DelView = (ImageView)findViewById(R.id.image_del);
    }

    public void setImageBitmap(Bitmap bitmap) {
        MainView.setImageBitmap(bitmap);
    }
}
