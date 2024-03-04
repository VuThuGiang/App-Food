package vn.dtc.project.grabfood.fragments.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.adapters.FavouriteFoodAdapter
import vn.dtc.project.grabfood.databinding.FragmentFavoriteBinding
import vn.dtc.project.grabfood.util.Resource
import vn.dtc.project.grabfood.util.VerticalItemDecoration
import vn.dtc.project.grabfood.viewmodel.FavouriteViewModel

class FavouriteFragment: Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favouriteFoodAdapter by lazy { FavouriteFoodAdapter() }
    private val viewModel by activityViewModels<FavouriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFavouriteRv()

        //click on food will show detail
        favouriteFoodAdapter.onFoodClick = {
            val b = Bundle().apply { putParcelable("food", it.food) }
            findNavController().navigate(R.id.action_favouriteFragment_to_foodDetailsFragment, b)
        }
        //remove
        favouriteFoodAdapter.onRemoveClick = {
            viewModel.remove(it)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Remove item from your favourite")
                    setMessage("Do you want to remove item from your favourite food?")
                    setNegativeButton("Cancel"){dialog,_ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Remove"){dialog,_ ->
                        viewModel.removeFavouriteFood(it)

                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.favouriteFoodz.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarFavourite.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressbarFavourite.visibility = View.GONE
                        if (it.data!!.isEmpty()){
                            showEmptyFavourite()
                            hideOtherViews()
                        } else {
                            hideEmptyFavourite()
                            showOtherViews()
                            favouriteFoodAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error ->{
                        binding.progressbarFavourite.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvAllFavorite.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvAllFavorite.visibility = View.GONE
        }
    }

    private fun hideEmptyFavourite() {
        binding.apply {
            layoutFavouriteEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyFavourite() {
        binding.apply {
            layoutFavouriteEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupFavouriteRv() {
        binding.rvAllFavorite.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = favouriteFoodAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}