package wang.xiunian.pexelsdemo.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import wang.xiunian.pexelsdemo.ui.main.entity.PhotosResponse
import java.lang.Exception

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _imageItems = MutableLiveData<ResponseList>()
    val imageItems: LiveData<ResponseList>
        get() = _imageItems

    private val _loadingResult = MutableLiveData<Boolean>()
    val loadingResult: LiveData<Boolean>
        get() = _loadingResult

    fun fetchImages(page: Int, perPage: Int) {
        viewModelScope.launch {
            try {
                val imgResponses = PexelsImageRepository.getImageResps(page, perPage)
                _imageItems.value = ResponseList(imgResponses, false)
            } catch (e: Exception) {
                Log.d(TAG, "fetchImages: failed")
                _loadingResult.value = false
            }
        }

    }

    fun loadCacheIfExist(page: Int, perPage: Int) {
        //only load the first page, maybe I can optimize every page in the future
        viewModelScope.launch {
            try {
                val imgResponses = PexelsImageRepository.getImageRespFromCached(page, perPage)
                imgResponses?.let {
                    _imageItems.value = ResponseList(it, true)
                }

            } catch (e: Exception) {
                Log.d(TAG, "fetchImages cache: failed")
            }
        }
    }
}

data class ResponseList(val photoListResponse: List<PhotosResponse>, val isCached: Boolean)