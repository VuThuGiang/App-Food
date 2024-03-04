package vn.dtc.project.grabfood.fragments.shopping

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.adapters.SearchAdapter
import vn.dtc.project.grabfood.databinding.FragmentSearchBinding
import vn.dtc.project.grabfood.util.Resource
import vn.dtc.project.grabfood.util.VerticalItemDecoration
import vn.dtc.project.grabfood.util.hideBottomNavigationView
import vn.dtc.project.grabfood.viewmodel.SearchViewModel

@AndroidEntryPoint
class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchFood()

        searchAdapter.onClick ={
            val b = Bundle().apply { putParcelable("food", it) }
            findNavController().navigate(R.id.action_searchFragment_to_foodDetailsFragment, b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.searchFood.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.searchFoodProgressbar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        searchAdapter.differ.submitList(it.data)
                        binding.searchFoodProgressbar.visibility = View.GONE
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.searchFoodProgressbar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
        binding.nestedScrollSearch.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
                v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchSearchFood()
            }
        })

    }

    private fun setupSearchFood() {
        searchAdapter = SearchAdapter()
        binding.rvSearchItem.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }


}