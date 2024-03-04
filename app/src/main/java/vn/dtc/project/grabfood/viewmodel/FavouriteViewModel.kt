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
import kotlinx.coroutines.launch
import vn.dtc.project.grabfood.data.FavouriteFood
import vn.dtc.project.grabfood.firebase.FirebaseCommon
import vn.dtc.project.grabfood.util.Resource
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {
    private val _favouriteFood = MutableStateFlow<Resource<List<FavouriteFood>>>(Resource.Unspecified())
    val favouriteFoodz = _favouriteFood.asStateFlow()

    private var favouriteFoodDocuments = emptyList<DocumentSnapshot>()

    private val _deleteDialog = MutableSharedFlow<FavouriteFood>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    fun removeFavouriteFood(favouriteFood: FavouriteFood) {
        val index = favouriteFoodz.value.data?.indexOf(favouriteFood)
        if (index != null && index != -1){
            val documentId = favouriteFoodDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("favourite")
                .document(documentId).delete()
        }

    }




    init {
        getFavouriteFood()
    }
    private fun getFavouriteFood(){
        viewModelScope.launch { _favouriteFood.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("favourite")
            .addSnapshotListener { value, error ->
                if (error != null || value == null){
                    viewModelScope.launch { _favouriteFood.emit(Resource.Error(error?.message.toString())) }
                }else{
                    favouriteFoodDocuments = value.documents
                    val favouriteFood = value.toObjects(FavouriteFood::class.java)
                    viewModelScope.launch { _favouriteFood.emit(Resource.Success(favouriteFood)) }
                }
            }
    }
    fun remove(
        favouriteFood: FavouriteFood
    ){
        val index = favouriteFoodz.value.data?.indexOf(favouriteFood)
        if (index != null && index != -1){
            viewModelScope.launch { _deleteDialog.emit(favouriteFood) }
            return
        }
    }

}