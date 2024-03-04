package vn.dtc.project.grabfood.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Food(
    val id: String,
    val name: String,
    val category: String,
    val restaurant: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val images: List<String>
): Parcelable {
    constructor(): this("0","", "", "", 1f, images= emptyList())
}