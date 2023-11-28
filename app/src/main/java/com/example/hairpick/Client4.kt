package com.example.hairpick

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.ActivityClient4Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class Client4 : AppCompatActivity(),OnMapReadyCallback {
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore
    lateinit var storageRef: StorageReference

    lateinit var binding: ActivityClient4Binding
    var googleMap: GoogleMap?=null
    private var mapReady=false
    var shopData:ShopInfo?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClient4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        db= FirebaseFirestore.getInstance()
        storage= Firebase.storage
        firestore=FirebaseFirestore.getInstance()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val shopId=intent.getStringExtra("shopId")
        if(shopId!=null){
            val docRef=db.collection("stylists").document(shopId)
            storageRef=storage.reference.child("shopImages/"+shopId)
            docRef.get().addOnSuccessListener {
                shopData=it.toObject(ShopInfo::class.java)
                dataBinding(binding,shopData!!)
            }
        }
    }
    fun dataBinding(binding:ActivityClient4Binding,shopData:ShopInfo){
        binding.shopName.text=shopData.shopName
        binding.shopAddress.text=shopData.shopAddress
        binding.priceText.text=shopData.priceList
        binding.shopNum.text=shopData.shopNum
        binding.shopDesc.text=shopData.shopDesc

        storageRef.listAll()
            .addOnSuccessListener { result->
                val urlList= mutableListOf<Uri>()

                val tasks=result.items.map{item->
                    item.downloadUrl.addOnSuccessListener { imageUri->
                        urlList.add(imageUri)
                    }

                }

                Tasks.whenAllSuccess<Unit>(tasks)
                    .addOnSuccessListener {
                        for((index,uri) in urlList.withIndex()){
                            when(index){
                                0-> Glide.with(this).load(uri).into(binding.img0)
                                1-> Glide.with(this).load(uri).into(binding.img1)
                                2-> Glide.with(this).load(uri).into(binding.img2)
                                3-> Glide.with(this).load(uri).into(binding.img3)
                                4-> Glide.with(this).load(uri).into(binding.img4)
                            }
                        }
                    }.addOnFailureListener {
                        Log.d("jeon", "이미지 다운로드 실패")
                    }

            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map  // Assign the received GoogleMap object to your variable
        mapReady=true
        updateMapIfNeeded()
    }
    private fun updateMapIfNeeded() {
        if (mapReady) {
            // 위치 설정
            val latLng = LatLng(35.8928, 128.6076)
            val position = CameraPosition.Builder()
                .target(latLng)
                .zoom(18f)
                .build()
            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(position))

            //맵 마커

            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.mapmarker)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 100, false)

            val markerOptions= MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
            markerOptions.position(latLng)
            markerOptions.title(binding.shopName.getText().toString())
            markerOptions.snippet("Tel)"+binding.shopNum.getText().toString())

            googleMap?.addMarker(markerOptions)
        }
    }
}