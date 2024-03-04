package vn.dtc.project.grabfood.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.adapters.HomeViewpagerAdapter
import vn.dtc.project.grabfood.databinding.FragmentHomeBinding
import vn.dtc.project.grabfood.fragments.categories.MainCategoryFragment
import vn.dtc.project.grabfood.fragments.categories.Meat_Fragment
import vn.dtc.project.grabfood.fragments.categories.Sea_Food_Fragment
import vn.dtc.project.grabfood.fragments.categories.Vegetables_Fragment
import vn.dtc.project.grabfood.fragments.categories.Desert_Fragment
import vn.dtc.project.grabfood.fragments.categories.Drink_Fragment
import vn.dtc.project.grabfood.fragments.categories.Other_Food_Fragment

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf(
            MainCategoryFragment(),
            Meat_Fragment(),
            Sea_Food_Fragment(),
            Vegetables_Fragment(),
            Desert_Fragment(),
            Drink_Fragment(),
            Other_Food_Fragment()
        )

        binding.viewpagerHome.isUserInputEnabled = false

        binding.searchBar.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        val viewPager2Adapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Meat"
                2 -> tab.text = "Sea Food"
                3 -> tab.text = "Vegetables"
                4 -> tab.text = "Dessert"
                5 -> tab.text = "Drink"
                6 -> tab.text = "Other Food"
            }
        }.attach()
    }
}