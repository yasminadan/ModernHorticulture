package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.farmer

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ke.co.yadan.kiwianddragonhorticulture.util.Constants
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.KIWI_FRUIT_NAME
import ke.co.yadan.kiwianddragonhorticulture.util.extractUsername
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.AuthViewModel
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.FirestoreViewModel
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), PopupMenu.OnMenuItemClickListener {

    private val binding: FragmentHomeBinding by viewBinding()

    private val fireStoreViewModel: FirestoreViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // fetch user's name
        fireStoreViewModel.getUserName(fireStoreViewModel.currentUser.uid)

        setupObservers()
        setupOnClickListeners()

    }

    private fun setupOnClickListeners() {
        binding.kiwiFruit.learnMore.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFruitDescriptionFragment(KIWI_FRUIT_NAME)
            )
        }

        binding.dragonFruit.learnMore.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFruitDescriptionFragment(Constants.DRAGON_FRUIT_NAME)
            )
        }

        binding.sellFruitsCard.sellFruits.setOnClickListener {
            val username =
                binding.greetUser.text.toString().extractUsername()
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSellFruitFragment(username)
            )
        }

        binding.menu.setOnClickListener {
            showPopup(it)
        }
    }

    private fun setupObservers() {
        fireStoreViewModel.username.observe(viewLifecycleOwner, { name ->
            Log.d("Username", "Observed Username: $name")
            binding.greetUser.text = "Hello $name!"
        })
    }

    private fun showPopup(view: View) {
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener(this@HomeFragment)
            inflate(R.menu.main_menu)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_go_to_uploads -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToAdvertisementsFragment(
                        binding.greetUser.text.toString().extractUsername()
                    )
                )
                true
            }

            R.id.action_logout -> {
                authViewModel.logOut()
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToSignInFragment()
                )
                true
            }

            else -> false
        }
    }
}