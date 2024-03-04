package vn.dtc.project.grabfood.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import vn.dtc.project.grabfood.data.Category
import vn.dtc.project.grabfood.viewmodel.CategoryViewModel

class BaseCategoryViewModelFactoryFactory(
    private val  firestore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore, category) as T
    }
}