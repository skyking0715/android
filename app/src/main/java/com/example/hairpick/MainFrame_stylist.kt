package com.example.hairpick

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.example.hairpick.databinding.ActivityMainFrameStylistBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class MainFrame_stylist : AppCompatActivity() {
    lateinit var db:FirebaseFirestore
    private lateinit var binding: ActivityMainFrameStylistBinding
    private val stylistMainFrame: StylistMainPage by lazy { StylistMainPage() }
    private val stylistShopFrame: StylistShop by lazy { StylistShop() }
    private val stylist4Frame: Stylist_4 by lazy { Stylist_4() }
    private val clientChatFrame: ClientChatFragment by lazy { ClientChatFragment() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= FirebaseFirestore.getInstance()
        val myInfo=getUserObject{ //유저정보 객체
                stylistInfo ->
            if(stylistInfo!=null){
                MyAccountApplication.name=stylistInfo.name
                MyAccountApplication.address=stylistInfo.shopAddress
                MyAccountApplication.profile=stylistInfo.img

            }else{
                Log.d("jeon", "데이터 로드 실패")
            }
        }




        binding=ActivityMainFrameStylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(binding.frameView.id, stylistMainFrame).commit()

        binding.frameTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var selected: Fragment =when(tab?.text){
                    "home"->stylistMainFrame
                    "프로필"->stylistShopFrame
                    "의뢰하기"->stylist4Frame
                    //"bid"->
                    "1:1채팅"->clientChatFrame
                    else ->stylistMainFrame
                }
                showHideFragment(selected)
              /*  //탭 색깔 설정 - 테스트
               val iconColor = ContextCompat.getColor(applicationContext, R.color.tabselected)
                tab?.icon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)*/
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
              /*  //탭 색깔 설정 - 테스트
               val iconColor = ContextCompat.getColor(applicationContext, R.color.tabunselected)
                tab?.icon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)*/
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
    //프래그먼트 한번 생성하면, 계속 재사용
    private fun showHideFragment(selectedFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        val fragments = listOf(stylistMainFrame,stylistShopFrame,stylist4Frame,clientChatFrame)

        for (fragment in fragments) {
            if (fragment != selectedFragment && fragment.isAdded) {
                transaction.hide(fragment) //이미 생성되어있다면, 숨김
            }
        }

        if (selectedFragment.isAdded) {
            transaction.show(selectedFragment)
        } else {
            transaction.add(binding.frameView.id, selectedFragment, selectedFragment.javaClass.simpleName)
        }

        transaction.commit()
    }

    fun getUserObject(callback: (ShopInfo?)->Unit){
        var shopInfo:ShopInfo?=null
        val docRef=db.collection("stylists").document(MyAccountApplication.email.toString())
        docRef.get().addOnSuccessListener { document->
            shopInfo=document.toObject(ShopInfo::class.java)
            callback(shopInfo)

        }.addOnFailureListener {
            Log.d("jeon","사용자 데이터 가져오기 실패")
            callback(null)
        }

    }

}