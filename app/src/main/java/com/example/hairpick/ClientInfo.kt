package com.example.hairpick

import android.graphics.Bitmap

class ClientInfo(val id:String) {
    lateinit var img:String
    lateinit var name:String
    var sex:Int=0
    lateinit var num:String
    lateinit var adress:String

    public fun setInfo(img:String,name:String,sex:Int, num:String, address:String){
        this.img=img
        this.name=name
        this.sex=sex
        this.num=num
        this.adress=address
    }
    public fun getInfo(){

    }

}