package com.example.hairpick

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.example.hairpick.databinding.FragmentClientBidBinding
import com.example.hairpick.databinding.ItemClientBidBinding

class MyViewHolder(val binding: ItemClientBidBinding):
    RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<Int>):
//@TODO 1: datas 원소타입 바꾸기
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemClientBidBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding

        binding.priceTextView.text = "제안가 : ${datas[position]}원"

        binding.itemRoot.setOnClickListener{
            //@TODO 2: bid 페이지 항목 클릭시 이동하도록
        }
    }
}
class ClientBidFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentClientBidBinding.inflate(inflater, container, false)
        val datas = mutableListOf<Int>() //@TODO 1

        //@TODO 3: 제안 정보 받아오도록
        for(i in 1..19){
            datas.add(i*10000)
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager=layoutManager
        val adapter = MyAdapter(datas)
        binding.recyclerView.adapter = adapter

        return binding.root
    }
}

class ClientBid :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cliBid = ActivityMainFrameBinding.inflate(layoutInflater)
        setContentView(cliBid.root)

        supportFragmentManager.beginTransaction().add(cliBid.frameView.id, ClientBidFragment()).commit()
    }
}

