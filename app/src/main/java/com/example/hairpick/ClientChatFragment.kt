package com.example.hairpick

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.ChatUserDataBinding
import com.example.hairpick.databinding.FragmentClient4Binding
import com.example.hairpick.databinding.FragmentClientChatBinding
import com.example.hairpick.databinding.ShopDataBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClientChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClientChatFragment : Fragment() , ItemClickListener {

    lateinit var binding: FragmentClientChatBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onItemClick(position: Int) {
        // 채팅유저 클릭 후 이벤트
        // Chatting만 나옴
        val intent = Intent(activity, Chatting::class.java)
        startActivity(intent)
    }
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
        binding= FragmentClientChatBinding.inflate(inflater,container,false)


        val datas = mutableListOf<String>()


        datas.add("ㄱㄱㄱ")
        datas.add("ㄴㄴㄴ")
        datas.add("ㄷㄷㄷ")
        datas.add("ㄹㄹㄹ")
        datas.add("ㅁㅁㅁ")

        val layoutManager = LinearLayoutManager(activity)

        binding.chatRecyclerView.layoutManager=layoutManager

        val adapter = ChatAdapter(datas, this) //생성자 clickListener 추가
        binding.chatRecyclerView.adapter = adapter



        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClientChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClientChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


interface ItemClickListener {
    fun onItemClick(position: Int)
}// click listener인터페이스 추가

class ChatViewHolder(val binding:ChatUserDataBinding, val clickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root), View.OnClickListener{

    init {
        // ViewHolder의 생성자에서 클릭 리스너 등록
        binding.chatUser.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        // 클릭 -> ItemClickListener를 통해 처리
        clickListener.onItemClick(adapterPosition)
    }

}

class ChatAdapter(val datas: MutableList<String>, val itemClickListener: ItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder
                = ChatViewHolder(ChatUserDataBinding.inflate(LayoutInflater.from(parent.context),parent,false), itemClickListener)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ChatViewHolder).binding
        binding.userName.text = datas[position]

    }
    override fun getItemCount(): Int {
        return datas.size
    }


}