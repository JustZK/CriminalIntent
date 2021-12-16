package com.zk.criminalintent.bean

import java.util.*

class Crime(var id: UUID) {

    constructor() : this(UUID.randomUUID())

    var title = ""
    var date = Date()
    var solved = false
    var suspect = ""

    fun getPhotoFileName(): String{
        return "IMG $id.jpg"
    }
}