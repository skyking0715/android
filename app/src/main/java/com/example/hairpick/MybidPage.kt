package com.example.hairpick

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.FragmentClientChatBinding
import com.example.hairpick.databinding.FragmentMybidPageBinding
import com.example.hairpick.databinding.MybidItemBinding
import com.example.hairpick.databinding.TimeTextItemBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MybidPage.newInstance] factory method to
 * create an instance of this fragment.
 */

class MybidList(){
    var img : Int = R.drawable.grayprofile
    var title : String = "안녕하세요?"
    var money : String = "나의 제안가격 : "

    constructor(img:Int, title:String, money : String):this(){
        this.img = img
        this.title = title
        this.money = "${this.money}"+money
    }


}

class MybidViewHolder(val binding: MybidItemBinding): RecyclerView.ViewHolder(binding.root)
class MybidAdapter(val activity:FragmentActivity,val datas: MutableList<MybidList>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder
            = MybidViewHolder(MybidItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as MybidViewHolder).binding

        binding.mybidTitle.text=datas[position].title
        binding.mybidMoney.text=datas[position].money

        //클릭 리스너
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, Chatting::class.java)
            activity.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return datas.size
    }
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener


}



class MybidPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentMybidPageBinding


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
        binding= FragmentMybidPageBinding.inflate(inflater,container,false)


        val datas = mutableListOf<MybidList>()


        datas.add(MybidList(R.drawable.grayprofile, "손상모 염색 문의", "100000"))
        datas.add(MybidList(R.drawable.grayprofile, "이런 커트 가능한가요?", "90000"))
        datas.add(MybidList(R.drawable.grayprofile, "이런 커트 가능한가요?", "70000"))
        datas.add(MybidList(R.drawable.grayprofile, "염색 문의", "150000"))

        val layoutManager = LinearLayoutManager(activity)

        binding.mybidRecyclerview.layoutManager=layoutManager

        val adapter = MybidAdapter(requireActivity(),datas) //생성자 clickListener 추가
        binding.mybidRecyclerview.adapter = adapter


        return binding.root
    }


}