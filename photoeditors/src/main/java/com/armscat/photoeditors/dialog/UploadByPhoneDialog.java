package com.armscat.photoeditors.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;

import com.armscat.photoeditors.R;


public class UploadByPhoneDialog {

    private AlertDialog alertDialog;
    private Button btn_photo;
//    private Button btn_video;
    private Button photo_gallery;
    private Button cancel;


    public UploadByPhoneDialog(Context context, boolean isShowAlbum) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.upload_by_phone);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        window.setWindowAnimations(R.style.myStyle);  //添加动画

        btn_photo = (Button) window.findViewById(R.id.btn_photo);
        View view = window.findViewById(R.id.view);
        photo_gallery = (Button) window.findViewById(R.id.photo_gallery);
        cancel = (Button) window.findViewById(R.id.cancel);

        if(!isShowAlbum){
            view.setVisibility(View.GONE);
            photo_gallery.setVisibility(View.GONE);
        }
        setListener();

    }
    private void setListener() {

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
    //拍照
    public Button photoByCamera() {

        return btn_photo;
    }

    public Button PassByValue() {

        return photo_gallery;
    }

    public void colseDialog() {
        alertDialog.dismiss();
    }

}
