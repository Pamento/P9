package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.BuildConfig;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result;
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final GoogleMapsRepository mGoogleMapsRepository;
    private String mUrlOfStaticMapOfProperty;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<Location> mLocationOfAddress = new MutableLiveData<>();

    public DetailViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository, GoogleMapsRepository googleMapsRepository) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mGoogleMapsRepository = googleMapsRepository;
    }

    public LiveData<SingleProperty> getSingleProperty() {
        return mPropertiesRepository.getSingleProperty(null);
    }

    public LiveData<List<ImageOfProperty>> getImagesOfProperty() {
        return mImageRepository.getAllImagesOfProperty(null);
    }

    public LiveData<List<PropertyWithImages>> getAllProperties() {
        return mPropertiesRepository.getAllPropertiesWithImages();
    }

    public String getPropertyId() {
        return mPropertiesRepository.getPROPERTY_ID();
    }

    public String getUrlOfStaticMapOfProperty() {
        return mUrlOfStaticMapOfProperty;
    }

    public void setUrlOfStaticMapOfProperty(String location) {
        mUrlOfStaticMapOfProperty = "https://maps.google.com/maps/api/staticmap?center=" +
                location + "&zoom=15&size=200x200&markers=size:mid%7Ccolor:red%7C"
                + location + "&key=" + BuildConfig.MAPS_API_KEY;
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
}
