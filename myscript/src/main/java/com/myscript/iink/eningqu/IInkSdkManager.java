package com.myscript.iink.eningqu;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myscript.iink.Configuration;
import com.myscript.iink.ContentBlock;
import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.Engine;
import com.myscript.iink.IEditorListener2;
import com.myscript.iink.IRenderTarget;
import com.myscript.iink.IRendererListener;
import com.myscript.iink.MimeType;
import com.myscript.iink.ParameterSet;
import com.myscript.iink.PointerType;
import com.myscript.iink.Renderer;
import com.myscript.iink.eningqu.IHwRecognition;
import com.myscript.iink.eningqu.bean.MyScriptRealBean;
import com.myscript.iink.eningqu.bean.MyScriptResultBean;
import com.myscript.iink.eningqu.certificate.MyCertificate;
import com.myscript.iink.uireferenceimplementation.FontMetricsProvider;
import com.myscript.iink.uireferenceimplementation.IRenderView;
import com.myscript.iink.uireferenceimplementation.InputController;
import com.myscript.iink.uireferenceimplementation.SmartGuideView;
import com.myscript.iink2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.myscript.iink.uireferenceimplementation.SmartGuideView.SMART_GUIDE_FADE_OUT_DELAY_OTHER_DEFAULT;
import static com.myscript.iink.uireferenceimplementation.SmartGuideView.SMART_GUIDE_FADE_OUT_DELAY_WRITE_DEFAULT;
import static com.myscript.iink.uireferenceimplementation.SmartGuideView.SMART_GUIDE_FADE_OUT_DELAY_WRITE_IN_DIAGRAM_DEFAULT;
import static com.myscript.iink.uireferenceimplementation.SmartGuideView.SMART_GUIDE_HIGHLIGHT_REMOVAL_DELAY_DEFAULT;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/6/14 16:38
 * desc   :
 * version: 1.0
 */
public class IInkSdkManager {
    public final static String TAG = IInkSdkManager.class.getSimpleName();
    private static IInkSdkManager instance;

    private Engine mEngine;
    private Renderer mRenderer;
    private Editor mEditor;

    private int viewWidth;
    private int viewHeight;

    private Map<String, Typeface> typefaceMap = new HashMap<>();
    private ParameterSet exportParams;
    private int fadeOutWriteInDiagramDelay;
    private int fadeOutWriteDelay;
    private int fadeOutOtherDelay;
    private int removeHighlightDelay;
    private IHwRecognition iHwRecognition;
    ContentPackage mContentPackage;
    ContentPart mContentPart;
    String mPackageName = "File1.iink";
    String curPackageName = "";
    //    String mConfDir;
    String mConfDir2;
    private String recognFile;
    SmartGuideView smartGuideView;

    boolean isCopyRes = false;

    public boolean isCopyRes() {
        return isCopyRes;
    }

    public void setCopyRes(boolean copyRes) {
        isCopyRes = copyRes;
    }

    public interface IInkSdkInitCallback {
        void onSuccess();

        void onFailure();
    }

    public static IInkSdkManager getInstance() {
        if (null == instance) {
            synchronized (IInkSdkManager.class) {
                if (null == instance) {
                    instance = new IInkSdkManager();
                }
            }
        }
        return instance;
    }

    public boolean isInitSuccess() {
        return mEngine != null;
    }

    private Engine getEngine() {
        if (mEngine == null) {
            mEngine = Engine.create(MyCertificate.getBytes());
        }
        return mEngine;
    }

    public void unInit() {
        Log.i(TAG, "IInkSdk unInit ");
        if (null != mContentPackage && null != mContentPart) {
            mContentPackage.removePart(mContentPart);
            mContentPart.close();
            mContentPart = null;
            mContentPackage.close();
            mContentPackage = null;
        }

        if (null != mEditor && !mEditor.isClosed()) {
            mEditor.clear();
            mEditor.close();
            try {
                mEngine.deletePackage(mPackageName);
                mEngine.close();
                mEngine = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void init(@NonNull Context context, IInkSdkInitCallback callback, String appName) {
        Log.i(TAG, "IInkSdk init ");
        mEngine = getEngine();
        // configure recognition
        Configuration conf = mEngine.getConfiguration();
//              mConfDir = "zip://" + context.getPackageCodePath() + "!/assets/conf";
        mConfDir2 = AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf";
        Log.e("mConfDir2",mConfDir2+"");
        conf.setStringArray("configuration-manager.search-path", new String[]{/*mConfDir, */mConfDir2});
        String tempDir = context.getFilesDir().getPath() + File.separator + "tmp";
        conf.setString("content-package.temp-folder", tempDir);
        if (null != callback) {
            callback.onSuccess();
        }
    }

    public void copyRecoRes(final Context context) {
        if (!isCopyRes) {
            //拷贝资源文件
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isCopyRes = true;
                    FileUtils.copyFileOrDir(context.getApplicationContext(), "conf", AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
                    FileUtils.copyFileOrDir(context.getApplicationContext(), "resources", AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
                    isCopyRes = false;
                }
            }).start();
        }

    }

//    public List<File> getLanguages(Context context) {
//        List<File> files = FileUtils.listFilesInDir("file:///android_asset/conf/");
//        List<File> files = FileUtils.listFilesInDir(mConfDir);
//        return files;
//    }

    public void setPackageName(String name) {
        if (!TextUtils.isEmpty(curPackageName)) {
            if (mContentPackage != null) {
                try {
                    mContentPackage.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        curPackageName = name;
    }

    public void setLanguage(Context context, String lang) {
        Engine engine = getEngine();
        try {

            if (null != mContentPackage && null != mContentPart) {
                mContentPackage.removePart(mContentPart);
                mContentPart.close();
                mContentPart = null;
                mContentPackage.close();
                mContentPackage = null;
            }

            if (null != mRenderer && !mRenderer.isClosed()) {
                mRenderer.close();
                mRenderer = null;
            }

            if (null != mEditor && !mEditor.isClosed()) {
                mEditor.clear();
                mEditor.setPart(null);
                mEditor.close();
                mEditor = null;
            }
            Configuration conf = engine.getConfiguration();
            conf.setString("lang", lang);

            initEditor(context.getApplicationContext(), engine);

            recognFile = IInkSdkManager.getInstance().getRecognFile(AppCommon.getHwrFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEditor(@NonNull Context context, @NonNull Engine engine) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mRenderer = engine.createRenderer(displayMetrics.xdpi, displayMetrics.ydpi, iRenderTarget);
        mEditor = engine.createEditor(mRenderer);
        mEditor.setFontMetricsProvider(new FontMetricsProvider(displayMetrics, typefaceMap));
        mEditor.addListener(iEditorListener);
        mEditor.getRenderer().addListener(iRendererListener);

        setConf(context, mEditor, displayMetrics);
        setParams(engine);
        setContentPart(context, engine);

        /*smartGuideView = findViewById(R.id.smart_guide_view);
        smartGuideView.setEditor(mEditor);

        inputController = new InputController(getContext(), this, getEditor());
        setOnTouchListener(inputController);

        // transfer mEditor to render views
        if (renderView != null)
        {
            renderView.setEditor(mEditor);
        }
        else if (layerViews != null)
        {
            for (int i = 0; i < layerViews.length; ++i)
            {
                if (layerViews[i] != null)
                    layerViews[i].setEditor(mEditor);
            }
        }*/
    }

    private void setConf(@NonNull Context context, @NonNull Editor editor, @NonNull DisplayMetrics displayMetrics) {
        Configuration conf = editor.getConfiguration();
        float verticalMarginPX = context.getResources().getDimension(R.dimen.vertical_margin);
        float horizontalMarginPX = context.getResources().getDimension(R.dimen.horizontal_margin);
        float verticalMarginMM = 25.4f * verticalMarginPX / displayMetrics.ydpi;
        float horizontalMarginMM = 25.4f * horizontalMarginPX / displayMetrics.xdpi;
        conf.setNumber("text.margin.top", verticalMarginMM);
        conf.setNumber("text.margin.left", horizontalMarginMM);
        conf.setNumber("text.margin.right", horizontalMarginMM);
        conf.setNumber("text.margin.bottom", verticalMarginMM);

        conf.setNumber("math.margin.top", verticalMarginMM);
        conf.setNumber("math.margin.bottom", verticalMarginMM);
        conf.setNumber("math.margin.left", horizontalMarginMM);
        conf.setNumber("math.margin.right", horizontalMarginMM);
        conf.setBoolean("text.guides.enable", true);

        Configuration configuration = mEditor.getEngine().getConfiguration();
        fadeOutWriteInDiagramDelay = configuration.getNumber("smart-guide.fade-out-delay.write-in-diagram", SMART_GUIDE_FADE_OUT_DELAY_WRITE_IN_DIAGRAM_DEFAULT).intValue();
        fadeOutWriteDelay = configuration.getNumber("smart-guide.fade-out-delay.write", SMART_GUIDE_FADE_OUT_DELAY_WRITE_DEFAULT).intValue();
        fadeOutOtherDelay = configuration.getNumber("smart-guide.fade-out-delay.other", SMART_GUIDE_FADE_OUT_DELAY_OTHER_DEFAULT).intValue();
        removeHighlightDelay = configuration.getNumber("smart-guide.highlight-removal-delay", SMART_GUIDE_HIGHLIGHT_REMOVAL_DELAY_DEFAULT).intValue();
    }

    private void setParams(@NonNull Engine engine) {
        this.exportParams = engine.createParameterSet();
        this.exportParams.setBoolean("export.jiix.strokes", false);
        this.exportParams.setBoolean("export.jiix.bounding-box", false);
        this.exportParams.setBoolean("export.jiix.glyphs", false);
        this.exportParams.setBoolean("export.jiix.primitives", false);
        this.exportParams.setBoolean("export.jiix.chars", false);

    }

    private void setContentPart(@NonNull Context context, @NonNull Engine engine) {
        File file = new File(context.getFilesDir(), mPackageName);
        if (!TextUtils.isEmpty(curPackageName)) {
            file = new File(context.getFilesDir(), curPackageName);
        }
        String contentType = ContentType.Diagram;
        try {
            mContentPackage = engine.openPackage(file);
            Log.w(TAG, "open success");

            Configuration conf = engine.getConfiguration();
            if ("math".equals(conf.getString("lang"))) {
                contentType = ContentType.Math;
            }
            mContentPart = mContentPackage.createPart(contentType);
//            mContentPart = mContentPackage.getPart(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mContentPackage == null) {
            Log.w(TAG, "open failed");
            try {
                mContentPackage = engine.createPackage(file);

//                mContentPart = mContentPackage.createPart("Text");

                Configuration conf = engine.getConfiguration();
                if ("math".equals(conf.getString("lang"))) {
                    contentType = ContentType.Math;
                }
                mContentPart = mContentPackage.createPart(contentType);
                mContentPackage.save();
                Log.w(TAG, "create package save");
            } catch (IOException e) {
                //                e.printStackTrace();
                Log.e(TAG, "Failed to create package");
                if (null != iHwRecognition) {
                    iHwRecognition.onError("Failed to create package");
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Package already opened");
                if (null != iHwRecognition) {
                    iHwRecognition.onError("Package already opened");
                }
            } catch (Exception e) {
                Log.e(TAG, "package create failed");
                if (null != iHwRecognition) {
                    iHwRecognition.onError("Package already opened");
                }
            }
        }

        mEditor.setPart(mContentPart);
    }

    public void setIHwRecognition(IHwRecognition hwRecognition) {
        this.iHwRecognition = hwRecognition;
    }

    IRenderTarget iRenderTarget = new IRenderTarget() {

        @Override
        public void invalidate(Renderer renderer, EnumSet<LayerType> layers) {
            Log.i(TAG, "invalidate 1");
            invalidate(renderer, 0, 0, viewWidth, viewHeight, layers);
        }

        @Override
        public void invalidate(Renderer renderer, int x, int y, int width, int height, EnumSet<LayerType> layers) {
            Log.i(TAG, "invalidate 2");
            if (width <= 0 || height <= 0)
                return;

           /* if (renderView != null)
            {
                renderView.update(renderer, x, y, width, height, layers);
            }
            else if (layerViews != null)
            {
                for (LayerType type : layers)
                {
                    IRenderView layerView = layerViews[type.ordinal()];
                    if (layerView != null)
                        layerView.update(renderer, x, y, width, height, layers);
                }
            }*/
        }
    };

    private IEditorListener2 iEditorListener = new IEditorListener2() {

        @Override
        public void selectionChanged(Editor editor, String[] strings) {

        }

        @Override
        public void activeBlockChanged(Editor editor, String blockId) {
//            ContentBlock activeBlock = editor.getBlockById(blockId);
//
//            L.warn(TAG, "update activeBlockChanged");
//            update(activeBlock);
        }

        @Override
        public void partChanging(Editor editor, ContentPart contentPart, ContentPart contentPart1) {
            Log.i(TAG, "partChanging");
        }

        @Override
        public void partChanged(Editor editor) {
            Log.i(TAG, "partChanged");
        }

        @Override
        public void contentChanged(Editor editor, String[] strings) {
            Log.i(TAG, "contentChanged");
            if (getEditor().isClosed()) {
                return;
            }

            if (getEditor().isClosed()) {
                return;
            }
            ContentBlock rootBlock = getEditor().getRootBlock();
            update(rootBlock);
        }

        @Override
        public void onError(Editor editor, String blockId, String message) {
            Log.i(TAG, "onError");
            if (null != iHwRecognition) {
                iHwRecognition.onError("recognize error blockId=" + blockId + ", message=" + message);
            }
        }
    };

    private void update(ContentBlock rootBlock) {
        String jiixString = "";
        try {
            jiixString = mEditor.export_(rootBlock, MimeType.JIIX);
            Log.i(TAG, "update = " + jiixString);

            //            IInkSdkManager.getInstance().getEditor().clear();
        } catch (Exception e) {
            e.printStackTrace(); // when processing is ongoing, export may fail: ignore
        }
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(jiixString);
            if (jsonObject.has("type")) {
                String type = jsonObject.getString("type");
                if (type.equals("Text")) {
                    MyScriptResultBean result = gson.fromJson(jiixString, MyScriptResultBean.class);
                    if (result != null) {
                        if (null != iHwRecognition) {
                            if (!TextUtils.isEmpty(result.getLabel())) {
                                iHwRecognition.onHwReco(result.getLabel().trim(), null);
                            }
                        }
                        saveRecogn(AppCommon.getHwrFilePath(),
                                recognFile + "\n" + result.getLabel().trim());
                    } else {
                        Log.e(TAG, "Recognize result=" + result + ", iHwRecognition=" + iHwRecognition);
                        if (null != iHwRecognition) {
                            iHwRecognition.onError("recognize failed");
                        }
                    }
                } else if (type.equals("Diagram")) {
                    MyScriptRealBean result = gson.fromJson(jiixString, MyScriptRealBean.class);
                    if (result != null) {
                        List<MyScriptRealBean.ElementsBean> elements = result.getElements();
                        if (null != elements) {
                            if (elements.size() > 0) {
                                try {
                                    Collections.sort(elements);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int count = elements.size();
                                Log.i(TAG, "Elements count=" + count);
                                StringBuffer label = new StringBuffer();
                                for (int i = 0; i < count; ++i) {
                                    if (!TextUtils.isEmpty(elements.get(i).getLabel()))
                                        label.append(elements.get(i).getLabel()).append("\n");
                                    Log.i(TAG, "Elements " + i + ":" + label);
                                }
                                if (null != iHwRecognition) {
                                    if (!TextUtils.isEmpty(label)) {
                                        iHwRecognition.onHwReco(label.toString(), result);
                                    } else {
                                        iHwRecognition.onHwReco("", null);
                                    }
                                }
                                saveRecogn(AppCommon.getHwrFilePath(),
                                        recognFile + "\n" + label.toString());
                            } else {
                                Log.e(TAG, "Elements=0");
                            }
                        } else {
                            Log.e(TAG, "Elements is null");
                            if (null != iHwRecognition) {
                                iHwRecognition.onError("recognize failed");
                            }
                        }
                    } else {
                        Log.e(TAG, "Recognize result=" + result + ", iHwRecognition=" + iHwRecognition);
                        if (null != iHwRecognition) {
                            iHwRecognition.onError("recognize failed");
                        }
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Failed to parse jiix string as json words: " + e.toString());
            if (null != iHwRecognition) {
                iHwRecognition.onError("recognize failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    IRendererListener iRendererListener = new IRendererListener() {

        @Override
        public void viewTransformChanged(Renderer renderer) {
            Log.i(TAG, "viewTransformChanged");
        }
    };

    public Editor getEditor() {
        return mEditor;
    }

    public void editorClean() {
        if (null != mEditor && !mEditor.isClosed()) {
            mEditor.clear();
        }
    }

    public ParameterSet getExportParams() {
        return exportParams;
    }

    public void pointerDown(float x, float y, long t, float f, PointerType pointerType, int pointerId) {
        mEditor.pointerDown(x, y, t, f, pointerType, pointerId);
    }

    public void pointerMove(float x, float y, long t, float f, PointerType pointerType, int pointerId) {
        mEditor.pointerMove(x, y, t, f, pointerType, pointerId);
    }

    public void pointerUp(float x, float y, long t, float f, PointerType pointerType, int pointerId) {
        mEditor.pointerUp(x, y, t, f, pointerType, pointerId);
    }

    public final String export_(ContentBlock block, MimeType mimeType, ParameterSet overrideConfiguration) throws NullPointerException, IllegalArgumentException, IllegalStateException, UnsupportedOperationException, IOException {
        return mEditor.export_(block, mimeType, overrideConfiguration);
    }

    public void saveRecogn(String filePath, String content) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            fos.write(content.getBytes());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public String getRecognFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Reader read = null;
            String content = "";
            BufferedReader br = null;
            try {
                read = new FileReader(file);
                br = new BufferedReader(read);
                StringBuffer sb = new StringBuffer();
                while ((content = br.readLine()) != null) {
                    sb.append(content).append("\n");
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    read.close();
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
