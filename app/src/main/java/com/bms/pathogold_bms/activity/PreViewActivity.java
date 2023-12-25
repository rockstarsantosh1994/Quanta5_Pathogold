package com.bms.pathogold_bms.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bms.pathogold_bms.R;
import com.bumptech.glide.Glide;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PreViewActivity extends AppCompatActivity {

   // @BindView(R.id.ivImageView)
    public ImageView ivPreviewImage;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        //ButterKnife.bind(this);
        ivPreviewImage = findViewById(R.id.ivImageView);
        Toolbar toolbar=findViewById(R.id.toolbar_preview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.view_image));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        if(!getIntent().getStringExtra("img_src").isEmpty()){
            Glide.with(this).load(getIntent().getStringExtra("img_src")).into(ivPreviewImage);
        }

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(ivPreviewImage);
        mAttacher.update();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                 break;
        }
        return super.onOptionsItemSelected(item);
    }
}
