package com.eningqu.aipen.common.utils;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/9 10:39
 * desc   : 拉起App
 * version: 1.0
 */
public class StartAppUtil {
    private static final StartAppUtil ourInstance = new StartAppUtil();

    public static StartAppUtil getInstance() {
        return ourInstance;
    }

    private StartAppUtil() {
    }

    /**
     * 通过包名启动
     *
     * @param context
     * @param pkg     如："com.tencent.mobileqq"
     */
    public void startByPackage(Context context, String pkg) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkg);
        context.startActivity(intent);
    }

    /**
     * 通过包名和类名启动
     *
     * @param context
     * @param pkg       如："com.tencent.mobileqq"
     * @param className 如："com.tencent.mobileqq.activity.SplashActivity"
     */
    public void startByClassName(Context context, String pkg, String className) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName(pkg, className);
        intent.setComponent(comp);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 通过URI启动APP的指定页面
     *
     * @param context
     * @param uri     "csd://com.example.bi/cyn?type=110"
     */
    public void startByUri(Context context, String uri) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(uri));
        intent.putExtra("", "");//这里Intent当然也可传递参数,但是一般情况下都会放到上面的URL中进行传递
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 从谷歌搜索内容
     *
     * @param context
     * @param keyWord "搜索内容"
     */
    public void startGoogleSearcher(Context context, String keyWord) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, keyWord);
        context.startActivity(intent);
    }

    /**
     * 浏览网页
     *
     * @param context
     * @param url     "http://www.google.com"
     */
    public void startWebView(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 启动地图
     *
     * @param context
     * @param longitude
     * @param latitude  "geo:36.899533,66.036476"
     */
    public void startMap(Context context, float longitude, float latitude) {
        Uri uri = Uri.parse("geo:" + longitude + "," + latitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


    /**
     * 规划路径
     *
     * @param context
     * @param orig_longitude
     * @param orig_latitude
     * @param dest_longitude
     * @param dest_latitude
     * @param mode           driving
     */
    public void planningPath(Context context, float orig_longitude, float orig_latitude, float dest_longitude, float dest_latitude, String mode) {
        //https://maps.googleapis.com/maps/api/directions/json?origin=39.99709957757345,116.31184045225382&destination=39.949158391497214,116.4154639095068&sensor=false&mode=driving

        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin=" + orig_longitude + "," + orig_latitude + "&destination=" + dest_longitude + "," + dest_latitude + "&sensor=false&mode=" + mode);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     */
    public void dialPhone(Context context, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phoneNumber
     * @param content
     */
    public void sendSms(Context context, String phoneNumber, String content) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        context.startActivity(intent);
    }

    /**
     * 发送邮件
     * @param context
     * @param emailReceiver
     * @param title
     * @param body
     */
    public void sendEmail(Context context, String[] emailReceiver, String title, String body) {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");
        String emailSubject = title;
        String emailBody = body;
        // 设置邮件默认地址
        email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReceiver);
        // 设置邮件默认标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
        // 设置要默认发送的内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
        // 调用系统的邮件系统
        context.startActivity(Intent.createChooser(email,
                "Please choose an app to email."));
    }

    public void playMedia(Context context, String fileName){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(fileName);
        intent.setDataAndType(uri,"audio/mp3");
        context.startActivity(intent);
    }

  /*
<p>10）设置界面</p>
<pre><code>Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
startActivity(intent);
</code></pre>
<p>11）拍照</p>
<pre><code> // 打开拍照程序
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
startActivityForResult(intent, 1);
</code></pre>
<pre><code> // 取出照片数据
Bundle extras = intent.getExtras();
Bitmap bitmap = (Bitmap) extras.get("data");
</code></pre>
<p>12）选择图片</p>
<pre><code>Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
intent.setType("image/*");
startActivityForResult(intent, 2);
*/

    /**
     * 启动录音机
     *
     * @param context
     */
    public void startSoundRecorder(Context context) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        context.startActivity(intent);
    }

    /**
     * 卸载App
     *
     * @param context
     * @param packageName
     */
    public void uninstallApp(Context context, String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * 安装软件
     *
     * @param context
     * @param fileName
     */
    public void installApp(Context context, String fileName) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(fileName)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 跳转到腾讯应用宝下载软件
     */
    public void goTencentAppMarket(Context context, String packageName) {
        if (exist(context, "com.tencent.android.qqdownloader")) {// 市场存在
            startAppStore(context, packageName, "com.tencent.android.qqdownloader");
        } else {
            Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=" + packageName);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        }
    }

    /**
     * 启动到app详情界面
     */
    public void startAppStore(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断软件是否存在
     */
    public boolean exist(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
