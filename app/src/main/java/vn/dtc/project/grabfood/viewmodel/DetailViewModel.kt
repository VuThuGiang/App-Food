package vn.dtc.project.grabfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.dtc.project.grabfood.data.CartFood
import vn.dtc.project.grabfood.data.FavouriteFood
import vn.dtc.project.grabfood.firebase.FirebaseCommon
import vn.dtc.project.grabfood.util.Resource
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel() {
    private val _addToCart = MutableStateFlow<Resource<CartFood>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    private val _addToFavourite = MutableStateFlow<Resource<FavouriteFood>>(Resource.Unspecified())
    val addToFavourite = _addToFavourite.asStateFlow()

    fun addUpdateFoodInFavourite(favouriteFood: FavouriteFood){
        firestore.collection("user").document(auth.uid!!).collection("favourite")
            .whereEqualTo("food.id", favouriteFood.food.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){ // add new food to favourite
                        addNewToFavourite(favouriteFood)
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToFavourite.emit(Resource.Error(it.message.toString())) }
            }
    }

    private fun addNewToFavourite(favouriteFood: FavouriteFood) {
        firebaseCommon.addFoodToFavourite(favouriteFood){addedFood, e->
            viewModelScope.launch{
                if (e == null){
                    _addToFavourite.emit(Resource.Success(addedFood!!))
                } else {
                    _addToFavourite.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    fun addUpdateFoodInCart(cartFood: CartFood){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("food.id", cartFood.food.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){//Add new food
                        addNewFood(cartFood)
                    } else{
                        val food = it.first().toObject(CartFood::class.java)
                        if (food == cartFood){// Increase the quantity
                            val documentID = it.first().id
                            increaseQuantity(documentID, cartFood)
                        } else{ //Add new food
                            addNewFood(cartFood)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }
    private fun addNewFood(cartFood: CartFood){
        firebaseCommon.addFoodToCart(cartFood){ addedFood, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedFood!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }

        }
    }

    private fun increaseQuantity(documentId: String, cartFood: CartFood){
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartFood!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }


}