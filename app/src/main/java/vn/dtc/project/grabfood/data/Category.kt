package vn.dtc.project.grabfood.data

sealed class Category(val category: String){
    object Meat: Category("Meat")
    object SeaFood: Category("Sea Food")
    object Vegetables: Category("Vegetables")
    object Dessert: Category("Dessert")
    object Drink: Category("Drink")
    object OtherFood: Category("Other Food")

}
