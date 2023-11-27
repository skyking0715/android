package com.example.hairpick

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.FragmentTrendMenBinding
import com.example.hairpick.databinding.TrendimgitemBinding
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
 * Use the [TrendMenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrendMenFragment : Fragment() {
    lateinit var binding:FragmentTrendMenBinding
    lateinit var photoAdapter_trend: Men_TrendImgAdapter

    lateinit var storage: FirebaseStorage
    lateinit var storageReference_trendMen: StorageReference

    private var uriList: ArrayList<Uri>? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        storage= Firebase.storage
        storageReference_trendMen=storage.reference.child("manTrend")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTrendMenBinding.inflate(inflater, container, false)

        photoAdapter_trend = Men_TrendImgAdapter(requireContext())
        val layoutManagerTrend = LinearLayoutManager(requireContext())
        layoutManagerTrend.orientation = LinearLayoutManager.HORIZONTAL


        binding.recyclerView1.layoutManager = layoutManagerTrend
         binding.recyclerView1.adapter = photoAdapter_trend

        /*arguments?.let {
            uriList = it.getParcelableArrayList("uriList")
            uriList?.let {
                for(uri in it){
                    Log.d("Jeon","$uri")
                    photoAdapter_trend.addPhoto(uri)
                }
                }
        }*/
        getTrendImg()

        return binding.root
    }
    companion object{
        fun newInstance(uriList: ArrayList<Uri>): TrendMenFragment {
            val fragment = TrendMenFragment()
            val args = Bundle()
            args.putParcelableArrayList("uriList", uriList)
            fragment.arguments = args
            return fragment
        }
    }
    fun getTrendImg(){
        storageReference_trendMen.listAll()
            .addOnSuccessListener { result->
                for(item in result.items){
                    item.downloadUrl.addOnSuccessListener {imageUri->
                        Log.d("jeon","$imageUri")
                        photoAdapter_trend.addPhoto(imageUri)
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

}
class Men_TrendImgViewHolder(val binding: TrendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("jeon", "ACTION_DOWN")
                    // 터치 다운 이벤트 처리
                }

                MotionEvent.ACTION_MOVE -> {
                    Log.d("jeon", "ACTION_MOVE")
                    // 터치 이동 이벤트 처리
                }

                MotionEvent.ACTION_UP -> {
                    Log.d("jeon", "ACTION_UP")
                    // 터치 업 이벤트 처리
                }
            }
            true
        }
    }
    fun bind(photoUri: Uri){
        //Glide - 구글에서 만든 이미지 로더 라이브러리
        //기술문서 작성 시 추가할 것
        Glide.with(binding.trendimgData.context)
            .load(photoUri)
            .into(binding.trendimgData)
    }
}

class Men_TrendImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = mutableListOf<Uri>()


    fun addPhoto(uri:Uri) {
        photoList.add(uri)
        notifyDataSetChanged()
        Log.d("jeon", "Added photo: $uri, Total count: ${photoList.size}")
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return Men_TrendImgViewHolder(
            TrendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Men_TrendImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)


    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}