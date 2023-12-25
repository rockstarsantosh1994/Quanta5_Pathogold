package com.bms.pathogold_bms.utility;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileBase64 {
    /**
     * encodeBase64File: (transforms the file into a base64 string).
     * @param path file path
     * @return
     * @throws Exception
     */

    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer,Base64.DEFAULT);
    }
    /**
     * decoderBase64File: (decode base64 characters to save the file).
     * @param base64Code encoded string
     * @param savePath file save path
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code,String savePath) throws Exception {
        byte[] buffer =Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }
}
