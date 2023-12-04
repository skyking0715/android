package com.example.hairpick

import android.app.ProgressDialog.show
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.MyAccountApplication.Companion.name
import com.example.hairpick.databinding.FragmentStylist4Binding
import com.example.hairpick.databinding.FragmentStylistShopBinding
import com.example.hairpick.databinding.ImgitemBinding
import com.example.hairpick.databinding.ItemClientBidBinding
import com.example.hairpick.databinding.Stylist4RequestitemBinding
import com.example.hairpick.databinding.TrendimgitemBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.util.concurrent.CountDownLatch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Stylist_4.newInstance] factory method to
 * create an instance of this fragment.
 */
class Request(){
    lateinit var id:String
    lateinit var profile: String
    lateinit var title:String
    lateinit var desc:String
    //Shop 클래스는 대표 이미지와 가게이름
    constructor(id:String,profile: String, title: String, desc:String):this(){
        this.id=id
        this.profile=profile
        this.title=title
        this.desc=desc
    }
}
class Stylist_4 : Fragment() {
    lateinit var binding:FragmentStylist4Binding
    lateinit var itembinding:Stylist4RequestitemBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var collectionRef: CollectionReference
    lateinit var datas:MutableList<Request>
    lateinit var adapter:S4Adapter
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        firestore=FirebaseFirestore.getInstance()
        collectionRef=firestore.collection("requests")
        datas = mutableListOf<Request>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentStylist4Binding.inflate(inflater,container,false)
        itembinding= Stylist4RequestitemBinding.inflate(layoutInflater)

        getReqDatas()
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.stylist4Recycle.layoutManager=layoutManager

        adapter = S4Adapter(datas,requireContext())
        binding.stylist4Recycle.adapter = adapter
        binding.areaTxt.text="[${MyAccountApplication.address}] "+"주변 의뢰"

        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        getReqDatas()
    }
    fun getReqDatas(){
        datas.clear()
        val searchAddress:String=MyAccountApplication.address.toString()
        Log.d("Jeon", searchAddress)
        collectionRef.whereEqualTo("address",searchAddress)
            .get()
            .addOnSuccessListener {
                for(document in it){
                    val reqDoc=document.toObject(requestInfo::class.java)
                    val request=Request(reqDoc.id,reqDoc.profile,reqDoc.title,reqDoc.desc) //이미지리스트 제외 리퀘스트 정보 가져오기
                    adapter.addId(reqDoc.id)
                    datas.add(request)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.w("Jeon", "Error getting documents")
            }

    }

}

class S4ViewHolder(val binding: Stylist4RequestitemBinding):
    RecyclerView.ViewHolder(binding.root){
    val nestedRecyclerView: RecyclerView = binding.nestedRecyclerView
    }

class S4Adapter(val datas: MutableList<Request>,val context: Context):
    RecyclerView.Adapter<S4ViewHolder>() {
    val storage= Firebase.storage
     val userId:MutableList<String> = mutableListOf()

    fun addId(userId:String){
       this.userId.add(userId)
    }
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): S4ViewHolder {
        return S4ViewHolder(Stylist4RequestitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: S4ViewHolder, position: Int) {
        val binding = (holder as S4ViewHolder).binding
        val request = datas[position]

        // 중첩된 RecyclerView를 초기화하고 설정합니다.
        val layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.nestedRecyclerView.layoutManager = layoutManager

        // 중첩된 RecyclerView에 대한 어댑터를 생성하고 설정합니다.
        val nestedAdapter = innerAdapter()
        holder.nestedRecyclerView.adapter = nestedAdapter

        // 프로필 이미지, 제목, 설명 설정
        Glide.with(binding.root.context).load(request.profile).into(binding.profileView)
        binding.titleView.text = request.title
        binding.descView.text = request.desc

        // 참고 이미지 리스트 가져오기 및 설정
        val storageRef = storage.reference.child("reqImages/" +datas[position].id)
        storageRef.listAll()
            .addOnSuccessListener { result ->
                val urlList = mutableListOf<Uri>()

                val tasks = result.items.map { item ->
                    val downloadTask=item.downloadUrl.addOnSuccessListener { imageUri ->
                        urlList.add(imageUri)
                    }
                        .addOnFailureListener {
                            Log.d("jeon","이미지 다운로드 실패")
                        }
                    downloadTask
                }

                Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener {
                        for ((index, uri) in urlList.withIndex()) {
                            nestedAdapter.addPhoto(uri)
                        }
                    }
                    .addOnFailureListener {
                        Log.d("jeon", "이미지 다운로드 실패")
                    }
            }

            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }


        //버튼 클릭 리스너
        binding.bidbtn.setOnClickListener {
           bid(binding,position)
            // 소프트 키보드를 내리는 부분 추가
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.bidprice.windowToken, 0)
        }
    }
    fun bid(binding:Stylist4RequestitemBinding,position:Int){
        val img=MyAccountApplication.profile
        val price=binding.bidprice.getText().toString()
        val name=MyAccountApplication.name
        val shopName=MyAccountApplication.shopName

        val suggest=bidForm(img, price ,name,shopName)

        val db = FirebaseFirestore.getInstance()
        val userID:String=userId.get(position)
        val reqCollectionRef = db.collection("requests").document(userID).collection("bids").document(MyAccountApplication.email.toString())
        val bidCollectionRef = db.collection("stylists").document(MyAccountApplication.email.toString()).collection("bids").document(userID)
        //고객에게 전송하는 입찰 데이터
        reqCollectionRef.set(suggest)
            .addOnSuccessListener {docRef->
                successDialog(context)
                //내 입찰 기록 저장
                bidCollectionRef.set(suggest)
                    .addOnSuccessListener {docRef->
                        Log.d("Jeon", "데이터 저장 성공")
                    }
                    .addOnFailureListener {
                        Log.d("Jeon", "데이터 저장 실패")
                    }
            }
            .addOnFailureListener {
                failDialog(context)
            }

    }

    fun successDialog(context: Context){
        AlertDialog.Builder(context).run {
            setTitle("Bid")
            setMessage("제안서가 성공적으로 전송되었습니다!")
            setPositiveButton("확인",null)
            show()
        }
    }
    fun failDialog(context: Context){
        AlertDialog.Builder(context).run{
            setTitle("Bid 실패")
            setMessage("다시 시도해주세요.")
            setPositiveButton("확인",null)
            show()
        }
    }
}
class innerViewHolder(val binding: ImgitemBinding):
    RecyclerView.ViewHolder(binding.root){
    fun bind(photoUri: Uri){
        //Glide - 구글에서 만든 이미지 로더 라이브러리
        //기술문서 작성 시 추가할 것
        Glide.with(binding.imgData.context)
            .load(photoUri)
            .into(binding.imgData)
    }



}
class innerAdapter():
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val photoList = mutableListOf<Uri>()
    fun addPhoto(uri: Uri) {
        photoList.add(uri)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return innerViewHolder(
            ImgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       (holder as innerViewHolder).bind(photoList[position])
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}