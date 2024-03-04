package vn.dtc.project.grabfood.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.activities.ShoppingActivity
import vn.dtc.project.grabfood.adapters.ViewPager2Images
import vn.dtc.project.grabfood.data.CartFood
import vn.dtc.project.grabfood.data.FavouriteFood
import vn.dtc.project.grabfood.databinding.FragmentDetailsBinding
import vn.dtc.project.grabfood.util.Resource
import vn.dtc.project.grabfood.util.hideBottomNavigationView
import vn.dtc.project.grabfood.viewmodel.DetailViewModel

@AndroidEntryPoint
class FoodDetailsFragment: Fragment() {
    private val args by navArgs<FoodDetailsFragmentArgs>()
    private lateinit var binding: FragmentDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val food = args.food

        setupViewpager()

        binding.imageClose.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.apply {
            tvFoodName.text = food.name
            if (food.offerPercentage != null){
                food.offerPercentage.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * food.price
                    tvFoodPrice.text = "$ ${String.format("%.2f",priceAfterOffer)}"
                }
            }else{
                tvFoodPrice.text = "$ ${food.price}"
            }

            tvRestaurant.text = food.restaurant
            tvFoodDescription.text = food.description
        }
        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateFoodInCart(CartFood(food,1))
        }
        //favourite
        binding.icFavouriteFoodButton.setOnClickListener{
            viewModel.addUpdateFoodInFavourite(FavouriteFood(food))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToFavourite.collectLatest {
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                        binding.icFavouriteFoodButton.setBackgroundResource(R.drawable.ic_favourite2)
                        Toast.makeText(requireContext(), "Food added to favourite", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


//-------------------------------------


        viewPagerAdapter.differ.submitList(food.images)

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), "Food added to cart", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error ->{
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun setupViewpager() {
        binding.apply {
            viewPagerFoodImages.adapter = viewPagerAdapter
        }
    }
}
