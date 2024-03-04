package vn.dtc.project.grabfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.dtc.project.grabfood.data.Category
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.util.Resource

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category

): ViewModel() {

    private val _offerFood = MutableStateFlow<Resource<List<Food>>>(Resource.Unspecified())
    val offerFood = _offerFood.asStateFlow()
    private val pagingInfo = PagingInfo()

    init {
        fetchOfferFood()
    }

    fun fetchOfferFood(){
        viewModelScope.launch {
            _offerFood.emit(Resource.Loading())
        }
        firestore.collection("Food").whereEqualTo("category", category.category)
            .limit(pagingInfo.offerPage * 10).get()

            .addOnSuccessListener {
                val food = it.toObjects(Food::class.java)
                pagingInfo.isPagingEnd = food == pagingInfo.oldOfferFood
                pagingInfo.oldOfferFood = food
                viewModelScope.launch {
                    _offerFood.emit(Resource.Success(food))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offerFood.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    internal data class PagingInfo(
        var offerPage: Long = 1,
        var oldOfferFood: List<Food> = emptyList(),
        var isPagingEnd: Boolean = false
    )
}