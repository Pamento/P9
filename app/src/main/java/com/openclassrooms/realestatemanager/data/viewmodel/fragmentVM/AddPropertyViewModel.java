package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

public class AddPropertyViewModel extends ViewModel {
    private static final String TAG = "AddProperty";
    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final Executor mExecutor;
    private SingleProperty mSingleProperty;
    private List<ImageOfProperty> mImagesOfPropertyList = new ArrayList<>();
    private MutableLiveData<List<ImageOfProperty>> mImagesOfProperty;
    private ImageOfProperty mImageOfProperty;

    public AddPropertyViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository, Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mExecutor = executor;
    }

    public void init() {
        mSingleProperty = new SingleProperty();
        mSingleProperty.setId(UUID.randomUUID().toString());
        mImagesOfProperty = new MutableLiveData<>();
    }

    public SingleProperty getSingleProperty() {
        return mSingleProperty;
    }

    public void setSingleProperty(SingleProperty singleProperty) {
        mSingleProperty = singleProperty;
    }

    public void setImageOfProperty(ImageOfProperty imageOfProperty) {
        mImageOfProperty = imageOfProperty;
    }

    public void createNewProperty(String type,
                                  String description,
                                  Integer surface,
                                  Integer price,
                                  Integer rooms,
                                  Integer bedroom,
                                  Integer bathroom,
                                  Integer dateRegister,
                                  Integer dateSold,
                                  String address1,
                                  String address2,
                                  String city,
                                  String quarter,
                                  Integer postalCode,
                                  String amenities,
                                  String agent) {
        mSingleProperty.setType(type.equals("") ? null : type);
        mSingleProperty.setDescription(description.equals("") ? null : description);
        mSingleProperty.setSurface(surface);
        mSingleProperty.setPrice(price);
        mSingleProperty.setRooms(rooms);
        mSingleProperty.setBedroom(bedroom);
        mSingleProperty.setBathroom(bathroom);
        mSingleProperty.setDateRegister(dateRegister);
        mSingleProperty.setDateSold(dateSold);
        mSingleProperty.setAddress1(address1);
        mSingleProperty.setAddress2(address2);
        mSingleProperty.setQuarter(quarter.equals("") ? null : quarter);
        mSingleProperty.setCity(city.equals("") ? null : city);
        mSingleProperty.setPostalCode(postalCode);
        mSingleProperty.setAmenities(amenities.equals("") ? null : amenities);
        mSingleProperty.setAgent(agent);
    }

    public void createOneImageOfProperty(String imageUri) {
        mImageOfProperty = new ImageOfProperty();
        mImageOfProperty.setPropertyId(mSingleProperty.getId());
        mImageOfProperty.setPath(imageUri);
        Log.i(TAG, "createOneImageOfProperty: imageUri:: " + imageUri);
        mImagesOfPropertyList.add(mImageOfProperty);
        mImagesOfProperty.postValue(mImagesOfPropertyList);
            mImageOfProperty = null;
    }

    public void removeOneImageOfProperty(ImageOfProperty imageOfProperty) {
        mImagesOfPropertyList.remove(imageOfProperty);
        mImagesOfProperty.postValue(mImagesOfPropertyList);
    }

    public void addDescriptionToImage(int position, String description) {
        ImageOfProperty iOP = mImagesOfPropertyList.get(position);
        iOP.setDescription(description);
        mImagesOfPropertyList.set(position, iOP);
        mImagesOfProperty.setValue(mImagesOfPropertyList);
    }

    public LiveData<List<ImageOfProperty>> getImagesOfProperty() {
        return mImagesOfProperty;
    }


    // Save data
    public boolean createSingleProperty() {
        mExecutor.execute(() -> mPropertiesRepository.createSingleProperty(mSingleProperty));
        return true;
    }

    public boolean createImagesOfProperty() {
        mExecutor.execute(() -> mImageRepository.addPropertyImages(mImagesOfProperty.getValue()));
        return true;
    }
}
