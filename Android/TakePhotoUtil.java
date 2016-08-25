

import java.io.File;

import com.ngo.application.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class TakePhotoUtil {
    private Activity activity;
    private ImageView imageView;
    private PopupWindow popupwindow;
    private File mOutputFile;
    private Bitmap bm;

    public static final int REQUEST_CODE_TAKE_PHOTO = 0;

    public static final int REQUEST_CODE_GOTO_PHOTO = 3;

    public static final int REQUEST_CODE_CLIP_PHOTO = 1;

    public TakePhotoUtil(Activity activity, ImageView view) {
        this.activity = activity;
        imageView = view;
    }

    public PopupWindow getPopWindow() {
        return popupwindow;
    }

    public void initmPopupWindowView() {

        View customView = null;
        // 获取自定义布局文件pop.xml的视图
        customView = activity.getLayoutInflater().inflate(R.layout.popview_item, null, false);
        popupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集
        popupwindow.setFocusable(true);
        // 设置允许在外点击消失
        popupwindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupwindow.setBackgroundDrawable(new BitmapDrawable());

        /** 在这里可以实现自定义视图的功能 */
        Button btton1 = (Button) customView.findViewById(R.id.popview_button1);
        Button btton2 = (Button) customView.findViewById(R.id.popview_button2);
        Button btton3 = (Button) customView.findViewById(R.id.popview_button3);

        btton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (hasCarema()) {
                    takePhoto();
                }
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                }
            }
        });

        btton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                goToPhoto();
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                }
            }
        });

        btton3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                }
            }
        });

    }

    /**
     * 判断是否有相机
     */
    private boolean hasCarema() {
        PackageManager pm = activity.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            ToastUtils.showToast(activity, "未检测到照相机");
            return false;
        }
        return true;
    }

    /***
     * 调用相机
     * 
     */
    private void takePhoto() {
        // 路径
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 文件全路径
        mOutputFile = new File(sdPath, System.currentTimeMillis() + ".jpg");
        // 转换成url
        Uri uri = Uri.fromFile(mOutputFile);
        // 调用拍照功能
        Intent newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        newIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(newIntent, REQUEST_CODE_TAKE_PHOTO);
    }

    private void goToPhoto() {
        // 路径
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 文件全路径
        mOutputFile = new File(sdPath, System.currentTimeMillis() + ".jpg");
        // Logger.d(mOutputFile.getAbsolutePath());
        // 转换成url
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setType("image/*");
        // Action:选择数据然后返回
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, REQUEST_CODE_GOTO_PHOTO);
    }

    public void onTakePhotoFinished(int resultCode, Intent data) {
        // 根据返回码判断拍照结果
        if (resultCode == activity.RESULT_CANCELED) {// 返回
            ToastUtils.showToast(activity, "取消拍照");
            return;
        } else if (resultCode != activity.RESULT_OK) {// 拍照失败
            ToastUtils.showToast(activity, "拍照失败");
        } else {// 拍照成功
            clipPhoto(Uri.fromFile(mOutputFile));
        }
    }

    public void onGotoPhotoFinished(int resultCode, Intent data) {
        // 根据返回码判断拍照结果
        if (resultCode == activity.RESULT_CANCELED) {// 返回
            ToastUtils.showToast(activity, "取消获取图片");
            return;
        } else if (resultCode != activity.RESULT_OK) {// 拍照失败
            ToastUtils.showToast(activity, "获取图片失败");
        } else {// 拍照成功
            File file = new File(GetFilePathUtils.getPath(activity, data.getData()));
            clipPhoto(Uri.fromFile(file));
        }
    }

    private void clipPhoto(Uri uri) {
        File f = new File(mOutputFile.getAbsolutePath());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        // intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        // intent.putExtra("aspectX", 1);
        // intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        // intent.putExtra("outputX", 900);
        // intent.putExtra("outputY", 900);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        activity.startActivityForResult(intent, REQUEST_CODE_CLIP_PHOTO);
    }

    public void onClipPhotoFinished(int resultCode, Intent data) {
        if (resultCode == activity.RESULT_CANCELED) {// 返回
            ToastUtils.showToast(activity, "clip photo canceled");
            return;
        } else if (resultCode != activity.RESULT_OK) {// 剪裁失败返回原图
            ToastUtils.showToast(activity, "take photo failed");
        }
        recycle();
        bm = BitmapFactory.decodeFile(mOutputFile.getAbsolutePath());
        imageView.setImageBitmap(bm);
    }

    private void recycle() {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            System.gc();
            bm = null;
        }
    }

    public String getFilePath() {
        return mOutputFile.getAbsolutePath();
    }

}
