package com.example.hairpick

import android.net.Uri
import android.util.Log

class ShopInfo(val id:String) {
    lateinit var img:String
    lateinit var name:String
    var sex:Int=0
    lateinit var num:String


    var shopImg:List<String> = listOf()
    lateinit var shopName:String
    lateinit var shopDesc:String
    lateinit var shopNum:String
    lateinit var shopAddress:String
    lateinit var priceList:String

    public fun stylistInfoset(name:String,sex:Int, num:String){
        this.name=name
        this.sex=sex
        this.num=num
    }
    public fun shopImgSet(imgList:List<String>){
        shopImg=imgList
        Log.d("Jeon", "shopImgSet: $imgList")
    }
    public fun shopInfoset(shopName:String, shopDesc:String,shopNum:String, shopAddress:String, priceList:String){
        this.shopName=shopName
        this.shopDesc=shopDesc
        this.shopNum=shopNum
        this.shopAddress=shopAddress
        this.priceList=priceList
    }

}