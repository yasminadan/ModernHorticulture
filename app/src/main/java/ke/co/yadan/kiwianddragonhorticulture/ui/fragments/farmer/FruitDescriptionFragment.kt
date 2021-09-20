package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.farmer

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.FirestoreViewModel
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentFruitDescriptionBinding

@AndroidEntryPoint
class FruitDescriptionFragment : Fragment(R.layout.fragment_fruit_description) {

    private val binding: FragmentFruitDescriptionBinding by viewBinding()

    private val args: FruitDescriptionFragmentArgs by navArgs()

    private val fireStoreViewModel: FirestoreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fruitName = args.fruitName

        // fetch fruit description
        fireStoreViewModel.getFruitDescription(fruitName)

        setupOnClickListeners()
        setupObservers()

    }

    private fun setupOnClickListeners() {
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupObservers() {
        fireStoreViewModel.fruitDescription.observe(viewLifecycleOwner) { fruitDescription ->

            binding.fruitImg.load(fruitDescription.imgSrc)

            binding.fruitNameTV.text =
                if (args.fruitName.contains("kiwi", true)) "KIWI FRUIT"
                else "DRAGON FRUIT"

            binding.fruitSummary.text = fruitDescription.description

            binding.ecologicalRequirements.apply {
                title.text = getString(R.string.ecological_requirements)
                description.text = fruitDescription.ecologicalRequirements

                var expand = false
                dropdown.setOnClickListener {
                    expand = !expand
                    if (expand) {
                        TransitionManager.beginDelayedTransition(binding.ecologicalRequirements.root, AutoTransition())
                        description.visibility = View.VISIBLE
                        dropdown.rotation = 180f
                    } else {
                        TransitionManager.beginDelayedTransition(binding.ecologicalRequirements.root, AutoTransition())
                        description.visibility = View.GONE
                        dropdown.rotation = 0f
                    }
                }
            }

            binding.soilMngt.apply {
                title.text = getString(R.string.soil_mngt)
                description.text = fruitDescription.soilManagement

                var expand = false
                dropdown.setOnClickListener {
                    expand = !expand
                    if (expand) {
                        TransitionManager.beginDelayedTransition(binding.soilMngt.root, AutoTransition())
                        description.visibility = View.VISIBLE
                        dropdown.rotation = 180f
                    } else {
                        TransitionManager.beginDelayedTransition(binding.soilMngt.root, AutoTransition())
                        description.visibility = View.GONE
                        dropdown.rotation = 0f
                    }
                }
            }

            binding.howToPlant.apply {
                title.text = getString(R.string.how_to_plant)
                description.text = fruitDescription.howToPlant

                var expand = false
                dropdown.setOnClickListener {
                    expand = !expand
                    if (expand) {
                        TransitionManager.beginDelayedTransition(binding.howToPlant.root, AutoTransition())
                        description.visibility = View.VISIBLE
                        dropdown.rotation = 180f
                    } else {
                        TransitionManager.beginDelayedTransition(binding.howToPlant.root, AutoTransition())
                        description.visibility = View.GONE
                        dropdown.rotation = 0f
                    }
                }
            }

            binding.diseaseControlMeasures.apply {
                title.text = getString(R.string.dcm)
                description.text = fruitDescription.diseaseControlMeasures

                var expand = false
                dropdown.setOnClickListener {
                    expand = !expand
                    if (expand) {
                        TransitionManager.beginDelayedTransition(binding.diseaseControlMeasures.root, AutoTransition())
                        description.visibility = View.VISIBLE
                        dropdown.rotation = 180f
                    } else {
                        TransitionManager.beginDelayedTransition(binding.diseaseControlMeasures.root, AutoTransition())
                        description.visibility = View.GONE
                        dropdown.rotation = 0f
                    }
                }
            }
        }
    }

}