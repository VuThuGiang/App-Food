package vn.dtc.project.grabfood.data

data class FavouriteFood(
    val food: Food,
){
    constructor(): this(Food())
}
