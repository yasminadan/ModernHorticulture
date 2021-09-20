package ke.co.yadan.kiwianddragonhorticulture.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.yadan.kiwianddragonhorticulture.util.Event
import ke.co.yadan.kiwianddragonhorticulture.util.Resource
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    val currentUser = auth.currentUser

    val signInResponse: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val signUpResponse: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val resetPasswordResponse: MutableLiveData<Event<Resource<String>>> = MutableLiveData()

    fun signUpWithEmailAndPassword(email: String, password: String) {
        signUpResponse.postValue(Event(Resource.Loading()))
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signUpResponse.postValue(Event(Resource.Success("Account created successfully!")))
                }
            }
            .addOnFailureListener {
                signUpResponse.postValue(Event(Resource.Error(it.message)))
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        signInResponse.postValue(Event(Resource.Loading()))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signInResponse.postValue(Event(Resource.Success("Signed in successfully!")))
                }
            }
            .addOnFailureListener {
                signInResponse.postValue(Event(Resource.Error(it.message)))
            }
    }

    fun sendPasswordResetEmail(email: String) {
        resetPasswordResponse.postValue(Event(Resource.Loading()))
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resetPasswordResponse.postValue(Event(Resource.Success("Check your email!")))
                }
            }
            .addOnFailureListener { e ->
                resetPasswordResponse.postValue(Event(Resource.Success(e.message.toString())))
            }
    }

    fun logOut() = auth.signOut()

}