package ke.co.yadan.kiwianddragonhorticulture.ui.fragments.onboarding.screens

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.yadan.kiwianddragonhorticulture.R
import ke.co.yadan.kiwianddragonhorticulture.databinding.FragmentThirdScreenBinding

class ThirdScreen : Fragment(R.layout.fragment_third_screen) {

    private val binding: FragmentThirdScreenBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginFarmer.setOnClickListener {
            findNavController().navigate(
                R.id.action_viewPagerFragment_to_farmer_login_flow
            )
        }

    }

}