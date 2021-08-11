package com.eningqu.aipen.common.utils;

import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.view.View;

import com.eningqu.aipen.qpen.SignatureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/8 21:43
 * desc   :
 * version: 1.0
 */
public class PdfUtil {

    public static void pdfModel(SignatureView view, int width, int height, int pageNum, String filePath) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageNum).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        view.onMyDraw(page.getCanvas());
        document.finishPage(page);
        File file = new File(filePath);
        try {
            if (null != file && file.exists()) {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            document.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }

    public static void pdfModel(View view, int width, int height, int pageNum, String filePath) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageNum).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        view.draw(page.getCanvas());
        document.finishPage(page);
        File file = new File(filePath);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            document.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }
}
