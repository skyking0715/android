package com.example.hairpick

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.google.android.material.tabs.TabLayout


class MainFrame : AppCompatActivity() {
    var initTime=0L
    private lateinit var binding: ActivityMainFrameBinding
    private val requestFrame: RequestPage by lazy { RequestPage() }
    private val clientMainFrame: ClientMainPage by lazy { ClientMainPage() }
    private val client3Frame: Client_3 by lazy { Client_3() }
    private val clientChatFrame: ClientChatFragment by lazy { ClientChatFragment() }

    private val clientBidFrame: ClientBidFragment by lazy { ClientBidFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportFragmentManager.beginTransaction().add(binding.frameView.id, clientMainFrame).commit()


        binding.frameTabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                var selected: Fragment= when(tab?.text){
                    "home"->clientMainFrame
                    "미용실"->client3Frame
                    "의뢰하기"->requestFrame
                    "bid"->clientBidFrame
                    "1:1채팅"-> clientChatFrame
                    else ->requestFrame
                }
                    showHideFragment(selected)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.profileBtn.setOnClickListener {
            val intent = Intent(this, ClientInfoUpdate::class.java)
            startActivity(intent)
        }
    }

    //프래그먼트 한번 생성하면, 계속 재사용
     fun showHideFragment(selectedFragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        val fragments = listOf(clientMainFrame, client3Frame, requestFrame,  clientChatFrame,clientBidFrame)


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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis()- initTime>3000){
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                initTime=System.currentTimeMillis()
                return true
            }
            else {
                finishAffinity() // This will finish all activities in the task
            }
        }
        return super.onKeyDown(keyCode, event)
    }


}