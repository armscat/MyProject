package com.armscat.photoeditors.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.armscat.photoeditors.R;


public class RemindDialag extends Dialog {

    private  Button ok;
    private  Button cancel;
     Activity context;
    private  String titleStr;
    private  String title_subStr;
    private  Boolean isshowcancle;
    private  Boolean isshowok;
    private  String cancleStr;
    private  String okStr;

    public RemindDialag(Activity context) {
        super(context);
        this.context = context;
    }
    public RemindDialag(Activity context, int theme, String titleStr, String title_subStr, boolean isshowcancle, boolean isshowok, String cancleStr, String okStr) {
        super(context, theme);
        this.context = context;
        this.titleStr = titleStr;
        this.title_subStr = title_subStr;
        this.isshowcancle = isshowcancle;
        this.isshowok = isshowok;
        this.cancleStr = cancleStr;
        this.okStr = okStr;
        try {
            this.show();
            this.setCancelable(true);
            this.setCanceledOnTouchOutside(false);
        }catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.remind_dialog_item);

        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window window = this.getWindow();
        WindowManager m = context.getWindowManager();

        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
//        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        p.width = (int) (dm.widthPixels* 0.8);// 宽度设置为屏幕的0.8
        Display d = m.getDefaultDisplay();
        p.width = (int) (d.getWidth() * 0.8);
        window.setAttributes(p);

        TextView title = (TextView) window.findViewById(R.id.tv_title);
        TextView title_sub = (TextView) window.findViewById(R.id.tv_title_sub);

        ok = (Button) window.findViewById(R.id.btn_ok);
        cancel = (Button) window.findViewById(R.id.btn_cancel);
        View view = (View) window.findViewById(R.id.view);

        title.setText(titleStr);
        title_sub.setText(title_subStr);
        //取消按钮
        if (isshowcancle) {
            cancel.setVisibility(View.VISIBLE);
            cancel.setText(cancleStr);
        } else {
            cancel.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        //确认按钮
        if (isshowok){
            ok.setVisibility(View.VISIBLE);
            ok.setText(okStr);
        }else {
            ok.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setCancelable(true);
    }
    public  Button Ok() {
        return ok;
    }
    public  Button Cancel() {
        return cancel;
    }
    public void Dismiss() {
        dismiss();
    }
}
