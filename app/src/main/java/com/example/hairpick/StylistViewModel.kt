package com.example.hairpick

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference

class StylistViewModel : ViewModel() {
    private val _photoList = MutableLiveData<List<Uri>>()
    val photoList: LiveData<List<Uri>> get() = _photoList
    fun getTrendMen(storageReference: StorageReference) {
        storageReference.listAll()
            .addOnSuccessListener { result ->
                val fetchedMenPhotos = mutableListOf<Uri>()
                for (item in result.items) {
                    item.downloadUrl.addOnSuccessListener { imageUri ->
                        fetchedMenPhotos.add(imageUri)
                        _photoList.value = fetchedMenPhotos
                    }.addOnFailureListener {
                        Log.d("jeon", "이미지 다운로드 url 가져오기 실패")
                    }
                }
            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }
    }
}