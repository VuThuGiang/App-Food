package vn.dtc.project.grabfood.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.adapters.BestFoodAdapter
import vn.dtc.project.grabfood.databinding.FragmentBaseCategoryBinding
import vn.dtc.project.grabfood.util.showBottomNavigationView

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestFoodAdapter by lazy { BestFoodAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()

        offerAdapter.onClick ={
            val b = Bundle().apply { putParcelable("food", it) }
            findNavController().navigate(R.id.action_homeFragment_to_foodDetailsFragment, b)
        }

        binding.rvOfferfood.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx !=0){
                    onOfferPagingRequest()
                }
            }
        })
    }

    fun showOfferLoading(){
        binding.offerfoodProgressBar.visibility = View.VISIBLE
    }
    fun hideOfferLoading(){
        binding.offerfoodProgressBar.visibility = View.GONE
    }

    open fun onOfferPagingRequest(){

    }


    private fun setupOfferRv() {
        binding.rvOfferfood.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter= offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}


