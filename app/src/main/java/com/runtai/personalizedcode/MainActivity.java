package com.runtai.personalizedcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.runtai.personalizedcode.uitls.MakeQRCodeUtil;
import com.runtai.personalizedcode.uitls.ToolImage;
import com.runtai.personalizedcode.view.ActionSheet;

public class MainActivity extends AppCompatActivity implements OnLongClickListener, ActionSheet.OnSheetItemClickListener {
    //个性二维码图片
    private ImageView qrcode;
    private Bitmap bitmap;
    private static int width, height;
    private ActionSheet actionSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        qrcode = (ImageView) findViewById(R.id.personalized_code);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获得控件的宽高
        calculateView();
        qrcode.setOnLongClickListener(this);
    }

    /**
     * 计算控件的宽高
     */
    private void calculateView() {
        final ViewTreeObserver vto = qrcode.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (vto.isAlive()) {
                    vto.removeOnPreDrawListener(this);
                }
                height = qrcode.getMeasuredHeight();
                width = qrcode.getMeasuredWidth();
                Log.e("height", "" + height);
                Log.e("width", "" + width);
                Bitmap logo = MakeQRCodeUtil.gainBitmap(MainActivity.this, R.drawable.logo1);
                Bitmap background = MakeQRCodeUtil.gainBitmap(MainActivity.this, R.drawable.bg1);
                Bitmap markBMP = MakeQRCodeUtil.gainBitmap(MainActivity.this, R.drawable.water);
                try {
                    //获得二维码图片
                    //这里加上logo后二维码就错了
                    //bitmap = MakeQRCodeUtil.makeQRImage(logo, "http://www.baidu.com", width, height);
                    bitmap = MakeQRCodeUtil.makeQRImage("https://www.baidu.com/", width, height);
                    //给二维码加背景
                    bitmap = MakeQRCodeUtil.addBackground(bitmap, background);
                    //加水印
                    bitmap = MakeQRCodeUtil.composeWatermark(bitmap, markBMP);
                    //设置二维码图片
                    qrcode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

    }

    private void doSomeThing() {
        actionSheet = new ActionSheet(MainActivity.this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(false)
                .addSheetItem("发送给好友", ActionSheet.SheetItemColor.Blue,
                        MainActivity.this)
                .addSheetItem("保存到手机", ActionSheet.SheetItemColor.Blue,
                        MainActivity.this)
                .addSheetItem("收藏", ActionSheet.SheetItemColor.Blue,
                        MainActivity.this);
        actionSheet.show();
    }

    @Override
    public boolean onLongClick(View v) {
        doSomeThing();
        return true;
    }

    /***
     * 判断点击哪个条目做相应的事情
     *
     * @param which
     */
    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                sendToFriends();
                break;
            case 2:
                ToolImage.saveImageToGallery(MainActivity.this, bitmap);
                Toast.makeText(MainActivity.this, "已保存到手机", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(MainActivity.this, "已收藏", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void sendToFriends() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri imageUri = Uri.parse(Environment.getExternalStorageDirectory() + "/code/qrcode.jpg");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

}
