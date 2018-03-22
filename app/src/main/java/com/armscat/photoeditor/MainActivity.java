package com.armscat.photoeditor;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.armscat.photoeditors.Activity.ImageGridActivity;
import com.armscat.photoeditors.Activity.ImagePreviewDelActivity;
import com.armscat.photoeditors.Activity.PermissionsActivity;
import com.armscat.photoeditors.adapter.ImagePickerAdapter;
import com.armscat.photoeditors.bean.ImageItem;
import com.armscat.photoeditors.dialog.UploadByPhoneDialog;
import com.armscat.photoeditors.util.PermissionsChecker;
import com.armscat.photoeditors.util.Utils;
import com.armscat.photoeditors.view.GridSpacingItemDecoration;
import com.armscat.photoeditors.view.ImagePicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.armscat.photoeditors.view.ImagePicker.REQUEST_CODE_PREVIEW;
import static com.armscat.photoeditors.view.ImagePicker.REQUEST_CODE_TAKE;
import static com.armscat.photoeditors.view.ImagePicker.RESULT_CODE_ITEMS;

public class MainActivity extends AppCompatActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener{
//
//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;
    private Unbinder butterKnife;
    private ArrayList<ImageItem> imgArray;
    private ImagePickerAdapter adapter;

    private PermissionsChecker mPermissionsChecker; // 权限检测
    private final int PHOTO_WITH_CAMERA_P = 4;// 拍摄照片权限
    private final int PHOTO_WITH_GALLERY_P = 5;//本地照片权限
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butterKnife = ButterKnife.bind(this);
        mPermissionsChecker = new PermissionsChecker(this);

        setView();

        setAdapter();
    }

    private void setView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, Utils.dp2px(this, 3), false));
    }

    private void setAdapter() {
        if (imgArray == null) {
            imgArray = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new ImagePickerAdapter(this, imgArray, 16);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case -1:
                final UploadByPhoneDialog uploadByPhoneDialog = new UploadByPhoneDialog(MainActivity.this, true);
                Button photo_gallery = uploadByPhoneDialog.PassByValue();
                Button photoByCamera = uploadByPhoneDialog.photoByCamera();
//                    Button videoByCamera = uploadByPhoneDialog.videoByCamera();
                //拍照
                photoByCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadByPhoneDialog.colseDialog();
                        // 缺少权限时, 进入权限配置页面
                        if (mPermissionsChecker.lacksPermissions(REQUEST_PERMISSIONS)) {
                            startPermissionsActivity(PHOTO_WITH_CAMERA_P, REQUEST_PERMISSIONS);
                        } else {
                            //直接拍照
                            takePhoto();
                        }
                    }
                });

                //从图库获取图片
                photo_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadByPhoneDialog.colseDialog();
                        // 缺少权限时, 进入权限配置页面
                        if (mPermissionsChecker.lacksPermissions(REQUEST_PERMISSIONS)) {
                            startPermissionsActivity(PHOTO_WITH_GALLERY_P, REQUEST_PERMISSIONS);
                        } else {
                            getPhotoByGallery();
                        }

                    }
                });

                break;
            default:

                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imgArray);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_DEL, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);

        }
    }

    public void takePhoto() {

        ImagePicker.getInstance().setSelectLimit(16 - imgArray.size());
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_TAKE);
    }

    private void getPhotoByGallery() {

        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(16 - imgArray.size());
        Intent intent1 = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent1, REQUEST_CODE_TAKE);
    }

    //授权
    private void startPermissionsActivity(int code, String... str) {
        PermissionsActivity.startActivityForResult(this, code, str);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_TAKE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {

                    imgArray.addAll(images);
                    adapter.setImages(imgArray);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    imgArray.clear();
                    imgArray.addAll(images);
                    adapter.setImages(imgArray);
                }
            }
        }

        //1000为允许授权,1授权失败
        if (resultCode == 1000) {
            switch (requestCode) {
                case PHOTO_WITH_CAMERA_P:
                    //进行拍照摄像
                    takePhoto();
                    break;
                case PHOTO_WITH_GALLERY_P:
                    //从图库获取
                    getPhotoByGallery();
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (butterKnife != null) {
            butterKnife.unbind();
        }

    }


}
