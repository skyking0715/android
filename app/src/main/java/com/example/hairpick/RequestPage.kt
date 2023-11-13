package com.example.hairpick

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.ListFormatter.Width
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.FragmentRequestPageBinding
import com.example.hairpick.databinding.FragmentStylist4Binding
import com.example.hairpick.databinding.ImgitemBinding
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestPage : Fragment(){
    lateinit var binding:FragmentRequestPageBinding
    private val PICK_IMAGE_REQUEST = 1

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



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentRequestPageBinding.inflate(inflater,container,false)
        binding.galleryBtn.setOnClickListener{
            openGallery()
        }


        val datas= mutableListOf<String>()
        for(i in 1..10){
            datas.add("Item $i")
        }
        val imgDatas= mutableListOf<Uri>()
        for(i in 1..10){
            imgDatas.add(Uri.parse("res/drawable/avatar.png"))
        }

        val layoutManager=LinearLayoutManager(activity)
        layoutManager.orientation=LinearLayoutManager.HORIZONTAL
        val adapter=ReImgAdapter(datas)

        binding.recyclerView.layoutManager=layoutManager
        binding.recyclerView.adapter=adapter

        return binding.root
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 갤러리에서 선택한 이미지 처리
        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                // TODO: 선택한 이미지에 대한 처리를 진행하세요.

                //binding.galleryBtn.

            }
        }

        getContent.launch(intent)
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RequestPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
class ReImgViewHolder(val binding:ImgitemBinding):RecyclerView.ViewHolder(binding.root)

class ReImgAdapter(val datas:MutableList<String>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //뷰 홀더
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReImgViewHolder(ImgitemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val binding=(holder as ReImgViewHolder).binding

        //binding.itemData.text=datas[position]
        //binding.imgData.setImageDrawable(data)
    }
    //항목 개수
    override fun getItemCount(): Int {
      return datas.size
    }
}







