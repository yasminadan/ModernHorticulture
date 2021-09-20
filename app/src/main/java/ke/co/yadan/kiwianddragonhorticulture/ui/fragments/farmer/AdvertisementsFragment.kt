package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.farmer

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.FirestoreViewModel
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.adapter.GridViewAdapter
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentAdvertisementsBinding

@AndroidEntryPoint
class AdvertisementsFragment : Fragment(R.layout.fragment_advertisements) {

    private val binding: FragmentAdvertisementsBinding by viewBinding()

    private val fireStoreViewModel: FirestoreViewModel by viewModels()

    private val args: AdvertisementsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fireStoreViewModel.getSellingFruitDetails(args.username)

        setupObservers()
    }

    private fun setupObservers() {
        fireStoreViewModel.fruitDescriptionList.observe(viewLifecycleOwner) {
            val gridViewAdapter = GridViewAdapter(requireContext(), it, fireStoreViewModel)
            binding.gridView.apply {
                adapter = gridViewAdapter
            }
        }
    }
}