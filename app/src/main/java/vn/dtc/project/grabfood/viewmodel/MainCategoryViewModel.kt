package vn.dtc.project.grabfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.util.Resource
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel@Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialFood = MutableStateFlow<Resource<List<Food>>>(Resource.Unspecified())
    val specialFood: StateFlow<Resource<List<Food>>> = _specialFood

    private val _bestDealsFood = MutableStateFlow<Resource<List<Food>>>(Resource.Unspecified())
    val bestDealsFood: StateFlow<Resource<List<Food>>> = _bestDealsFood

    private val _bestFood = MutableStateFlow<Resource<List<Food>>>(Resource.Unspecified())
    val bestFood: StateFlow<Resource<List<Food>>> = _bestFood

    private val pagingInfo = PagingInfo()
    init {
        fetchSpecialFood()
        fetchBestsDeals()
        fetchBestFood()
    }

    fun fetchSpecialFood(){
        viewModelScope.launch {
            _specialFood.emit(Resource.Loading())
        }

        firestore.
        collection("Food").
        whereEqualTo("category","Special Food").get().addOnSuccessListener {result ->
            val specialFoodList = result.toObjects(Food::class.java)
            viewModelScope.launch {
                _specialFood.emit(Resource.Success(specialFoodList))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _specialFood.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun fetchBestsDeals(){
        viewModelScope.launch {
            _bestDealsFood.emit(Resource.Loading())
        }
        firestore.
        collection("Food").
        whereEqualTo("category","Best Deal").get()
            .addOnSuccessListener {result ->
                val bestDealsFood = result.toObjects(Food::class.java)
                viewModelScope.launch {
                    _bestDealsFood.emit(Resource.Success(bestDealsFood))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsFood.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun fetchBestFood(){
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _bestFood.emit(Resource.Loading())
            }
            firestore.
            collection("Food").limit(pagingInfo.bestFoodPage * 10).get()
                .addOnSuccessListener {result ->
                    val bestFood = result.toObjects(Food::class.java)
                    pagingInfo.isPagingEnd = bestFood == pagingInfo.oldBestFood
                    pagingInfo.oldBestFood = bestFood
                    viewModelScope.launch {
                        _bestFood.emit(Resource.Success(bestFood))
                    }
                    pagingInfo.bestFoodPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestFood.emit(Resource.Error(it.message.toString()))
                    }

            }
        }
    }

    internal data class PagingInfo(
        var bestFoodPage: Long = 1,
        var oldBestFood: List<Food> = emptyList(),
        var isPagingEnd: Boolean = false
    )
}