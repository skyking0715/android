package com.example.hairpick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.hairpick.databinding.ActivityMainFrameStylistBinding
import com.google.android.material.tabs.TabLayout

class MainFrame_stylist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding=ActivityMainFrameStylistBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val requestFrame=RequestPage()
        val clientMainFrame=ClientMainPage()
        val client3Frame=Client_3()
        val client4Frame=Client_4()

        supportFragmentManager.beginTransaction().add(binding.frameView.id, clientMainFrame).commit()
        /*fragment0 = Fragment0()
        fragment1 = Fragment1()
        fragment2 = Fragment2()
        fragment3 = Fragment3()



        supportFragmentManager.beginTransaction().add(R.id.frame, fragment0).commit()*/
        binding.frameTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position

                var selected: Fragment =clientMainFrame

                when(tab?.text){
                    "home"->selected=clientMainFrame
                    "미용실"->selected=client3Frame
                    "의뢰하기"->selected=requestFrame
                    "bid"->selected=client4Frame
                    "1:1채팅"->selected=requestFrame
                    else ->selected=requestFrame
                }

                supportFragmentManager.beginTransaction().replace(binding.frameView.id, selected).commit();
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
}