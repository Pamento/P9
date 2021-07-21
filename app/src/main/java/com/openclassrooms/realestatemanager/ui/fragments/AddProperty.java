package com.openclassrooms.realestatemanager.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding;
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfAddPropertyAdapter;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.resources.AppResources;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.openclassrooms.realestatemanager.util.Constants.PICK_IMAGE_GALLERY;
import static com.openclassrooms.realestatemanager.util.Constants.REQUEST_IMAGE_CAPTURE;

public class AddProperty extends Fragment {
    private static final String TAG = "AddProperty";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private AddPropertyViewModel mAddPropertyViewModel;
    private FragmentAddPropertyBinding binding;
    private AmenitiesCheckboxesBinding amenitiesBinding;
    private FormAddressPropertyBinding formAddressBinding;
    private File photoFile;
    private String currentPhotoPath;
    private List<ImageOfProperty> imagesToAdd = new ArrayList<>();
    private ImageListOfAddPropertyAdapter mImageAdapter;
    private boolean mIsPropertySold = false;
    private int mMillisOfRegisterProperty = 0;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false);
        bindIncludesLayouts();

        setEventListener();
        setRecyclerView();
        return binding.getRoot();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mAddPropertyViewModel = new ViewModelProvider(requireActivity(),vmF).get(AddPropertyViewModel.class);
        mAddPropertyViewModel.init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPropertyTypeSpinner();
        setAgentSpinner();
        setCreationDate();
        handleSoldDateInput();
    }

    private void setAgentSpinner() {
        String[] agents = AppResources.getAGENTS();
        //AgentAdapter adapter = new AgentAdapter(requireContext(),R.layout.type_dropdown_item,agents);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, agents);
        binding.addFAgent.setAdapter(adapter);
        binding.addFAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemSelected: AGENT SELECTED");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {/**/}
        });
    }

    private void setPropertyTypeSpinner() {
        String[] types = AppResources.getPropertyType();
        //PropertyTypeSpinnerAdapter adapter = new PropertyTypeSpinnerAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, types);
        binding.addFTypeDropdown.setAdapter(adapter);
        binding.addFTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //adapterView.getItemAtPosition(i);
                Log.i(TAG, "onItemSelected: item selected:: " + adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { /**/ }
        });
    }

    // SET UI
    private void setCreationDate() {
        // TODO we need the long value for save in SQLite if user change date, write the new one. Problem ? Convert string date to time milliseconds !
        mMillisOfRegisterProperty = (int) System.currentTimeMillis();
        binding.addFDateSince.setText(Utils.getTodayDate());
        binding.addFDateSince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO create DatePicker
                // TODO !!! After add DatePicker, add function to change mMillisOfRegisterProperty
            }
        });
    }

    private void handleSoldDateInput() {
        Log.i(TAG, "handleSoldDateInput: RUN.          - mIsPropertySold:: " + mIsPropertySold);
        binding.addFDateSoldOn.setEnabled(mIsPropertySold);
    }

    private void setEventListener() {
        binding.addFBtnAddImgCamera.setOnClickListener(view -> takePictureIntent());
        binding.addFBtnAddImgGallery.setOnClickListener(view -> openGallery());
        binding.addFSoldSwitch.setOnClickListener(view -> handleSwitchEvent());
    }

    private void handleSwitchEvent() {
        mIsPropertySold = !mIsPropertySold;
        Log.i(TAG, "handleSwitchEvent: click on 'Switch' button. mIsPropertySold:: " + mIsPropertySold);
        handleSoldDateInput();
        if (mIsPropertySold) {
            binding.addFDateSoldOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO use DatePicker
                    // TODO add time millis to property
                }
            });
        }
    }

    private void setRecyclerView() {
        RecyclerView rv = binding.addFImagesRecycler;
        mImageAdapter = new ImageListOfAddPropertyAdapter(imagesToAdd);
        rv.setAdapter(mImageAdapter);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.HORIZONTAL));
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
            image = File.createTempFile(imageFileName, ".jpg", storagePicturesDir);
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

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            photoFile = createImageFile();

            if (photoFile != null) {
                Uri fp = FileProvider.getUriForFile(requireActivity(), "com.openclassrooms.realestatemanager.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fp);

                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e("ERROR", "takePictureIntent: ", e);
                    // TODO display error state to the user
                }
            }
        }
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Log.i(TAG, "onActivityResult: take photo -> uri:: " + photoFile.getAbsolutePath());
            mAddPropertyViewModel.createOneImageOfProperty(photoFile.getAbsolutePath());
            if (imageBitmap != null)
                Log.i(TAG, "onActivityResult: bitmap is ok:: " + imageBitmap.toString());
            else {
                super.onActivityResult(requestCode, resultCode, data);
                Log.i(TAG, "onActivityResult: non image");
            }
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                mAddPropertyViewModel.createOneImageOfProperty(data.getData().toString());
                Log.i(TAG, "onActivityResult: uri of image:: " + data.getData().toString());
            }
        }
    }

    public void createProperty() {
        // Get inputs values:
        String type = binding.addFTypeDropdown.getText().toString();
        String desc = binding.addFDescription.getEditableText().toString();
        int surface = Integer.parseInt(binding.addFInputSurface.getText().toString());
        int price = Integer.parseInt(binding.addFInputPrice.getText().toString());
        int rooms = Integer.parseInt(binding.addFInputRooms.getText().toString());
        int bedrooms = Integer.parseInt(binding.addFInputBedrooms.getText().toString());
        int bathrooms = Integer.parseInt(binding.addFInputBathrooms.getText().toString());
        //TODO add long to this value and string date in input
        //int dateRegister = (int) System.currentTimeMillis();
        int dateSold = (int) System.currentTimeMillis();
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        String address2 = formAddressBinding.addAddress2FormAddressSuite.getEditableText().toString();
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        int codeZip = Integer.parseInt(formAddressBinding.addAddressFormPostalCode.getEditableText().toString());
        String amenities = getAmenities();
        String agent = binding.addFAgent.getText().toString();
        mAddPropertyViewModel.createNewProperty(type,desc,surface,price,rooms,bedrooms,bathrooms,mMillisOfRegisterProperty,dateSold,address1,address2,city,quarter,codeZip,amenities,agent);
    }

    private String getAmenities() {
        String[] amenities = new String[6];
        amenities[0] = amenitiesBinding.amenitiesShop.isChecked() ? "Shop" : "null";
        amenities[1] = amenitiesBinding.amenitiesPark.isChecked() ? "Park" : "null";
        amenities[2] = amenitiesBinding.amenitiesPlayground.isChecked() ? "Playground" : "null";
        amenities[3] = amenitiesBinding.amenitiesSchool.isChecked() ? "School" : "null";
        amenities[4] = amenitiesBinding.amenitiesBus.isChecked() ? "Bus" : "null";
        amenities[5] = amenitiesBinding.amenitiesSubway.isChecked() ? "Subway" : "null";
        return StringModifier.arrayToSingleString(amenities);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        formAddressBinding = null;
        amenitiesBinding = null;
        binding = null;
    }
}