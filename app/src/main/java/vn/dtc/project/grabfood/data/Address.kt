package vn.dtc.project.grabfood.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val phone: String
): Parcelable {

    constructor(): this("","","")
}