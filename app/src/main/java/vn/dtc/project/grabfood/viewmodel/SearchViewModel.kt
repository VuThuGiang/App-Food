package vn.dtc.project.grabfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.util.Resource
import javax.inject.Inject

@HiltViewModel
class SearchViewModel  @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel(){
    private val _searchFood = MutableStateFlow<Resource<List<Food>>>(Resource.Unspecified())
    val searchFood: StateFlow<Resource<List<Food>>> = _searchFood

    private val pagingInfo = MainCategoryViewModel.PagingInfo()

    init {
        fetchSearchFood()
    }
    fun fetchSearchFood(){
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _searchFood.emit(Resource.Loading())
            }
            firestore.
            collection("Food").limit(pagingInfo.bestFoodPage * 20).get()
                .addOnSuccessListener {result ->
                    val searchFood = result.toObjects(Food::class.java)
                    pagingInfo.isPagingEnd = searchFood == pagingInfo.oldBestFood
                    pagingInfo.oldBestFood = searchFood
                    viewModelScope.launch {
                        _searchFood.emit(Resource.Success(searchFood))
                    }
                    pagingInfo.bestFoodPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _searchFood.emit(Resource.Error(it.message.toString()))
                    }

                }
        }
    }
}