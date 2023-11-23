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
    lateinit var photoAdapter: TrendImgAdapter

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



        photoAdapter = TrendImgAdapter(requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL


        binding.recyclerView1.layoutManager = layoutManager
        binding.recyclerView1.adapter = photoAdapter

        var resId1=R.drawable.trend1
        var imageUri1:Uri=getResourceUri(requireContext(),resId1)
        photoAdapter.addPhoto(imageUri1)
        var resId2=R.drawable.trend2
        var imageUri2:Uri=getResourceUri(requireContext(),resId2)
        photoAdapter.addPhoto(imageUri2)
        var resId3=R.drawable.trend3
        var imageUri3:Uri=getResourceUri(requireContext(),resId3)
        photoAdapter.addPhoto(imageUri3)
        var resId4=R.drawable.trend4
        var imageUri4:Uri=getResourceUri(requireContext(),resId4)
        photoAdapter.addPhoto(imageUri4)
        var resId5=R.drawable.trend5
        var imageUri5:Uri=getResourceUri(requireContext(),resId5)
        photoAdapter.addPhoto(imageUri5)
        var resId6=R.drawable.trend6
        var imageUri6:Uri=getResourceUri(requireContext(),resId6)
        photoAdapter.addPhoto(imageUri6)

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