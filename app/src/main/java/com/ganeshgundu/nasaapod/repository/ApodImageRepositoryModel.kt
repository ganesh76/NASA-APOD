package com.ganeshgundu.nasaapod.repository

import com.ganeshgundu.nasaapod.db.ApodImageData

data class ApodImageRepositoryModel(
        val responseStatus: ApodImageRepository.ResponseStatus,
        val response: ApodImageData
)