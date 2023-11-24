package com.example.hairpick

import android.graphics.Bitmap

class ClientInfo() {

    lateinit var  id:String
    lateinit var img:String
    lateinit var name:String
    var sex:Int=0
    lateinit var num:String
    lateinit var adress:String

    constructor(id:String):this(){
        this.id=id
    }
    public fun setInfo(img:String,name:String,sex:Int, num:String, address:String){
        this.img=img
        this.name=name
        this.sex=sex
        this.num=num
        this.adress=address
    }


}