package com.openclassrooms.realestatemanager.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding;
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProperty#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProperty extends Fragment {
    private static final String TAG = "AddProperty";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private FragmentAddPropertyBinding binding;
    private AmenitiesCheckboxesBinding amenitiesBinding;
    private FormAddressPropertyBinding formAddressBinding;
    private File photoFile;
    private String currentPhotoPath;
    private String FILE_NAME = "photo.jpg";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddProperty() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProperty.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProperty newInstance(String param1, String param2) {
        AddProperty fragment = new AddProperty();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false);
        bindIncludesLayouts();

        setOnAddImageFromCameraListener();
        return binding.getRoot();
    }

    private void setOnAddImageFromCameraListener() {
        binding.addFBtnAddImgCamera.setOnClickListener(view -> takePictureIntent());
    }

    // TODO move this function to activity, getExternalFileDir is accessible only from activity
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG" + timeStamp + "_";
        // In lifecycle, requireContext must by called after OnResume,
        // for to be sur that the fragment is well attached to his host
        File storagePicturesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName,".jpg", storagePicturesDir);
            currentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void bindIncludesLayouts() {
        View amenitiesView = binding.addFAmenities.getRoot();
        View addressFormView = binding.addFFormAddress.getRoot();
        amenitiesBinding = AmenitiesCheckboxesBinding.bind(amenitiesView);
        formAddressBinding = FormAddressPropertyBinding.bind(addressFormView);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager())  != null) {
            photoFile = createImageFile();

            if (photoFile != null) {
                Uri fp = FileProvider.getUriForFile(requireActivity(),"com.openclassrooms.realestatemanager.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fp);

                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e("ERROR", "takePictureIntent: ", e);
                    // TODO display error state to the user
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = null;
            Bitmap imageBitmap = null;
            if (data != null) {
                extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            }
            // TODO save the picture as a image
            if (imageBitmap != null) Log.i(TAG, "onActivityResult: bitmap is ok:: " + imageBitmap.toString());
            else {
                super.onActivityResult(requestCode, resultCode, data);
                Log.i(TAG, "onActivityResult: non image");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        formAddressBinding = null;
        amenitiesBinding = null;
        binding = null;
    }
}