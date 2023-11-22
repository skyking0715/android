package com.example.hairpick

import android.net.Uri

class ShopInfo(val id:String) {
    lateinit var img:String
    lateinit var name:String
    var sex:Int=0
    lateinit var num:String


    lateinit var shopImg:MutableList<Uri>
    lateinit var shopName:String
    lateinit var shopDesc:String
    lateinit var shopNum:String
    lateinit var shopAddress:String
    lateinit var priceList:String

    public fun stylistInfoset(img:String,name:String,sex:Int, num:String){
        this.img=img
        this.name=name
        this.sex=sex
        this.num=num
    }
    public fun shopImgSet(imgList:MutableList<Uri>){
        shopImg=imgList
    }
    public fun shopInfoset(shopName:String, shopDesc:String,shopNum:String, shopAddress:String, priceList:String){
        this.shopName=shopName
        this.shopDesc=shopDesc
        this.shopNum=shopNum
        this.shopAddress=shopAddress
        this.priceList=priceList
    }
    public fun getInfo(){

    }
}