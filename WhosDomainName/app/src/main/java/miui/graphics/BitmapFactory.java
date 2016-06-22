package miui.graphics;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liuchonghui on 16/6/22.
 */
public class BitmapFactory {
    public static boolean saveToFile(Bitmap bitmap, String path) throws IOException {
        return saveToFile(bitmap, path, false);
    }

    public static boolean saveToFile(Bitmap bitmap, String path, boolean saveToPng) throws IOException {
        if (bitmap != null) {
            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(path);
                bitmap.compress(saveToPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }

            }

            return true;
        } else {
            return false;
        }
    }
}
