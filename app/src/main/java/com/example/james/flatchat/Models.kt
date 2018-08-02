package com.example.james.flatchat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImage: String): Parcelable {
    // empty constructor
    constructor() : this("","","")

}