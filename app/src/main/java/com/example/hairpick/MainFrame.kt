package com.example.hairpick

import android.R
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.google.android.material.tabs.TabLayout


class MainFrame : AppCompatActivity() {
    private lateinit var binding: ActivityMainFrameBinding
    private val requestFrame: RequestPage by lazy { RequestPage() }
    private val clientMainFrame: ClientMainPage by lazy { ClientMainPage() }
    private val client3Frame: Client_3 by lazy { Client_3() }
    private val client4Frame: Client_4 by lazy { Client_4() }
    private val clientChatFrame: ClientChatFragment by lazy { ClientChatFragment() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientChatFrame = ClientChatFragment() //채팅프레그먼트

        supportFragmentManager.beginTransaction().add(binding.frameView.id, clientMainFrame).commit()


        binding.frameTabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                var selected: Fragment= when(tab?.text){
                    "home"->clientMainFrame
                    "미용실"->client3Frame
                    "의뢰하기"->requestFrame
                    "bid"->client4Frame
                    "1:1채팅"->clientChatFrame
                    else ->requestFrame
                }
                showHideFragment(selected)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    //프래그먼트 한번 생성하면, 계속 재사용
    private fun showHideFragment(selectedFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        val fragments = listOf(clientMainFrame, client3Frame, requestFrame, client4Frame, clientChatFrame)

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