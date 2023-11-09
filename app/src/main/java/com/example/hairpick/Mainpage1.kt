package com.example.hairpick

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hairpick.databinding.MainOneBinding

class Mainpage1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val main1 = MainOneBinding.inflate(layoutInflater)
        main1.accountNone.setOnClickListener{

            Toast.makeText(applicationContext, "text Clicked", Toast.LENGTH_LONG).show()



        }

        setContentView(main1.root)

        title = "공통 1페이지"

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //10쪽 방법 2 (업버튼)
    }

    //10쪽 방법 2 (업버튼)
    override fun onSupportNavigateUp(): Boolean {
        Log.d("jung", "onSupportNavigateUp")
        onBackPressed() //백버튼 눌렀을 때 업버튼과 같은 반응을 하게함 (없을시엔 어플이 종료)
        return super.onSupportNavigateUp()
    }


//    override fun onSupportNavigateUp(): Boolean {
//        Log.d("jung", "onSupportNavigateUp")
//        return super.onSupportNavigateUp()
//    }

}
