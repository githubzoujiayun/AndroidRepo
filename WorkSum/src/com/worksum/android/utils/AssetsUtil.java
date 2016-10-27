package com.worksum.android.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author chao.qin
 *         <p>
 *         16/10/19
 */
public class AssetsUtil {

    public static String readFile(Context context,String fileName) {
        AssetManager manager = context.getAssets();
        String html = "";
        try {
            InputStream is = manager.open(fileName);
            Scanner scanner = new Scanner(is,"UTF-8");
            while (scanner.hasNext()) {
                html += scanner.nextLine();
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }
}
