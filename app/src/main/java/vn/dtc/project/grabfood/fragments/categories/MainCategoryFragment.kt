package vn.dtc.project.grabfood.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.adapters.BestDealsAdapter
import vn.dtc.project.grabfood.adapters.BestFoodAdapter
import vn.dtc.project.grabfood.adapters.SpecialFoodAdapter
import vn.dtc.project.grabfood.databinding.FragmentMainCategoriesBinding
import vn.dtc.project.grabfood.util.Resource
import vn.dtc.project.grabfood.util.showBottomNavigationView
import vn.dtc.project.grabfood.viewmodel.MainCategoryViewModel

private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_categories) {
    private lateinit var binding: FragmentMainCategoriesBinding
    private lateinit var specialFoodAdapter: SpecialFoodAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestFoodAdapter: BestFoodAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoriesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialFood()
        setupBestDealRv()
        setupBestFoodRv()

        specialFoodAdapter.onClick ={
            val b = Bundle().apply { putParcelable("food", it) }
            findNavController().navigate(R.id.action_homeFragment_to_foodDetailsFragment, b)
        }
        bestDealsAdapter.onClick ={
            val b = Bundle().apply { putParcelable("food", it) }
            findNavController().navigate(R.id.action_homeFragment_to_foodDetailsFragment, b)
        }
        bestFoodAdapter.onClick ={
            val b = Bundle().apply { putParcelable("food", it) }
            findNavController().navigate(R.id.action_homeFragment_to_foodDetailsFragment, b)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.specialFood.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        specialFoodAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error ->{
                        hideLoading()
                        Log.e(TAG, it.message.toString())

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsFood.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error ->{
                        hideLoading()
                        Log.e(TAG, it.message.toString())

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestFood.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.bestFoodProgressbar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        bestFoodAdapter.differ.submitList(it.data)
                        binding.bestFoodProgressbar.visibility = View.GONE
                    }
                    is Resource.Error ->{
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.bestFoodProgressbar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
            v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestFood()
            }
        })
    }



    private fun setupBestFoodRv() {
        bestFoodAdapter = BestFoodAdapter()
        binding.rvBestDishes.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter= bestFoodAdapter
        }
    }

    private fun setupBestDealRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsDishes.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }

    private fun setupSpecialFood() {
        specialFoodAdapter = SpecialFoodAdapter()
        binding.rvSpecialDishes.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialFoodAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

}