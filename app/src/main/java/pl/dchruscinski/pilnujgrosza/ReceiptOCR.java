package pl.dchruscinski.pilnujgrosza;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiptOCR {
    TessBaseAPI tesseract = null;
    Context context;

    public ReceiptOCR(Context context) {
        this.context = context;
    }

    public void initAPI() {
        tesseract = new TessBaseAPI();
        String externalFilesDirPath = context.getExternalFilesDir(null).getAbsolutePath();
        copyAssets();
        tesseract.init(externalFilesDirPath, "pol");
    }

    public String recognizeReceipt(String pathToImage) {
        if (tesseract == null) {
            initAPI();
        }

        Bitmap processedImage = BitmapFactory.decodeFile(pathToImage);
        tesseract.setImage(processedImage);

        System.out.println(tesseract.getUTF8Text());

        return tesseract.getUTF8Text();
    }

    private void copyAssets() {
        String tessdataPath = context.getExternalFilesDir(null) + "/tessdata/";
        AssetManager assetManager = context.getAssets();
        String[] trainedDataFiles = null;

        try {
            trainedDataFiles = assetManager.list("traineddata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (trainedDataFiles != null) {
            for (String filename : trainedDataFiles) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    File folder = new File(tessdataPath);
                    if (!folder.exists()) folder.mkdir();

                    in = assetManager.open("traineddata/" + filename);
                    File outFile = new File(tessdataPath, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);

                    System.out.println("Asset file: " + filename + "has been copied to: " + tessdataPath);
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
