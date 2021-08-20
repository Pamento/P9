package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result;
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final GoogleMapsRepository mGoogleMapsRepository;
    private final Executor mExecutor;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SingleProperty mSingleProperty;
    private final List<ImageOfProperty> mImagesOfPropertyList = new ArrayList<>();
    private MutableLiveData<List<ImageOfProperty>> mImagesOfProperty;
    private MutableLiveData<Location> mLocationOfAddress;
    private final MutableLiveData<Long> mCreatePropertyResponse = new MutableLiveData<>();
    private final MutableLiveData<long[]> mSaveImagesOfPropertyResponse = new MutableLiveData<>();

    public AddPropertyViewModel(PropertiesRepository propertiesRepository,
                                ImageRepository imageRepository,
                                GoogleMapsRepository googleMapsRepository,
                                Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mGoogleMapsRepository = googleMapsRepository;
        mExecutor = executor;
    }

    public void init() {
        mSingleProperty = new SingleProperty();
        mSingleProperty.setId(UUID.randomUUID().toString());
        mImagesOfProperty = new MutableLiveData<>();
        mLocationOfAddress = new MutableLiveData<>();
        if (mImagesOfPropertyList.size() > 0) {
            mImagesOfPropertyList.clear();
        }
    }

    public SingleProperty getSingleProperty() {
        return mSingleProperty;
    }

    public void setSingleProperty(SingleProperty singleProperty) {
        mSingleProperty = singleProperty;
    }

    public void createNewProperty(String type,
                                  String description,
                                  Integer surface,
                                  Integer price,
                                  Integer rooms,
                                  Integer bedroom,
                                  Integer bathroom,
                                  String dateRegister,
                                  String dateSold,
                                  String address1,
                                  String address2,
                                  String city,
                                  String quarter,
                                  Integer postalCode,
                                  String location,
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
        mSingleProperty.setLocation(location);
        mSingleProperty.setAmenities(amenities.equals("") ? null : amenities);
        mSingleProperty.setAgent(agent);
    }

    public void createOneImageOfProperty(String imageUri) {
        ImageOfProperty imgOfProperty = new ImageOfProperty();
        imgOfProperty.setPropertyId(mSingleProperty.getId());
        imgOfProperty.setPath(imageUri);
        mImagesOfPropertyList.add(imgOfProperty);
        mImagesOfProperty.setValue(mImagesOfPropertyList);
    }

    public void removeOneImageOfProperty(ImageOfProperty imageOfProperty) {
        mImagesOfPropertyList.remove(imageOfProperty);
        mImagesOfProperty.setValue(mImagesOfPropertyList);
    }

    public void setImagesOfPropertyList(List<ImageOfProperty> imagesOfPropertyList) {
        mImagesOfPropertyList.clear();
        mImagesOfPropertyList.addAll(imagesOfPropertyList);
    }

    public LiveData<List<ImageOfProperty>> getImagesOfProperty() {
        return mImagesOfProperty;
    }

    public void resetImageOfProperty() {
        mImagesOfProperty.setValue(null);
        mImagesOfPropertyList.clear();
    }

    public void getLocationFromAddress(String address) {
        mGoogleMapsRepository.getGoogleGeoCodeOfAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Result result) {
                        mLocationOfAddress.setValue(result.getGeometry().getLocation());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {/**/}

                    @Override
                    public void onComplete() {/**/}
                });
    }

    public LiveData<Location> getGeoLocationOfProperty() {
        return mLocationOfAddress;
    }

    // Save data
    public void createProperty() {
        mExecutor.execute(() -> mCreatePropertyResponse.postValue(mPropertiesRepository.createSingleProperty(mSingleProperty)));
    }

    public void saveImagesOfProperty() {
        // If true, the insert method was felt
        if (mImagesOfProperty.getValue() != null) {
            mExecutor.execute(() -> mSaveImagesOfPropertyResponse.postValue(mImageRepository.createPropertyImages(mImagesOfProperty.getValue())));
        }
    }

    public LiveData<long[]> getSaveImagesResponse() {
        return mSaveImagesOfPropertyResponse;
    }

    public LiveData<Long> getCreatePropertyResponse() {
        return mCreatePropertyResponse;
    }
}
