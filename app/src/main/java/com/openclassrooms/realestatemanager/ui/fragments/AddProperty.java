package com.openclassrooms.realestatemanager.ui.fragments;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.DatePicker;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding;
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfAddPropertyAdapter;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.notification.NotificationsUtils;
import com.openclassrooms.realestatemanager.util.resources.AppResources;
import com.openclassrooms.realestatemanager.util.system.AskOSTo;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.openclassrooms.realestatemanager.util.Constants.PICK_IMAGE_GALLERY;
import static com.openclassrooms.realestatemanager.util.Constants.REQUEST_IMAGE_CAPTURE;

public class AddProperty extends Fragment implements DatePickerDialog.OnDateSetListener {
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
    private RecyclerView imagesRecycler;
    private File photoFile;
    private String currentPhotoPath;
    private List<ImageOfProperty> imagesToAdd = new ArrayList<>();
    private ImageListOfAddPropertyAdapter mImageAdapter;
    private boolean mIsPropertySold = false;
    private int mMillisOfRegisterProperty = 0;
    private int mMillisOfSoldDate = 0;
    private boolean isDateRegister;

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
        return binding.getRoot();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mAddPropertyViewModel = new ViewModelProvider(requireActivity(), vmF).get(AddPropertyViewModel.class);
        mAddPropertyViewModel.init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRecyclerView();
        setPropertyTypeSpinner();
        setAgentSpinner();
        setCreationDate();
        handleSoldDateInput();
        setRecyclerViewObserver();
    }

    private void setRecyclerViewObserver() {
        mAddPropertyViewModel.getImagesOfProperty()
                .observe(getViewLifecycleOwner(),
                        imageOfProperties -> {
                            Log.i(TAG, "ADD__ setRecyclerViewObserver: OBSERVER. __images-of-property.size():: " + imageOfProperties.size());
                            Log.i(TAG, "ADD__ setRecyclerViewObserver: imagesToAdd.size():: " + imagesToAdd.size());
                            if (imagesToAdd.size() == 0) {
                                Log.i(TAG, "ADD__ setRecyclerViewObserver: imagesToAdd.size()== 0");
                                imagesToAdd.addAll(imageOfProperties);
                                Log.i(TAG, "ADD__ setRecyclerViewObserver: imagesToAdd.size():: " + imagesToAdd.size());
                                setRecyclerView();
                            } else {
                                Log.i(TAG, "setRecyclerViewObserver: clear()");
                                imagesToAdd.clear();
                                Log.i(TAG, "ADD__ setRecyclerViewObserver: imagesToAdd.size():: " + imagesToAdd.size());
                                imagesToAdd.addAll(imageOfProperties);
                                Log.i(TAG, "ADD__ setRecyclerViewObserver: OBSERVER. __imagesToAdd.size():: " + imagesToAdd.size());
                                updateImageAdapter(imageOfProperties);
                            }
                        });
    }

    private void updateImageAdapter(List<ImageOfProperty> ipsum) {
        Log.i(TAG, "updateImageAdapter: imageToAdd.size():: " + imagesToAdd.size());
        mImageAdapter.updateImagesList(ipsum);
    }

    private void setAgentSpinner() {
        String[] agents = AppResources.getAGENTS();
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
        mMillisOfRegisterProperty = (int) System.currentTimeMillis();
        setDateInputField(Utils.getTodayDate(), 0);
        setListenerDatePicker();
    }

    private void setListenerDatePicker() {
        binding.addFDateSince.setOnClickListener(view -> {
            AskOSTo.hideKeyboard(requireActivity());
            showDatePickerDialog();
            isDateRegister = true;
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePicker = new DatePickerDialog(
                requireActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
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
        setDateInputField(Utils.getTodayDate(), 1);
        mMillisOfSoldDate = (int) System.currentTimeMillis();
        Log.i(TAG, "handleSwitchEvent: click on 'Switch' button. mIsPropertySold:: " + mIsPropertySold);
        handleSoldDateInput();
        if (mIsPropertySold) {
            binding.addFDateSoldOn.setOnClickListener(view -> {
                isDateRegister = false;
                showDatePickerDialog();
            });
        }
    }

    private void setDateInputField(String date, int mode) {
        if (mode == 1) {
            binding.addFDateSoldOn.setText(date);
        } else {
            binding.addFDateSince.setText(date);
        }
    }

    private void setRecyclerView() {
        Log.i(TAG, "ADD__ setRecyclerView: imageToAdd.size():: " + imagesToAdd.size());
        imagesRecycler = binding.addFImagesRecycler;
        mImageAdapter = new ImageListOfAddPropertyAdapter(imagesToAdd);
        imagesRecycler.setAdapter(mImageAdapter);
        //imagesRecycler.setHasFixedSize(true);
        imagesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        //imagesRecycler.addItemDecoration(new DividerItemDecoration(imagesRecycler.getContext(), DividerItemDecoration.VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleCallback);
        itemTouchHelper.attachToRecyclerView(imagesRecycler);
    }

    ItemTouchHelper.SimpleCallback mSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAbsoluteAdapterPosition();
            int toPosition = target.getAbsoluteAdapterPosition();
            Collections.swap(imagesToAdd, fromPosition, toPosition);
            // TODO? updateImageAdapter();
            Objects.requireNonNull(imagesRecycler.getAdapter()).notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            mAddPropertyViewModel.removeOneImageOfProperty(mImageAdapter.getImageOfPropertyAt(viewHolder.getAbsoluteAdapterPosition()));
            Log.i(TAG, "onSwiped: imageRecycler::getAbsoluteAdapterPosition:: " + viewHolder.getAbsoluteAdapterPosition());
            Log.i(TAG, "onSwiped: imageRecycler::getBindingAdapterPosition():: " + viewHolder.getBindingAdapterPosition());
        }
    };

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
                Uri fp = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);
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
            Uri imageUri = Uri.fromFile(photoFile);
            mAddPropertyViewModel.createOneImageOfProperty(imageUri.toString());
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                mAddPropertyViewModel.createOneImageOfProperty(uri.toString());
            }
        }
    }

    // after click on save icon, main activity run this function to check if required fields was fill
    // If so, this function run save method
    public void checkFormValidityBeforeSave() {
        Log.i(TAG, "ADD__ checkFormValidityBeforeSave: check method RUN");
        if (imagesToAdd.size() > 0 && !binding.addFAgent.getText().toString().equals("")) {
            //mAddPropertyViewModel.setImagesOfPropertyList(mImageAdapter.getImageOfPropertyList());
            Log.i(TAG, "checkFormValidityBeforeSave: we are ready to create SingleProperty");
            //createProperty();
        } else {
            Log.i(TAG, "checkFormValidityBeforeSave: else. Some fields are empty");
            String msg;
            boolean img = imagesToAdd.size() == 0;
            boolean agent = binding.addFAgent.getText().toString().equals("");
            if (img && agent) msg = String.format(requireActivity().getResources().getString(R.string.warning_missing_2_fields), "image", "agent");
            else if (img) msg = String.format(requireActivity().getResources().getString(R.string.warning_missing_1_fields), "image");
            else msg = String.format(requireActivity().getResources().getString(R.string.warning_missing_1_fields), "agent");

            NotificationsUtils notify = new NotificationsUtils(requireContext());
            notify.showWarning(requireContext(),msg);
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
        int dateRegister = mMillisOfRegisterProperty;
        int dateSold = mMillisOfSoldDate;
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        String address2 = formAddressBinding.addAddress2FormAddressSuite.getEditableText().toString();
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        int codeZip = Integer.parseInt(formAddressBinding.addAddressFormPostalCode.getEditableText().toString());
        String amenities = getAmenities();
        String agent = binding.addFAgent.getText().toString();
        mAddPropertyViewModel.createNewProperty(type, desc, surface, price, rooms, bedrooms, bathrooms, mMillisOfRegisterProperty, dateSold, address1, address2, city, quarter, codeZip, amenities, agent);
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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        Date date = calendar.getTime();
        if (isDateRegister) {
            mMillisOfRegisterProperty = (int) calendar.getTimeInMillis();
            setDateInputField(Utils.getUSFormatOfDate(date), 0);
        } else {
            mMillisOfSoldDate = (int) calendar.getTimeInMillis();
            if (mMillisOfSoldDate >= mMillisOfRegisterProperty) {
                setDateInputField(Utils.getUSFormatOfDate(date), 1);
            } else {
                NotificationsUtils notify = new NotificationsUtils(requireContext());
                String msg = requireActivity().getResources().getString(R.string.warning_date_sold_smaller_than_register);
                notify.showWarning(requireActivity(),msg);
            }
        }
    }
}