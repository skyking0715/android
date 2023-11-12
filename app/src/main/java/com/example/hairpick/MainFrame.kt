package com.example.hairpick

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hairpick.databinding.ActivityMainFrameBinding
import com.google.android.material.tabs.TabLayout


class MainFrame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMainFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val requestFrame=RequestPage()
        val client4Frame=Client4()

        supportFragmentManager.beginTransaction().add(binding.frameView.id, requestFrame).commit()
        /*fragment0 = Fragment0()
        fragment1 = Fragment1()
        fragment2 = Fragment2()
        fragment3 = Fragment3()



        supportFragmentManager.beginTransaction().add(R.id.frame, fragment0).commit()*/

        binding.frameTabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position

                var selected: Fragment? = null
                if (position == 0) {
                    //selected = fragment0
                } else if (position == 1) {
                    //selected = fragment1
                } else if (position == 2) {
                    //selected = fragment2
                } else if (position == 3) {
                    //selected = fragment3
                }else if (position == 4) {
                    //selected = fragment3
                }

                //getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }
        })








    }
}