package com.ganeshgundu.nasaapod.viewmodel

import androidx.lifecycle.*
import com.ganeshgundu.nasaapod.repository.ApodImageRepository
import com.ganeshgundu.nasaapod.repository.ApodImageRepositoryModel
import kotlinx.coroutines.launch

class ApodImageViewModel(val repository: ApodImageRepository) : ViewModel() {

    val responseData = MutableLiveData<ApodImageRepositoryModel>()
    val showLoading = MutableLiveData<Boolean>()

    fun getApodData() {
        showLoading.value = true
        viewModelScope.launch {
            val apodImageRepositoryModel = repository.getApodImage()
            showLoading.value = false
            responseData.value = apodImageRepositoryModel
        }
    }

    class ApodImageViewModelFactory(private val repository: ApodImageRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ApodImageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ApodImageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
