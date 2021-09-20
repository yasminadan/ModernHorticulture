package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.farmer

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ke.co.yadan.kiwianddragonhorticulture.util.Resource
import ke.co.yadan.kiwianddragonhorticulture.util.isEmail
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.AuthViewModel
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.FirestoreViewModel
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentSignUpBinding

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val binding: FragmentSignUpBinding by viewBinding()

    private val authViewModel: AuthViewModel by viewModels()
    private val fireStoreViewModel: FirestoreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.goToSignIn.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_signInFragment
            )
        }

        binding.signUpBtn.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val repeatPassword = binding.passwordRepeat.text.toString().trim()


            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!email.isEmail()) {
                Toast.makeText(requireContext(), "Enter valid email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(requireContext(), "Passwords don't match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            signUp(name, email, password)
        }

    }

    private fun signUp(name: String, email: String, password: String) {
        authViewModel.signUpWithEmailAndPassword(email, password)

        observeSignUpResponse(name)
    }

    private fun observeSignUpResponse(name: String) {
        authViewModel.signUpResponse.observe(viewLifecycleOwner, { event ->
            when (val resource = event.getContentIfNotHandled()) {
                is Resource.Success -> {
                    binding.progressCircular.hide()
                    resource.data?.let { msg ->
                        fireStoreViewModel.saveUserName(fireStoreViewModel.currentUser.uid, name)
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(
                            R.id.action_signUpFragment_to_homeFragment
                        )
                    }
                }
                is Resource.Error -> {
                    binding.progressCircular.hide()
                    resource.message?.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    binding.progressCircular.show()
                }
            }
        })
    }

}