package vn.dtc.project.grabfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import vn.dtc.project.grabfood.data.CartFood
import vn.dtc.project.grabfood.firebase.FirebaseCommon
import vn.dtc.project.grabfood.helper.getFoodPrice
import vn.dtc.project.grabfood.util.Resource
import javax.inject.Inject

@HiltViewModel
class CartViewModel@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {
    private val _cartFood = MutableStateFlow<Resource<List<CartFood>>>(Resource.Unspecified())
    val cartFoodz = _cartFood.asStateFlow()

    private var cartFoodDocuments = emptyList<DocumentSnapshot>()

    val foodPrice = cartFoodz.map {
        when(it){
            is Resource.Success ->{
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartFood>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    fun deleteCartFood(cartFood: CartFood){
        val index = cartFoodz.value.data?.indexOf(cartFood)
        if (index != null && index != -1){
            val documentId = cartFoodDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }

    }


    private fun calculatePrice(data: List<CartFood>): Float {
        return data.sumByDouble { cartFood ->
            (cartFood.food.offerPercentage.getFoodPrice(cartFood.food.price)*cartFood.quantity).toDouble()
        }.toFloat()
    }



    init {
        getCartFoot()
    }

    private fun getCartFoot(){
        viewModelScope.launch { _cartFood.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener{value, error ->
                if (error != null || value == null){
                    viewModelScope.launch { _cartFood.emit(Resource.Error(error?.message.toString())) }
                } else{
                    cartFoodDocuments = value.documents
                    val cartFood = value.toObjects(CartFood::class.java)
                    viewModelScope.launch {_cartFood.emit( Resource.Success(cartFood)) }
                }
            }
    }

    fun changeQuantity(
        cartFood: CartFood,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cartFoodz.value.data?.indexOf(cartFood)


        if (index != null && index != -1) {
            val documentId = cartFoodDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    viewModelScope.launch { _cartFood.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartFood.quantity == 1){
                        viewModelScope.launch { _deleteDialog.emit(cartFood) }
                        return
                    }
                    viewModelScope.launch { _cartFood.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartFood.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartFood.emit(Resource.Error(exception.message.toString())) }
        }
    }
}