package com.openclassrooms.realestatemanager.util.system;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageFileUtils {


    public static File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG" + timeStamp + "_";
        // In lifecycle, requireContext must by called after OnResume,
        // for to be sur that the fragment is well attached to his host
        File storagePicturesDir = RealEstateManagerApp.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storagePicturesDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
