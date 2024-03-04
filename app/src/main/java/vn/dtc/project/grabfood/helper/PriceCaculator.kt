package vn.dtc.project.grabfood.helper

fun Float?.getFoodPrice(price: Float): Float{
    if (this == null)
        return price

    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}