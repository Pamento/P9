package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class EditPropertyViewModel(
    private val mPropertiesRepository: PropertiesRepository,
    private val mImageRepository: ImageRepository,
    private val mGoogleMapsRepository: GoogleMapsRepository,
    private val mExecutor: Executor
) : ViewModel() {
    private val mDisposable = CompositeDisposable()
    private val mLocationOfAddress = MutableLiveData<Location>()
    private val mUpdatePropertyResponse = MutableLiveData<Int>()
    private val mCreateImgResponse: MutableLiveData<Long?> = MutableLiveData<Long?>()
    private val mUpdateImgResponse = MutableLiveData<Int>()
    val singleProperty: LiveData<SingleProperty>
        get() = mPropertiesRepository.getSingleProperty(null)
    val imagesOfProperty: LiveData<List<ImageOfProperty>>
        get() = mImageRepository.getAllImagesOfProperty(null)

    fun getLocationFromAddress(address: String?) {
        mGoogleMapsRepository.getGoogleGeoCodeOfAddress(address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Result> {
                override fun onSubscribe(d: Disposable) {
                    mDisposable.add(d)
                }

                override fun onNext(result: Result) {
                    mLocationOfAddress.value = result.geometry?.location
                }

                override fun onError(e: Throwable) { /**/
                }

                override fun onComplete() { /**/
                }
            })
    }

    val geoLocationOfProperty: LiveData<Location>
        get() = mLocationOfAddress

    // Handle data
    fun updateImageOfProperty(imageOfProperty: ImageOfProperty?) {
        mExecutor.execute {
            mUpdateImgResponse.postValue(
                mImageRepository.updateImageOfProperty(
                    imageOfProperty
                )
            )
        }
    }

    fun deleteImageOfProperty(imageId: Int) {
        mImageRepository.deletePropertyImage(imageId)
    }

    // Insert new image
    fun createImageOfProperty(imageOfProperty: ImageOfProperty?) {
        mExecutor.execute {
            mCreateImgResponse.postValue(
                mImageRepository.createPropertyImage(
                    imageOfProperty
                )
            )
        }
    }

    // Save changes
    fun updateProperty(singleProperty: SingleProperty?) {
        mExecutor.execute {
            mUpdatePropertyResponse.postValue(
                mPropertiesRepository.updateSingleProperty(
                    singleProperty
                )
            )
        }
    }

    val updatePropertyResponse: LiveData<Int>
        get() = mUpdatePropertyResponse
    val createImgResponse: LiveData<Long?>
        get() = mCreateImgResponse
    val updateImgResponse: LiveData<Int>
        get() = mUpdateImgResponse

    fun disposeDisposable() {
        if (mDisposable.isDisposed) mDisposable.dispose()
    }
}