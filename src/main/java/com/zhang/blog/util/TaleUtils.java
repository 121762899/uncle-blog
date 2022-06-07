package com.zhang.blog.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.InputStream;

public class TaleUtils {

    /**
     * 判断文件是否是图片类型
     * @param imageFile
     * @return
     */
    public static boolean isImage(InputStream imageFile) {
        try {
            Image img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;

        }
    }

}
