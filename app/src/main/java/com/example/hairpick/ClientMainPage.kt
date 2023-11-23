package com.example.hairpick

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.FragmentClientMainPageBinding
import com.example.hairpick.databinding.ImgitemBinding
import com.example.hairpick.databinding.RecommendimgitemBinding
import com.example.hairpick.databinding.TrendimgitemBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClientMainPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClientMainPage : Fragment() {
    lateinit var binding:FragmentClientMainPageBinding
    lateinit var photoAdapter_trend: TrendImgAdapter
    lateinit var photoAdapter_Rec:RecommendAdapter

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    fun getResourceUri(context: Context,resId:Int):Uri{
       return Uri.parse("android.resource://"+context.packageName+"/"+resId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentClientMainPageBinding.inflate(inflater, container, false)



        photoAdapter_trend = TrendImgAdapter(requireContext())
        photoAdapter_Rec= RecommendAdapter(requireContext())
        val layoutManagerTrend = LinearLayoutManager(requireContext())
        val layoutManagerRec=LinearLayoutManager(requireContext())
        layoutManagerTrend.orientation = LinearLayoutManager.HORIZONTAL
        layoutManagerRec.orientation = LinearLayoutManager.HORIZONTAL


        binding.recyclerView1.layoutManager = layoutManagerTrend
        binding.recyclerView1.adapter = photoAdapter_trend
        binding.recyclerView2.layoutManager=layoutManagerRec
        binding.recyclerView2.adapter=photoAdapter_Rec

        val trendImageResources = listOf(
            R.drawable.trend1,
            R.drawable.trend2,
            R.drawable.trend3,
            R.drawable.trend4,
            R.drawable.trend5,
            R.drawable.trend6
        )
        for (resId in trendImageResources) {
            val imageUri: Uri = getResourceUri(requireContext(), resId)
            photoAdapter_trend.addPhoto(imageUri)
        }
///////////////////////////////////////////////////////////////////////////////////////
        val recommendImageResources = mapOf(
            "가르마펌" to R.drawable.re1,
            "드롭컷" to R.drawable.re2,
            "리프펌" to R.drawable.re3,
            "쉐도우펌" to R.drawable.re4,
            "슬릭댄디펌" to R.drawable.re5,
            "시스루댄디펌" to R.drawable.re6,
            "시스루애즈펌" to R.drawable.re7,
            "시스루펌" to R.drawable.re8,
            "애즈펌" to R.drawable.re9,
            "히피펌" to R.drawable.re10
        )

        for (resId in recommendImageResources) {
            val hairName:String=resId.key
            val imageUri: Uri = getResourceUri(requireContext(), resId.value)
            photoAdapter_Rec.addPhoto(imageUri,hairName)
        }



        return binding.root
    }



}

class TrendImgViewHolder(val binding: TrendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoUri: Uri) {
        binding.trendimgData.setImageURI(photoUri)
    }
}

class TrendImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = mutableListOf<Uri>()

    fun addPhoto(uri: Uri) {
        photoList.add(uri)
        notifyDataSetChanged()
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return TrendImgViewHolder(
            TrendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TrendImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)

    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}

class RecommendViewHolder(val binding: RecommendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoUri: Uri,name:String) {
        binding.recommendimgData.setImageURI(photoUri)
        binding.hairName.text=name
    }
}

class RecommendAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = mutableListOf<Uri>()
    private val hairList= mutableListOf<String>()
    fun addPhoto(uri: Uri,name:String) {
        photoList.add(uri)
        hairList.add(name)
        notifyDataSetChanged()
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return RecommendViewHolder(
            RecommendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecommendViewHolder).binding

        val photoUri = photoList[position]
        val hairName=hairList[position]
        holder.bind(photoUri,hairName)

    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}