package com.example.hairpick

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.FragmentStylistMainPageBinding
import com.example.hairpick.databinding.FragmentTrendWomenBinding
import com.example.hairpick.databinding.TrendimgitemBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrendWomenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrendWomenFragment : Fragment() {
    lateinit var binding:FragmentTrendWomenBinding
    lateinit var photoAdapter_trend: Women_TrendImgAdapter
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("jeon","trendWomen")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTrendWomenBinding.inflate(inflater, container, false)

        photoAdapter_trend = Women_TrendImgAdapter(requireContext())
        val layoutManagerTrend = LinearLayoutManager(requireContext())
        layoutManagerTrend.orientation = LinearLayoutManager.HORIZONTAL


        binding.recyclerView1.layoutManager = layoutManagerTrend
        binding.recyclerView1.adapter = photoAdapter_trend

        return binding.root
    }
}

class Women_TrendImgViewHolder(val binding: TrendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(photoUri: Uri){
        //Glide - 구글에서 만든 이미지 로더 라이브러리
        //기술문서 작성 시 추가할 것
        Glide.with(binding.trendimgData.context)
            .load(photoUri)
            .into(binding.trendimgData)
    }
}

class Women_TrendImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        return Women_TrendImgViewHolder(
            TrendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Women_TrendImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)


    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}