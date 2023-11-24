package com.example.hairpick

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.FragmentClient3Binding
import com.example.hairpick.databinding.ShopDataBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Shop(val imageId: Int, val name: String){
    //Shop 클래스는 대표 이미지와 가게이름
}
class ShopViewHolder(val binding: ShopDataBinding):
    RecyclerView.ViewHolder(binding.root)

class ShopAdapter(val datas: MutableList<Shop>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder = ShopViewHolder(ShopDataBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ShopViewHolder).binding

        binding.itemData.text = datas[position].name
        val drawable = ContextCompat.getDrawable(binding.root.context, datas[position].imageId)
        binding.imageData.setImageDrawable(drawable)
        // ---------------------------------------------------recycleview에서 이미지 삽입

    }
    override fun getItemCount(): Int {
        return datas.size
    }
}


/**
 * A simple [Fragment] subclass.
 * Use the [Client_3.newInstance] factory method to
 * create an instance of this fragment.
 */
class Client_3 : Fragment() {
   /* // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }*/
   lateinit var binding:FragmentClient3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentClient3Binding.inflate(inflater,container,false)

        //binding.location.text = "현재위치 가져오기.."

        val datas = mutableListOf<Shop>()


        datas.add(Shop(R.drawable.re1, "가가미용실"))
        datas.add(Shop(R.drawable.re2, "나나미용실"))
        datas.add(Shop(R.drawable.re3, "다다미용실"))
        datas.add(Shop(R.drawable.re4, "라라미용실"))
        datas.add(Shop(R.drawable.re5, "마마미용실"))
        datas.add(Shop(R.drawable.re6, "바바미용실"))
        datas.add(Shop(R.drawable.re7, "사사미용실"))
        datas.add(Shop(R.drawable.re8, "아아미용실"))
        datas.add(Shop(R.drawable.re9, "자자미용실"))
        datas.add(Shop(R.drawable.re10, "차차미용실"))


        val layoutManager = LinearLayoutManager(activity)
        //xml파일에 recyclerView가 있어야함
        binding.nearShop.layoutManager=layoutManager

        val adapter = ShopAdapter(datas)
        binding.nearShop.adapter = adapter

        //binding.nearShop.addItemDecoration(MyDecoration(activity as Context)) //데코없음




        return binding.root
    }

 /*   companion object {
        *//**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Client_3.
         *//*
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Client_3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}