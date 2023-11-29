package com.example.hairpick

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.FragmentStylist4Binding
import com.example.hairpick.databinding.ImgitemBinding
import com.example.hairpick.databinding.ItemClientBidBinding
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Stylist_4.newInstance] factory method to
 * create an instance of this fragment.
 */
class Stylist_4 : Fragment() {
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
        // Inflate the layout for this fragment
        val binding= FragmentStylist4Binding.inflate(inflater,container,false)

        val datas = mutableListOf<Int>()

        datas.add(R.drawable.re1)
        datas.add(R.drawable.re2)
        datas.add(R.drawable.re3)
        datas.add(R.drawable.re4)
        datas.add(R.drawable.re5)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.extraPic.layoutManager=layoutManager

        val adapter = S4Adapter(datas)
        binding.extraPic.adapter = adapter

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Stylist_4.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Stylist_4().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



class S4ViewHolder(val binding: ImgitemBinding):
    RecyclerView.ViewHolder(binding.root)

class S4Adapter(val datas: MutableList<Int>):
//@TODO 1: datas 원소타입 바꾸기
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return S4ViewHolder(ImgitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as S4ViewHolder).binding

        val drawable = ContextCompat.getDrawable(binding.root.context, datas[position])
        binding.imgData.setImageDrawable(drawable)

    }
}