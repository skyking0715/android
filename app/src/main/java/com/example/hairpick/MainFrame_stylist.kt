package com.example.hairpick

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.example.hairpick.databinding.ActivityMainFrameStylistBinding
import com.google.android.material.tabs.TabLayout

class MainFrame_stylist : AppCompatActivity() {
    private lateinit var binding: ActivityMainFrameStylistBinding
    private val stylistMainFrame: StylistMainPage by lazy { StylistMainPage() }
    private val stylistShopFrame: StylistShop by lazy { StylistShop() }
    //private val client3Frame: Client_3 by lazy { Client_3() }
    //private val client4Frame: Client_4 by lazy { Client_4() }
    //private val clientChatFrame: ClientChatFragment by lazy { ClientChatFragment() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainFrameStylistBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(binding.frameView.id, stylistMainFrame).commit()

        binding.frameTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var selected: Fragment =when(tab?.text){
                    "home"->stylistMainFrame
                    "프로필"->stylistShopFrame
                    //"의뢰하기"->
                    //"bid"->
                    //"1:1채팅"->
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

        val fragments = listOf(stylistMainFrame,stylistShopFrame)

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
}