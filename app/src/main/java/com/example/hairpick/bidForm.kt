package com.example.hairpick

import com.google.android.gms.common.util.Strings

class bidForm() {
    lateinit var img:String
    lateinit var price:String
    lateinit var name:String
    lateinit var shopName:String

    constructor(img:String, price:String,name:String, shopName:String ):this(){
        this.img=img
        this.price=price
        this.name=name
        this.shopName=shopName
    }
}