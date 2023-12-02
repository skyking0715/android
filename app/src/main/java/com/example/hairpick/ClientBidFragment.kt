package com.example.hairpick

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.example.hairpick.databinding.FragmentClientBidBinding
import com.example.hairpick.databinding.ItemClientBidBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MyViewHolder(val binding: ItemClientBidBinding):
    RecyclerView.ViewHolder(binding.root)

class MyAdapter():
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val datas: MutableList<bidForm> = mutableListOf()
    fun clear(){
        datas.clear()
        notifyDataSetChanged()
    }
    fun addBid(bid:bidForm){
        datas.add(bid)
    }
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemClientBidBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding

        Glide.with(binding.root.context).load(datas[position].img).into(binding.profileImgView)
        binding.priceTextView.text="제안가 : "+datas[position].price+" 원"
        binding.nameTxt.text= datas[position].name+" 디자이너"
        binding.shopNameTxt.text=datas[position].shopName

        binding.itemRoot.setOnClickListener{
            val intent: Intent = Intent(binding.root.context,Chatting::class.java)
            binding.root.context.startActivity(intent)
        }
    }
}
class ClientBidFragment : Fragment(){
    lateinit var binding:FragmentClientBidBinding
    lateinit var db: FirebaseFirestore
    lateinit var collectionRef: CollectionReference
    lateinit var adapter:MyAdapter
    val datas = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= FirebaseFirestore.getInstance()
        collectionRef=db.collection("requests").document(MyAccountApplication.email.toString()).collection("bids")
        getReqDatas()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientBidBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager=layoutManager
        adapter = MyAdapter()
        binding.recyclerView.adapter = adapter

        return binding.root
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        adapter.clear()
        getReqDatas()
    }
    fun getReqDatas(){
        datas.clear()
        val searchAddress:String=MyAccountApplication.address.toString()
        Log.d("Jeon", searchAddress)
        collectionRef.get()
            .addOnSuccessListener {
                for(document in it){
                    val bidDoc=document.toObject(bidForm::class.java)
                    adapter.addBid(bidDoc)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.w("Jeon", "Error getting documents")
            }

    }
}


