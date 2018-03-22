package com.armscat.photoeditors.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


import com.armscat.photoeditors.R;
import com.armscat.photoeditors.bean.FileCache;
import com.armscat.photoeditors.bean.ImageItem;
import com.armscat.photoeditors.dialog.RemindDialag;
import com.armscat.photoeditors.util.NavigationBarChangeListener;
import com.armscat.photoeditors.util.ScannerUtils;
import com.armscat.photoeditors.view.ImagePicker;

import java.io.File;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧），ikkong （ikkong@163.com）
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：预览已经选择的图片，并可以删除, 感谢 ikkong 的提交
 * ================================================
 */
public class ImagePreviewDelActivity extends ImagePreviewBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView mBtnDel = (ImageView) findViewById(R.id.btn_del);
        Button btnSave = (Button) findViewById(R.id.btn_save);

        mBtnDel.setOnClickListener(this);

        topBar.findViewById(R.id.btn_back).setOnClickListener(this);

        mTitleCount.setText(getString(R.string.ip_preview_image_count, mCurrentPosition + 1, mSelectImageItems.size()));
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mTitleCount.setText(getString(R.string.ip_preview_image_count, mCurrentPosition + 1, mSelectImageItems.size()));
            }
        });
        NavigationBarChangeListener.with(this, NavigationBarChangeListener.ORIENTATION_HORIZONTAL)
                .setListener(new NavigationBarChangeListener.OnSoftInputStateChangeListener() {
                    @Override
                    public void onNavigationBarShow(int orientation, int height) {
                        topBar.setPadding(0, 0, height, 0);
                    }

                    @Override
                    public void onNavigationBarHide(int orientation) {
                        topBar.setPadding(0, 0, 0, 0);
                    }
                });


        if (isShowDel) {
            mBtnDel.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
        } else {
            mBtnDel.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.VISIBLE);
        }

        setListener(btnSave);
    }

    private void setListener(Button btnSave) {
        //保存
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final RemindDialag dialag = new RemindDialag(ImagePreviewDelActivity.this, R.style.loading_dialog, "提示", "保存到手机", true, true, "取消", "确定");
                dialag.Ok().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ImageItem obj = mSelectImageItems.get(mCurrentPosition);
//                                    Bitmap bitmap = ImgLoaderManager.getBitmapUrl(obj.getImagePath());
                                if (obj.getImageId() != null) {
                                    String path = new FileCache(ImagePreviewDelActivity.this).getLibraryPath(obj.getImageId());
                                    if (path != null) {
                                        ScannerUtils.saveImageToGallery(ImagePreviewDelActivity.this, new File(path), ScannerUtils.ScannerType.RECEIVER);
                                    }

                                }

                            }
                        }).start();
                        dialag.Dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_del) {
            showDeleteDialog();
        } else if (id == R.id.btn_back) {
            onBackPressed();
        }
    }

    /**
     * 是否删除此张图片
     */
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("要删除这张照片吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //移除当前图片刷新界面
                mSelectImageItems.remove(mCurrentPosition);
                if (mSelectImageItems.size() > 0) {
                    mAdapter.setData(mSelectImageItems);
                    mAdapter.notifyDataSetChanged();
                    mTitleCount.setText(getString(R.string.ip_preview_image_count, mCurrentPosition + 1, mSelectImageItems.size()));
                } else {
                    onBackPressed();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //带回最新数据
        intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, mSelectImageItems);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            topBar.setVisibility(View.GONE);
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            topBar.setVisibility(View.VISIBLE);
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}