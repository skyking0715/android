package com.example.hairpick

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.FragmentClient3Binding
import com.example.hairpick.databinding.FragmentClient4Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Client_4.newInstance] factory method to
 * create an instance of this fragment.
 */
class Client_4 : Fragment(),OnMapReadyCallback{
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore
    lateinit var storageRef: StorageReference

    lateinit var binding:FragmentClient4Binding
    var googleMap:GoogleMap?=null
    private var mapReady=false
    var shopData:ShopInfo?=null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        db= FirebaseFirestore.getInstance()
        storage= Firebase.storage
        firestore=FirebaseFirestore.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentClient4Binding.inflate(inflater,container,false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
      mapFragment.getMapAsync(this)

        val shopId=arguments?.getString("shopId")
        if(shopId!=null){
            val docRef=db.collection("stylists").document(shopId)
            storageRef=storage.reference.child("shopImages/"+shopId)
            docRef.get().addOnSuccessListener {
                shopData=it.toObject(ShopInfo::class.java)
                dataBinding(binding,shopData!!)
            }
        }





        return binding.root
    }

    fun dataBinding(binding:FragmentClient4Binding,shopData:ShopInfo){
        binding.shopName.text=shopData.shopName
        binding.shopAddress.text=shopData.shopAddress
        binding.priceText.text=shopData.priceList

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
                                1->Glide.with(this).load(uri).into(binding.img1)
                                2->Glide.with(this).load(uri).into(binding.img2)
                                3->Glide.with(this).load(uri).into(binding.img3)
                                4->Glide.with(this).load(uri).into(binding.img4)
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

            val markerOptions=MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
            markerOptions.position(latLng)
            markerOptions.title(binding.shopName.getText().toString())

            googleMap?.addMarker(markerOptions)
        }
    }
}