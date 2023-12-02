package com.example.hairpick

import android.graphics.Bitmap

class ClientInfo() {

    lateinit var  id:String
    lateinit var imgUrl:String
    lateinit var name:String
    var sex:Int=0
    lateinit var num:String
    lateinit var adress:String

    constructor(id:String):this(){
        this.id=id
    }
    public fun setInfo(name:String,sex:Int, num:String, address:String){
        this.name=name
        this.sex=sex
        this.num=num
        this.adress=address
    }

    public fun setInfo2(img:String, name:String,num:String,address: String){
        this.imgUrl=img
        this.name=name
        this.num=num
        this.adress=address
    }


}