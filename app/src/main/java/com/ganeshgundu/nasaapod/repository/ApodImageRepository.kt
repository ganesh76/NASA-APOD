package com.ganeshgundu.nasaapod.repository

import android.content.Context
import com.ganeshgundu.nasaapod.BuildConfig
import com.ganeshgundu.nasaapod.db.ApodImageDao
import com.ganeshgundu.nasaapod.db.ApodImageData
import com.ganeshgundu.nasaapod.network.ApodApiNetworkHelper
import com.ganeshgundu.nasaapod.util.NetworkManager.isNetworkAvailable
import com.ganeshgundu.nasaapod.util.PrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApodImageRepository(val apodImageDao: ApodImageDao, val context: Context) {

    enum class ResponseStatus() {
        API_ERROR,
        OFFLINE_DATA_NA,
        LAST_AVAILABLE_DATA,
        NONE
    }

    suspend fun getApodImage(): ApodImageRepositoryModel {
        if (isNetworkAvailable(context)) {
            if (PrefsHelper.getUpdatedDate(context)
                    .contentEquals(PrefsHelper.getCurrentFormattedDate())
            ) {
                return fetchApodFromDb()
            } else {
                return fetchApodFromApi()
            }
        } else {
            return fetchApodFromDb()
        }
    }

    suspend fun fetchApodFromApi(): ApodImageRepositoryModel {
        val response = ApodApiNetworkHelper.retrofitService.getAPod(BuildConfig.API_KEY, "true")
        response.body()?.let {
            if (response.isSuccessful) {
                val apodImageData = ApodImageData(it.date, it.title, it.explanation, it.imgUrl)
                if (!it.mediaType.contentEquals("image")) {
                    apodImageData.imageUrl = it.thumbnailUrl
                }
                apodImageDao.deleteAll()
                apodImageDao.insertApodImage(apodImageData)
                PrefsHelper.setUpdatedDate(context, PrefsHelper.getCurrentFormattedDate())
                return ApodImageRepositoryModel(ResponseStatus.NONE, apodImageData)
            } else {
                return ApodImageRepositoryModel(ResponseStatus.API_ERROR, ApodImageData())
            }
        } ?: return ApodImageRepositoryModel(ResponseStatus.API_ERROR, ApodImageData())
    }

    suspend fun fetchApodFromDb(): ApodImageRepositoryModel {
        lateinit var apodImageData: List<ApodImageData>
        withContext(Dispatchers.IO) {
            apodImageData = apodImageDao.getApodImage()
        }
        if (apodImageData.isEmpty()) {
            return ApodImageRepositoryModel(ResponseStatus.OFFLINE_DATA_NA, ApodImageData())
        } else {
            if (PrefsHelper.getUpdatedDate(context)
                    .contentEquals(PrefsHelper.getCurrentFormattedDate())
            ) {
                return ApodImageRepositoryModel(ResponseStatus.NONE, apodImageData.get(0))
            } else {
                return ApodImageRepositoryModel(
                    ResponseStatus.LAST_AVAILABLE_DATA,
                    apodImageData.get(0)
                )
            }
        }
    }

}