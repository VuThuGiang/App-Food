package vn.dtc.project.grabfood.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import vn.dtc.project.grabfood.R
import vn.dtc.project.grabfood.activities.LoginRegisterActivity
import vn.dtc.project.grabfood.databinding.FragmentProfileBinding
import vn.dtc.project.grabfood.util.Resource
import vn.dtc.project.grabfood.util.showBottomNavigationView
import vn.dtc.project.grabfood.viewmodel.ProfileViewModel


@AndroidEntryPoint
class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
        }
        binding.linearFavaurite.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_favouriteFragment)
        }

        binding.linearBilling.setOnClickListener{
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
                emptyArray(),
                false
            )
            findNavController().navigate(action)
        }
        binding.linearLogOut.setOnClickListener{
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarProfile.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressbarProfile.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text =" ${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error ->{
                        binding.progressbarProfile.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}