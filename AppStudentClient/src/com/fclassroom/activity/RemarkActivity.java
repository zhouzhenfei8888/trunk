package com.fclassroom.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.common.FileUtils;
import com.fclassroom.app.common.ImageUtils;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.StringUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.common.ZipUtils;
import com.fclassroom.app.widget.DelImage;
import com.fclassroom.app.widget.ZoomImageView;
import com.fclassroom.appstudentclient.R;

import android.widget.LinearLayout.LayoutParams;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 备注页
 */
public class RemarkActivity extends BaseActivity {

    private TextView cancle;
    private TextView sure;
    private EditText editText;
    private LinearLayout linearLayout, linearTakePhoto;
    private ImageView iv_photo;
    private String protraitPath;
    private Uri origUri;
    private Uri cropUri;
    private final static int CROP = 100;
    private File protraitFile;
    private File savedirFile;
    private File UserFile;
    private Bitmap protraitBitmap;
    private RelativeLayout delphoto1, delphoto2, delphoto3;
    ImageView iv1, iv2, iv3;
    private final static String JIKE = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/JIKE/";
    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/JIKE/PHOTO/";
    ImageView imageView;
    AppContext appContext;
    String accessToken;
    int gradeId;
    int subjectId;
    protected PopupWindow answerPopup;
    protected ZoomImageView answerImage;
    int examQuestionId, studentId, schoolId;
    String savedirstr;
    File outZip;
    int i = 0;
    ImageView[] imageViews, imagedels;
    RelativeLayout[] relativeLayouts;
    ImageView imageView1, imageView2, imageView3;
    ImageView imagedel1, imagedel2, imagedel3;
    DelImage[] delImages;
    LinearLayout linears;
    StringBuilder stringBuilderImagePath = new StringBuilder();
    List<HashMap<Integer, ImageView>> list = new ArrayList<>();
    private String deleteFile;
    private String translatepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        studentId = PreferenceUtils.getInt(appContext, PreferenceUtils.STUDENT_ID);
        schoolId = PreferenceUtils.getInt(appContext, PreferenceUtils.SCHOOL_ID);
        initViews();
    }

    private void initViews() {
        String remark = "" + (String) getIntent().getSerializableExtra("value");
        examQuestionId = (int) getIntent().getSerializableExtra("value2");
        cancle = (TextView) findViewById(R.id.tv_cancle);
        sure = (TextView) findViewById(R.id.tv_sure);
        editText = (EditText) findViewById(R.id.editText);
        linearTakePhoto = (LinearLayout) findViewById(R.id.linear_takephoto);
        delImages = new DelImage[3];
        imagedels = new ImageView[3];
        linears = (LinearLayout) findViewById(R.id.linears);
        imageView = (ImageView) findViewById(R.id.imageview_takephoto);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        editText.setHint("这里添加备注...");
        if (remark != "") {
            editText.setText(remark);
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  int count = linears.getChildCount();
                StringBuilder stringBuilder = new StringBuilder("");
                if (count != 0) {
                    for (int i = 0; i < count; i++) {
                        String address = (String) ((RelativeLayout) linears.getChildAt(i + 1)).getTag();
                        stringBuilder.append(address);
                    }
//                    System.out.println(stringBuilder.toString()+"yyyy");
                    saveRemarkImages(stringBuilder.toString());
                }*/
                String editRemark = editText.getText().toString();
                String imagePaths = stringBuilderImagePath.toString();
                EditRemark(accessToken, examQuestionId, editRemark);
                Intent intent = new Intent();
                intent.putExtra("remark", editRemark);
                intent.putExtra("imagePaths", imagePaths);
                setResult(5, intent);
                AppManager.getAppManager().finishActivity();
            }
        });
        linearLayout = (LinearLayout) findViewById(R.id.linear_img);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
//        iv_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView = new ImageView(RemarkActivity.this);
//                imageView.setPadding(20, 20, 20, 20);
//                startActionCamera();
//                linearLayout.addView(imageView);
////                imageView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////
////                        if (answerPopup == null) {
////                            View popupView = getLayoutInflater().inflate(
////                                    R.layout.item_answer_popup,
////                                    null);
////                            ImageView back = (ImageView) popupView.findViewById(R.id.iv_back);
////                            ImageView delete = (ImageView) popupView.findViewById(R.id.iv_delete);
////                            back.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View v) {
////                                    answerPopup.dismiss();
////                                }
////                            });
////                            delete.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View view) {
////                                    imageView.setVisibility(View.GONE);
////                                    answerPopup.dismiss();
////                                }
////                            });
////                            answerPopup = new PopupWindow(popupView,
////                                    LayoutParams.MATCH_PARENT,
////                                    LayoutParams.MATCH_PARENT, true);
////                            answerPopup.setTouchable(true);
////                            answerPopup.setOutsideTouchable(true);
////                            answerPopup
////                                    .setBackgroundDrawable(new BitmapDrawable(
////                                            getResources(),
////                                            (Bitmap) null));
////                            answerImage = ((ZoomImageView) popupView
////                                    .findViewById(R.id.zoom_imageview));
////                            answerImage
////                                    .setTapListener(new ZoomImageView.OnTapListener() {
////
////                                        @Override
////                                        public void onTap(MotionEvent e) {
////                                            answerPopup.dismiss();
////                                        }
////                                    });
////                        }
//////                        answerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
////                        answerImage.setImageBitmap(BitmapFactory.decodeFile(v.getTag().toString()));
////                        answerPopup.showAtLocation(getWindow().getDecorView(),
////                                Gravity.CENTER, 0, 0);
////
////                    }
////                });
//            }
//        });
        linearTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemarkActivity.this);
                builder.setTitle("");
                builder.setItems(new String[]{"拍照", "从相册中选择"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            startActionCamera();
                        } else if (which == 1) {
                            startImagePick();
                        }
                    }
                });
                builder.create().show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemarkActivity.this);
                builder.setTitle("");
                builder.setItems(new String[]{"拍照", "从相册中选择"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            startActionCamera();
                        } else if (which == 1) {
                            startImagePick();
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    private void EditRemark(final String accessToken, final int examQuestionId, final String editRemark) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.ToastMessage(RemarkActivity.this, "添加remark成功！");
                } else {
                    UIHelper.ToastMessage(RemarkActivity.this, "添加remark失败！");
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> baseResponseBean = appContext.EditRemark(accessToken, examQuestionId, editRemark);
                    msg.what = 1;
                    msg.obj = baseResponseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 选择图片裁剪
     *
     * @param
     */
    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"),
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    /**
     * 相机拍照
     *
     * @param
     */
    private void startActionCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    // 拍照保存的绝对路径
    private Uri getCameraTempFile() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            UIHelper.ToastMessage(RemarkActivity.this, "无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // 照片命名
        String cropFileName = "camera_" + timeStamp + ".jpg";
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);
        cropUri = Uri.fromFile(protraitFile);
        this.origUri = this.cropUri;
        return this.cropUri;
    }

    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            UIHelper.ToastMessage(RemarkActivity.this, "无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String year = new SimpleDateFormat("yyyy")
                .format(new Date());
        String month = new SimpleDateFormat("MM")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(RemarkActivity.this, uri);
        }
        String ext = FileUtils.getFileFormat(thePath);
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = timeStamp + "_200x200" + "." + ext;
        // 裁剪头像的绝对路径
        savedirstr = FILE_SAVEPATH + schoolId + "/" + year + "/" + month + "/" + studentId + "/" + examQuestionId + "/";
        translatepath = schoolId + "/" + year + "/" + month + "/" + studentId + "/" + examQuestionId + "/" + cropFileName;
        protraitPath = savedirstr + cropFileName;
        stringBuilderImagePath.append(" " + protraitPath);
//        protraitFile = new File(protraitPath);
        File savedir = new File(savedirstr);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        savedirFile = new File(savedir, cropFileName);
        cropUri = Uri.fromFile(savedirFile);
        return this.cropUri;
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     * @param
     */
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri);// 拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                startActionCrop(data.getData());// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                setImageView();
                if (i < 3) {
                    uploadNewPhoto();// 上传新照片,判断是否是三张内
                    saveRemarkImages(translatepath);
                }
                break;
        }
    }

    private void saveRemarkImages(final String protraitPath) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.ToastMessage(RemarkActivity.this, "路径保存成功");
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(RemarkActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.saveRemarkImages(accessToken, examQuestionId, protraitPath);
                    msg.what = 1;
                    msg.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void setImageView() {
        // 获取头像缩略图
        if (!StringUtils.isEmpty(savedirstr) && savedirFile.exists()) {
            protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
                    200, 200);
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new Date());
            File file = new File(FILE_SAVEPATH + schoolId + "/");
            List<File> files = new ArrayList<File>();
            files.add(file);
            outZip = new File(FILE_SAVEPATH + "studentImages_" + accessToken + "_" + schoolId + "_" + examQuestionId + "_" + timeStamp + ".zip");
            try {
                ZipUtils.zipFiles(files, outZip);
            } catch (IOException e) {
                e.printStackTrace();
            }
            linearTakePhoto.setBackgroundResource(R.drawable.remark_white);
            linearTakePhoto.setEnabled(false);
            imageView.setVisibility(View.VISIBLE);
            imageView.setEnabled(true);
            if (i >= 3) {
                UIHelper.ToastMessage(RemarkActivity.this, "最多只能上传三张图片");
            } else {
                System.out.println("ok1");
                delImages[i] = new DelImage(RemarkActivity.this);
                delImages[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imagedels[i] = (ImageView) delImages[i].findViewById(R.id.image_del);
                imagedels[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linears.removeView(delImages[i - 1]);
                        i--;
                    }
                });
                delImages[i].setImageBitmap(protraitBitmap);
                delImages[i].setTag(translatepath);
                linears.addView(delImages[i]);
                i++;
            }
        } else {
            UIHelper.ToastMessage(RemarkActivity.this, "图像不存在！");
        }
    }

    /**
     * 上传新照片
     */
    private void uploadNewPhoto() {
        final ProgressDialog loading = new ProgressDialog(RemarkActivity.this);
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    // 提示信息
                    UIHelper.ToastMessage(RemarkActivity.this, "上传成功");
                    if (savedirFile.exists()) {
                        savedirFile.delete();
                    }
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(RemarkActivity.this);
                }
            }
        };

        new Thread() {
            public void run() {
                // 获取头像缩略图
                if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
                    protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
                            200, 200);
                }
                if (protraitBitmap != null) {
                    Message msg = new Message();
                    try {
                        BaseResponseBean<String> res = appContext
                                .updatePortrait(accessToken, outZip);
                        msg.what = 1;
                        msg.obj = res;
                    } catch (AppException e) {
                        UIHelper.ToastMessage(RemarkActivity.this, "上传出错·");
                        msg.what = -1;
                        msg.obj = e;
                    }
                    handler.sendMessage(msg);
                } else {
                    UIHelper.ToastMessage(RemarkActivity.this, "图像不存在，上传失败·");
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
