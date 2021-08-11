package com.eningqu.aipen.qpen;

import android.graphics.Point;
import android.text.TextUtils;

import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.utils.ColorUtil;
import com.eningqu.aipen.common.utils.FileUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/19 17:12
 * desc   :
 * version: 1.0
 */
public class StrokesUtilForQpen {

    public final static String TAG = StrokesUtilForQpen.class.getSimpleName();

    /**
     * 读取整页笔画数据
     *
     * @param file
     * @return
     */
    public static PageStrokesCacheBean getStrokes(File file) {

        L.info(TAG, "getStrokes begin file=" + file);
        if (null == file || !file.exists()) {
            return null;
        }
        PageStrokesCacheBean cacheBean = new PageStrokesCacheBean();

        Reader read = null;
        String content = "";
        BufferedReader br = null;
        try {
            read = new FileReader(file);
            br = new BufferedReader(read);
            int line = 0;

            List<String> contentList = FileUtils.readFile(file, 1, 1);
            if (null != contentList && contentList.size() > 0 && !contentList.get(0).startsWith("USER_ID")) {
                //如果笔记文件没有头信息则从第六行笔记开始读取
                line = 6;
            }
            while ((content = br.readLine()) != null) {

                if (line == 0 && content.startsWith("USER_ID=")) {
                    cacheBean.setUserId(content.replace("USER_ID=", ""));
                } else if (line == 1 && content.startsWith("NOTEBOOK_ID=")) {
                    cacheBean.setNotebookId(content.replace("NOTEBOOK_ID=", ""));
                } else if (line == 2 && content.startsWith("PAGE=")) {
                    String strPage = content.replace("PAGE=", "");
                    strPage = "".equals(strPage) ? "1" : strPage;
                    cacheBean.setPage(Integer.valueOf(strPage));
                } else if (line == 3 && content.startsWith("VER=")) {
                    String str = content.replace("VER=", "");
                    str = "".equals(str) ? "1" : str;
                    cacheBean.setPage(Integer.valueOf(str));
                } else if (line == 4 && content.startsWith("BG=")) {
                    String str = content.replace("BG=", "");
                    str = "".equals(str) ? "1" : str;
                    cacheBean.setBg(str);
                } else if (line > 5 && !"".equals(content)) {
                    //坐标
                    cacheBean.getStrokesBeans().add(getStrokes(content));
                }
                line++;
            }
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
        L.info(TAG, "getStrokes end");
        return cacheBean;
    }

    /**
     * 解析每行笔画数据
     *
     * @param content
     * @return
     */
    private static StrokesBean getStrokes(String content) {
        StrokesBean strokesBean = new StrokesBean();

        String[] items = content.split("#");
        if (null != items && items.length > 3) {
            strokesBean.setCreateTime(Long.valueOf(items[0]));

            String[] dots = items[1].split("/");
            if (null != dots && dots.length > 0) {
                for (String dot : dots) {
                    if (!"".equals(dot.replace(",", ""))) {

                        String[] pts = dot.split(",");
                        Point pt = new Point(Integer.valueOf(pts[0]), Integer.valueOf(pts[1]));
                        strokesBean.addDot(pt);
                    }
                }
            }
            strokesBean.setColor(ColorUtil.hex2Int("#" + items[2]));
            float size = Float.valueOf(items[3]);
            strokesBean.setSizeLevel((int)size);
            strokesBean.setHide(false);
        }
        return strokesBean;
    }


    public static boolean saveStrokes(PageStrokesCacheBean cacheBean, String filePath) {
        if (null == cacheBean || null == cacheBean.getStrokesBeans()) {
            L.error(TAG, "cacheBean=" + cacheBean);
            return false;
        }
        //        L.info(TAG, "to save strokes filePath=" + filePath);
        //        L.info(TAG, "strokes.size=" + cacheBean.getStrokesBeans().size());

        List<StrokesBean> strokesBeans = cacheBean.getStrokesBeans();

        if (cacheBean.getStrokesBeans().size() > 0) {

            File file = new File(filePath);
            boolean noneFileHeader = false;
            long strokeEndTime;
            int index = 0;
            if (file.exists()) {
                //如果文件已存在则找到最后一笔的时间戳和传进来的笔画的时间戳对比，把时间戳后的保存
                //                file.delete();
                String endStroke = "";
                try {
                    endStroke = FileUtils.readLastLine(file, "gbk");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(endStroke) && endStroke != null) {
                    String substring = endStroke.substring(0, endStroke.indexOf("#"));
                    if (!TextUtils.isEmpty(substring)) {
                        strokeEndTime = Long.valueOf(substring);
                        for (int i = 0; i < strokesBeans.size();i++ ) {
                            if (strokeEndTime <= strokesBeans.get(i).getCreateTime()) {
                                index = i;
                                break;
                            }
                        }
                    }
                }
            } else {
                //如果不存在，则创建新的文件
                file = createNewFile(file);
                noneFileHeader = true;

            }
            //        writeFileHeader(file, AppCommon.getUserUID(), cacheBean.getNotebookId(),
            //                    cacheBean.getPage(), cacheBean.getBg());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file, true);
                if (noneFileHeader) {
                    //写文件头信息
                    StringBuilder sbHeader = new StringBuilder();
                    sbHeader.append("USER_ID=").append(AppCommon.getUserUID()).append("\n");
                    sbHeader.append("NOTEBOOK_ID=").append(AppCommon.getCurrentNotebookId()).append("\n");
                    sbHeader.append("PAGE=").append(AppCommon.getCurrentPage()).append("\n");
                    sbHeader.append("VER=").append(1).append("\n");
                    sbHeader.append("BG=").append(cacheBean.getBg()).append("\n");
                    sbHeader.append("#BEGIN#").append("\n");
                    fos.write(sbHeader.toString().getBytes());
                }
                //写笔画坐标
                StringBuilder sb = new StringBuilder();

                if (noneFileHeader) {
                    for (StrokesBean strokes : strokesBeans) {
                        sb.append(strokes.getCreateTime()).append("#");//时间戳
                        for (Point point : strokes.getDots()) {
                            //笔画坐标
                            sb.append(point.x).append(",").append(point.y).append("/");
                        }
                        if (sb.toString().endsWith("/")) {
                            sb.deleteCharAt(sb.lastIndexOf("/"));
                        }
                        sb.append("#");
                        sb.append(ColorUtil.int2Hex(strokes.getColor())).append("#");//颜色
                        sb.append(strokes.getSizeLevel());//粗细
                        sb.append("\n");//换行，一行一笔
                    }
                } else {
                    for (; index < strokesBeans.size(); index++) {
                        StrokesBean strokes = strokesBeans.get(index);
                        sb.append(strokes.getCreateTime()).append("#");//时间戳
                        for (Point point : strokes.getDots()) {
                            //笔画坐标
                            sb.append(point.x).append(",").append(point.y).append("/");
                        }
                        if (sb.toString().endsWith("/")) {
                            sb.deleteCharAt(sb.lastIndexOf("/"));
                        }
                        sb.append("#");
                        sb.append(ColorUtil.int2Hex(strokes.getColor())).append("#");//颜色
                        sb.append(strokes.getSizeLevel());//粗细
                        sb.append("\n");//换行，一行一笔
                    }
                }
                fos.write(sb.toString().getBytes());
            } catch (Exception e) {
                L.error(TAG, e.getMessage());
                return false;
            } finally {
                noneFileHeader = false;
                try {
                    fos.close();
                } catch (IOException e) {
                    L.error(TAG, e.getMessage());
                }
                fos = null;
            }
        } else {
            L.error(TAG, "cacheBean.getStrokesBeans().size()=0");
        }

        return true;
    }

    public static void deleteStrokes(String filePath) {
        File file = new File(filePath);
        Reader read = null;
        String content = "";
        BufferedReader br = null;
        FileOutputStream fos = null;
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath));
            lineNumberReader.skip(Long.MAX_VALUE);
            //注意加1，实际上是读取换行符，所以需要+1
            int lineNumber = lineNumberReader.getLineNumber() + 1;
            read = new FileReader(file);
            br = new BufferedReader(read);
            List<String> contentList = FileUtils.readFile(file, 1, lineNumber - 2);

            if (contentList.size() <= 5) {
                return;
            }
            AppCommon.cleanPageData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
            file.createNewFile();

            fos = new FileOutputStream(file, true);
            StringBuilder sb = new StringBuilder();
            for (String s : contentList) {
                sb = new StringBuilder();
                sb.append(s).append("\n");
                fos.write(sb.toString().getBytes());
            }

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

    /**
     * 保存新的笔画
     *
     * @param strokesBean
     * @param filePath
     * @return
     */
    public static boolean saveNewStrokes(final StrokesBean strokesBean, String filePath, String label) {
        File file = new File(filePath);
        if (!file.exists()) {
            FileUtils.createNewFile(file);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);

            List<String> contentList = FileUtils.readFile(file, 1, 1);

            if (!file.exists()) {
                //如果不存在，则创建新的文件
                createNewFile(file);
                //写文件头信息
                StringBuilder sbHeader = new StringBuilder();
                sbHeader.append("USER_ID=").append(AppCommon.getUserUID()).append("\n");
                sbHeader.append("NOTEBOOK_ID=").append(AppCommon.getCurrentNotebookId()).append("\n");
                sbHeader.append("PAGE=").append(AppCommon.getCurrentPage()).append("\n");
                sbHeader.append("VER=").append(1).append("\n");
                sbHeader.append("BG=").append(1).append("\n");
                sbHeader.append("#BEGIN#").append("\n");
                fos.write(sbHeader.toString().getBytes());
                AppCommon.updatePage(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.getCurrentNoteType(), label);
            } else if (null == contentList || contentList.size() == 0 ||
                    !contentList.get(0).startsWith("USER_ID")) {
                //如果存在文件，但是无头信息
                //写文件头信息
                StringBuilder sbHeader = new StringBuilder();
                sbHeader.append("USER_ID=").append(AppCommon.getUserUID()).append("\n");
                sbHeader.append("NOTEBOOK_ID=").append(AppCommon.getCurrentNotebookId()).append("\n");
                sbHeader.append("PAGE=").append(AppCommon.getCurrentPage()).append("\n");
                sbHeader.append("VER=").append(1).append("\n");
                sbHeader.append("BG=").append(1).append("\n");
                sbHeader.append("#BEGIN#").append("\n");
                fos.write(sbHeader.toString().getBytes());
                AppCommon.updatePage(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.getCurrentNoteType(), label);
            }

            //写笔画坐标
            StringBuilder sb = new StringBuilder();
            sb.append(strokesBean.getCreateTime()).append("#");//时间戳
            for (Point point : strokesBean.getDots()) {
                //笔画坐标
                sb.append(point.x).append(",").append(point.y).append("/");
            }
            if (sb.toString().endsWith("/")) {
                sb.deleteCharAt(sb.lastIndexOf("/"));
            }
            sb.append("#");
            sb.append(ColorUtil.int2Hex(strokesBean.getColor())).append("#");//颜色
            sb.append(strokesBean.getSizeLevel());//粗细
            sb.append("\n");//换行，一行一笔
            fos.write(sb.toString().getBytes());
        } catch (Exception e) {
            L.error(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                L.error(TAG, e.getMessage());
            }
        }

        return true;
    }

    /**
     * 写文件头
     *
     * @param file
     * @param userId
     * @param notebookId
     * @param page
     * @return
     */
    private static boolean writeFileHeader(File file, String userId, String notebookId, int page, String bg) {
        if (file == null || TextUtils.isEmpty(userId) ||
                TextUtils.isEmpty(notebookId)) {
            return false;
        }
        if (!file.exists()) {
            file = createNewFile(file);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            StringBuilder sb = new StringBuilder();
            sb.append("USER_ID=").append(userId).append("\n");
            sb.append("NOTEBOOK_ID=").append(notebookId).append("\n");
            sb.append("PAGE=").append(page).append("\n");
            sb.append("VER=").append(1).append("\n");
            sb.append("BG=").append(bg).append("\n");
            sb.append("#BEGIN#").append(bg).append("\n");
            fos.write(sb.toString().getBytes());
        } catch (Exception e) {
            L.error(e.getMessage());
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                L.error(TAG, e.getMessage());
            }
            fos = null;
        }

        return true;
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     */
    public static File createNewFile(File file) {
        try {
            if (file.exists()) {
                return file;
            }

            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            L.error(TAG, e.getMessage());
            return null;
        }
        return file;
    }

    /**
     * 读取文件内容 从文件中一行一行的读取文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        Reader read = null;
        String content = "";
        String result = "";
        BufferedReader br = null;
        try {
            read = new FileReader(file);
            br = new BufferedReader(read);
            while ((content = br.readLine().toString().trim()) != null) {
                result += content + "\r\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                read.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
                L.error(TAG, e.getMessage());
            }
        }
        return result;
    }
}
