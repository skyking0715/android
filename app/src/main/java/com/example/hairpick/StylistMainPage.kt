package com.example.hairpick

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.FragmentClientMainPageBinding
import com.example.hairpick.databinding.FragmentStylistMainPageBinding
import com.example.hairpick.databinding.ScheduleitemBinding
import com.example.hairpick.databinding.TrendimgitemBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StylistMainPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class StylistMainPage : Fragment() {
    lateinit var binding: FragmentStylistMainPageBinding
    lateinit var photoAdapter_trend: Stylist_TrendImgAdapter
    lateinit var scheduleAdapter: ScheduleAdapter
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var storageReference_trendMen: StorageReference
    lateinit var storageReference_trendWomen: StorageReference

    var photoList_men = arrayListOf<Uri>()
    var photoList_women = arrayListOf<Uri>()
    private var selectedTabIndex:Int=0

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
        storageReference_trendMen=storage.reference.child("manTrend")
        storageReference_trendWomen=storage.reference.child("womanTrend")

        //코루틴 사용 : 데이터 비동기적으로 가져오고, 모두 받아오면 그 후에 탭 클릭 이벤트 처리
        GlobalScope.launch(Dispatchers.Main) {
            getTrendImg()
            binding.trendTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        when (it.position) {
                            0 -> {
                                selectedTabIndex=0
                                updatePhotoAdapter()
                            }
                            1 ->{
                                selectedTabIndex=1
                                updatePhotoAdapter()
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentStylistMainPageBinding.inflate(inflater, container, false)
        photoAdapter_trend = Stylist_TrendImgAdapter(requireContext())
        scheduleAdapter=ScheduleAdapter(requireContext())

        val layoutManagerTrend = LinearLayoutManager(requireContext())
        val layoutManagerSchedule = LinearLayoutManager(requireContext())
        layoutManagerTrend.orientation = LinearLayoutManager.HORIZONTAL
        layoutManagerSchedule.orientation=LinearLayoutManager.VERTICAL

        binding.recyclerView1.layoutManager = layoutManagerTrend
        binding.recyclerView1.adapter = photoAdapter_trend
        binding.recyclerView2.layoutManager=layoutManagerSchedule
        binding.recyclerView2.adapter=scheduleAdapter

        sampleSchedule()

        return binding.root
    }

    fun getTrendImg(){
        storageReference_trendMen.listAll()
            .addOnSuccessListener { result->
                for(item in result.items){
                    item.downloadUrl.addOnSuccessListener {imageUri->
                        Log.d("jeon","$imageUri")
                        photoList_men.add(imageUri)
                    }
                        .addOnFailureListener {
                            Log.d("jeon","이미지 다운로드 url 가져오기 실패")
                        }

                }
            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }

        storageReference_trendWomen.listAll()
            .addOnSuccessListener { result->
                for(item in result.items){
                    item.downloadUrl.addOnSuccessListener {imageUri->
                        Log.d("jeon","$imageUri")
                        photoList_women.add(imageUri)
                    }
                        .addOnFailureListener {
                            Log.d("jeon","이미지 다운로드 url 가져오기 실패")
                        }

                }
            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }


    }
    private fun updatePhotoAdapter() {
        val selectedPhotoList = if (selectedTabIndex == 0) {
            photoList_men
        } else {
            photoList_women
        }
            photoAdapter_trend.setPhotoList(selectedPhotoList)
    }
    fun sampleSchedule(){
        scheduleAdapter.addDataList("오전","10:00","이민아")
        scheduleAdapter.addDataList("오전","10:30","하은빈")
        scheduleAdapter.addDataList("오전","11:20","김혁진")
        scheduleAdapter.addDataList("오후","02:30","오하림")
        scheduleAdapter.addDataList("오후","04:00","김동훈")
        scheduleAdapter.addDataList("오후","06:30","박동균")
    }


}

class Stylist_TrendImgViewHolder(val binding: TrendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(photoUri: Uri){
        //Glide - 구글에서 만든 이미지 로더 라이브러리
        //기술문서 작성 시 추가할 것
        Glide.with(binding.trendimgData.context)
            .load(photoUri)
            .into(binding.trendimgData)
    }
}

class Stylist_TrendImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = mutableListOf<Uri>()
    fun setPhotoList(photoList:List<Uri>){
        this.photoList.clear()
        this.photoList.addAll(photoList)
        notifyDataSetChanged()
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return Stylist_TrendImgViewHolder(
            TrendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Stylist_TrendImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)


    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}

class ScheduleViewHolder(val binding: ScheduleitemBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bind(data:String){
        val separatedStrings = data.split(" ")
        binding.ampm.text=separatedStrings.get(0)
        binding.time.text=separatedStrings.get(1)
        binding.clinetName.text=separatedStrings.get(2)
    }
}

class ScheduleAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList:ArrayList<String> = arrayListOf()
    fun addDataList(ampm:String, time:String, name:String){
        val string=ampm +" "+time +" "+name
        this.dataList.add(string)
        notifyDataSetChanged()
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ScheduleViewHolder(
            ScheduleitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ScheduleViewHolder).binding

        val photoUri = dataList[position]
        holder.bind(photoUri)


    }

    //항목 개수
    override fun getItemCount(): Int {
        return dataList.size
    }
}
