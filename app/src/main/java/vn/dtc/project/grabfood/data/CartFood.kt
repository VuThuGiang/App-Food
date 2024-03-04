package vn.dtc.project.grabfood.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartFood(
    val food: Food,
    val quantity: Int
): Parcelable{
    constructor(): this(Food(), 1)
}
