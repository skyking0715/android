package com.example.hairpick

import android.security.identity.AccessControlProfile
import android.util.Log

class requestInfo() {
    lateinit var id:String
    lateinit var address:String
    lateinit var profile: String
    var refImg:List<String> = listOf()
    lateinit var title:String
    lateinit var desc:String




    constructor(id:String, address:String):this(){
        this.id=id
        this.address=address
    }

    public fun refImgSet(imgList:List<String>){
        refImg=imgList
    }
    public fun reqInfoset(profile:String,title:String, desc:String){
        this.profile=profile
        this.title=title
        this.desc=desc
    }


}