package com.example.hairpick

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyAccountApplication:MultiDexApplication() {
    companion object{
        lateinit var auth:FirebaseAuth
        var email:String?="mingwan51910@gmail.com" //에러 방지용 default email
        var address:String?=null
        var sex:Int=1
        var name:String?=null

        fun checkAuth():Boolean{
            val currentUser=auth.currentUser
            return currentUser?.let{
                email=currentUser.email
                if(currentUser.isEmailVerified){
                    true
                }
                else{
                    false
                }
            }?:let{
                false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        auth= Firebase.auth
    }
}