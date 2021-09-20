package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.farmer

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ke.co.yadan.kiwianddragonhorticulture.util.Resource
import ke.co.yadan.kiwianddragonhorticulture.util.isEmail
import ke.co.yadan.kiwianddragonhorticulture.viewmodel.AuthViewModel
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentSignInBinding

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val binding: FragmentSignInBinding by viewBinding()

    private val authViewModel: AuthViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToHomeFragment()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()
        setupObservers()
    }

    private fun setupObservers() {
        observeSignInResponse()
    }

    private fun setupOnClickListeners() {
        binding.goToSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_signInFragment_to_signUpFragment
            )
        }

        binding.signInBtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.isEmail()) {
            Toast.makeText(requireContext(), "Enter a valid email.", Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.signInWithEmailAndPassword(email, password)

    }

    private fun observeSignInResponse() {
        authViewModel.signInResponse.observe(viewLifecycleOwner, { event ->
            when (val resource = event.getContentIfNotHandled()) {
                is Resource.Success -> {
                    //hide progress bar
                    binding.progressCircular.visibility = View.GONE
                    resource.data?.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(
                            R.id.action_signInFragment_to_homeFragment
                        )
                    }
                }

                is Resource.Error -> {
                    //hide progress bar
                    binding.progressCircular.visibility = View.GONE
                    resource.message?.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }

                }

                is Resource.Loading -> {
                    //show circular progress bar
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        })
    }

}