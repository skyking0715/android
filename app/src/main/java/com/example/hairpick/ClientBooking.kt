package com.example.hairpick

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.ClientBookingBinding
import com.example.hairpick.databinding.DesignerItemBinding

import com.example.hairpick.databinding.TimeTextItemBinding


//interface ItemClickListener {
//    fun onItemClick(position: Int)
//}// click listener인터페이스 추가



class Designer(val imageUrl: Int, val name: String, val info: String)
class DesignerViewHolder(val binding: DesignerItemBinding): RecyclerView.ViewHolder(binding.root)
class DesignerAdapter(val datas: MutableList<Designer>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder
            = DesignerViewHolder(DesignerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as DesignerViewHolder).binding

        val drawable = ContextCompat.getDrawable(binding.root.context, datas[position].imageUrl)

        binding.designerImg.setImageDrawable(drawable)
        binding.designerName.text = datas[position].name
        binding.designerInfo.text = datas[position].info

        //클릭 리스너
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
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

class ClientBooking : AppCompatActivity() {
    lateinit var binding : ClientBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ClientBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<Designer>()


        datas.add(Designer(R.drawable.grayprofile, "김00", "손상모 전문"))
        datas.add(Designer(R.drawable.grayprofile, "이00", "남성 커트 전문"))
        datas.add(Designer(R.drawable.grayprofile, "박00", "염색 전문"))
        datas.add(Designer(R.drawable.grayprofile, "최00", "남성 커트 전문"))


        val layoutManager = LinearLayoutManager(this) //프래그먼트로 변환시 activity로 수정
        val adapter = DesignerAdapter(datas)

        binding.bookingRecyclerview.layoutManager=layoutManager
        binding.bookingRecyclerview.adapter = adapter

        adapter.setItemClickListener(object: DesignerAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                Toast.makeText(v.context, "${datas[position].name}\n${datas[position].info}", Toast.LENGTH_SHORT).show()
            }
        })
        /////////////////////////////////////
        val times = mutableListOf<String>()
        times.add("10:00");times.add("11:00");times.add("1:30");times.add("2:30")
        times.add("3:30");times.add("4:30");times.add("5:30");times.add("6:30")


        val timelayoutManager = GridLayoutManager(this,4)
        val timeAdapter = TimeAdapter(times)

        binding.timeRecyclerview.layoutManager = timelayoutManager
        binding.timeRecyclerview.adapter = timeAdapter

        timeAdapter.setItemClickListener(object: TimeAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                Toast.makeText(v.context, "${times[position]}", Toast.LENGTH_SHORT).show()
            }
        })



        binding.bookBtn.setOnClickListener{
            //Toast.makeText(this, "예약되었습니다.", Toast.LENGTH_SHORT).show()
            successDialog()
        }


    }
    fun successDialog(){
        val btnHandler=object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1== DialogInterface.BUTTON_POSITIVE){
                   finish()
                }
            }
        }

        AlertDialog.Builder(this).run{
            setTitle("예약 성공")
            setMessage("예약이 완료되었습니다")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
}


class TimeViewHolder(val binding: TimeTextItemBinding): RecyclerView.ViewHolder(binding.root)
class TimeAdapter(val datas: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder
            = TimeViewHolder(TimeTextItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as TimeViewHolder).binding

        binding.timeText.text=datas[position]

        //클릭 리스너
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
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

