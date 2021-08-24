package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val mPropertiesRepository: PropertiesRepository, private val mImageRepository: ImageRepository, private val mGoogleMapsRepository: GoogleMapsRepository) : ViewModel() {
    var urlOfStaticMapOfProperty: String? = null
        private set
    private val mDisposable = CompositeDisposable()
    private val mLocationOfAddress = MutableLiveData<Location>()
    var isDollar = true
    val singleProperty: LiveData<SingleProperty>
        get() = mPropertiesRepository.getSingleProperty(null)
    val imagesOfProperty: LiveData<List<ImageOfProperty>>
        get() = mImageRepository.getAllImagesOfProperty(null)
    val allProperties: LiveData<List<PropertyWithImages>>
        get() = mPropertiesRepository.allPropertiesWithImages
    val propertyId: String
        get() = mPropertiesRepository.propertY_ID

    fun setUrlOfStaticMapOfProperty(location: String) {
        urlOfStaticMapOfProperty = ("https://maps.google.com/maps/api/staticmap?center=" +
                location + "&zoom=15&size=200x200&markers=size:mid%7Ccolor:red%7C"
                + location + "&key=" + BuildConfig.MAPS_API_KEY)
    }

    fun getLocationFromAddress(address: String?) {
        mGoogleMapsRepository.getGoogleGeoCodeOfAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Result> {
                    override fun onSubscribe(d: Disposable) {
                        mDisposable.add(d)
                    }

                    override fun onNext(result: Result) {
                        mLocationOfAddress.value = result.geometry.location
                    }

                    override fun onError(e: Throwable) { /**/
                    }

                    override fun onComplete() { /**/
                    }
                })
    }

    val geoLocationOfProperty: LiveData<Location>
        get() = mLocationOfAddress

    fun updateSingleProperty(singleProperty: SingleProperty?) {
        mPropertiesRepository.updateSingleProperty(singleProperty)
    }
}