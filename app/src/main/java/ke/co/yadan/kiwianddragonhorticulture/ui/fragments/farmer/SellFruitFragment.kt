package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.farmer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.FirestoreViewModel
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentSellFruitBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellFruitFragment : Fragment(R.layout.fragment_sell_fruit) {

    private val binding: FragmentSellFruitBinding by viewBinding()

    private val imagesList: MutableList<Uri> = mutableListOf()
    private val imagesUrlList: MutableList<String> = mutableListOf()

    private val firestoreViewModel: FirestoreViewModel by viewModels()

    private val args: SellFruitFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnCLickListeners()
        setupDropDownMenu()
    }

    private fun setupOnCLickListeners() {
        binding.imageSelector.setOnClickListener {
            chooseImages()
        }

        binding.uploadFruit.setOnClickListener {
            uploadFruit()
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun chooseImages() {
        val intent = Intent()

        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        resultLauncher.launch(intent)
    }

    private fun uploadFruit() {
        val phoneNumber = binding.phoneNumber.text.toString().trim()
        val typeOfFruit = binding.autoComplete.text.toString().trim()
        val quantity = binding.quantity.text.toString().trim()
        val sellingPrice = binding.pricePerKilo.text.toString().trim()
        val location = binding.location.text.toString().trim()

        if (phoneNumber.isEmpty() || typeOfFruit.isEmpty() || quantity.isEmpty()
            || sellingPrice.isEmpty() || location.isEmpty()
        ) {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagesList.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please choose at lease one image!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        binding.progressCircular.visibility = View.VISIBLE

        uploadImagesToFirestore()
        lifecycleScope.launch {
            delay(3000L)
            uploadSellingFruitDetails(phoneNumber, typeOfFruit, quantity, sellingPrice, location)
        }
    }

    private fun uploadImagesToFirestore() {
        for (i in imagesList.indices) {
            firestoreViewModel.uploadImage(imagesList[i])
            imagesUrlList.add(i, imagesList[i].lastPathSegment!!)
        }
    }

    private fun uploadSellingFruitDetails(
        phoneNumber: String,
        typeOfFruit: String,
        quantity: String,
        sellingPrice: String,
        location: String
    ) {
        firestoreViewModel.uploadSellingFruitDetails(
            args.username, phoneNumber, typeOfFruit, quantity, sellingPrice, location, imagesUrlList
        )

        observeUploadTask()
    }

    private fun observeUploadTask() {
        firestoreViewModel.sellingDetailsUpload.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            binding.progressCircular.visibility = View.INVISIBLE
            findNavController().navigate(
                SellFruitFragmentDirections.actionSellFruitFragmentToHomeFragment()
            )
        }
    }

    private fun setupDropDownMenu() {
        val items = listOf("Dragon Fruit", "Kiwi Fruit")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.autoComplete.setAdapter(adapter)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result?.data?.let { intent ->
                    if (intent.clipData == null) { // one image is selected
                        imagesList.clear()
                        imagesList.add(0, intent.data!!)
                    } else { // more than one image is selected
                        intent.clipData?.let { clipData ->
                            for (i in 0..clipData.itemCount) {
                                imagesList.clear()
                                imagesList.add(i, clipData.getItemAt(i).uri)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select at least one image!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}