package com.example.hairpick

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hairpick.databinding.MainOneBinding

class Mainpage1 : AppCompatActivity() {
    var initTime=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val main1 = MainOneBinding.inflate(layoutInflater)
        setContentView(main1.root)

        title = "공통 1페이지"

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //10쪽 방법 2 (업버튼)


        main1.startbtn.setOnClickListener {
            val intent = Intent(this, SignInPage::class.java)
            startActivity(intent)

        }
        main1.accountNone.setOnClickListener{

            val intent = Intent(this, SignUpUser::class.java)
            startActivity(intent)
        }

    }

    //10쪽 방법 2 (업버튼)
    override fun onSupportNavigateUp(): Boolean {
        Log.d("jung", "onSupportNavigateUp")
        onBackPressed() //백버튼 눌렀을 때 업버튼과 같은 반응을 하게함 (없을시엔 어플이 종료)
        return super.onSupportNavigateUp()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis()- initTime>3000){
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                initTime=System.currentTimeMillis()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}
