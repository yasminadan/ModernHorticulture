package ke.co.yadan.kiwianddragonhorticulture.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.yadan.kiwianddragonhorticulture.model.FruitDescription
import ke.co.yadan.kiwianddragonhorticulture.model.GridItem
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.DESCRIPTION_FIELD
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.DISEASE_CONTROL_MEASURES_FIELD
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.ECOLOGICAL_REQUIREMENTS_FIELD
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.FRUITS_COLLECTION
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.HOW_TO_PLANT_FIELD
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.IMG_FIELD
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.SELLING_FRUITS_COLLECTION
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.SOIL_MANAGEMENT_FIELD
import ke.co.yadan.kiwianddragonhorticulture.util.Constants.Companion.USERS_COLLECTION
import javax.inject.Inject

@HiltViewModel
class FirestoreViewModel @Inject constructor(
    currentUser: FirebaseUser,
    private val db: FirebaseFirestore,
    private val storageRef: StorageReference
) : ViewModel() {

    val currentUser = currentUser

    val imageDownloadUrl: MutableLiveData<String> = MutableLiveData()

    val sellingDetailsUpload: MutableLiveData<String> = MutableLiveData()
    val fruitDescriptionList: MutableLiveData<List<GridItem>> = MutableLiveData()

    val username: MutableLiveData<String> = MutableLiveData()
    val fruitDescription: MutableLiveData<FruitDescription> = MutableLiveData()

    fun saveUserName(uid: String, name: String) {
        val user = hashMapOf("name" to name)
        Log.d("UserID", "saveUserName: $uid")
        db.collection(USERS_COLLECTION)
            .document(uid)
            .set(user)
    }

    fun getUserName(uid: String) {
        Log.d("GetUsername", "getUserName: $uid")
        db.collection(USERS_COLLECTION).document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.getString("name").let {
                    username.postValue(it)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching name", exception)
            }
    }

    fun getFruitDescription(name: String) {
        db.collection(FRUITS_COLLECTION).document(name)
            .get()
            .addOnSuccessListener { snapshot ->
                fruitDescription.postValue(
                    FruitDescription(
                        diseaseControlMeasures = snapshot.getString(DISEASE_CONTROL_MEASURES_FIELD)!!,
                        ecologicalRequirements = snapshot.getString(ECOLOGICAL_REQUIREMENTS_FIELD)!!,
                        howToPlant = snapshot.getString(HOW_TO_PLANT_FIELD)!!,
                        soilManagement = snapshot.getString(SOIL_MANAGEMENT_FIELD)!!,
                        imgSrc = snapshot.getString(IMG_FIELD)!!,
                        description = snapshot.getString(DESCRIPTION_FIELD)!!
                    )
                )
            }
    }

    fun uploadImage(uri: Uri) {
        val fruitRef = storageRef.child("images/${uri.lastPathSegment}")

        val uploadTask = fruitRef.putFile(uri)

        uploadTask.addOnCompleteListener {
            if (it.isSuccessful) {

                fruitRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d("DownloadUri", "uploadImage: $downloadUri")
                }

            }
        }
    }

    fun uploadSellingFruitDetails(
        username: String,
        phoneNumber: String,
        typeOfFruit: String,
        quantity: String,
        sellingPrice: String,
        location: String,
        imagesUrlList: MutableList<String>
    ) {
        val fruitSellingDetails = hashMapOf(
            "username" to username,
            "phoneNumber" to phoneNumber,
            "typeOfFruit" to typeOfFruit,
            "quantity" to quantity,
            "sellingPrice" to sellingPrice,
            "location" to location,
            "imagesUrlList" to imagesUrlList
        )

        db.collection(SELLING_FRUITS_COLLECTION).document()
            .set(fruitSellingDetails)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sellingDetailsUpload.postValue("Successfully uploaded!")
                }
            }
            .addOnFailureListener {
                sellingDetailsUpload.postValue(it.message)
            }
    }

    fun getSellingFruitDetails(username: String) {
        db.collection(SELLING_FRUITS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                fruitDescriptionList.postValue(
                    documents.map {
                        GridItem(
                            it.getString("username")!!,
                            it.getString("phoneNumber")!!,
                            it.getString("typeOfFruit")!!,
                            it.getString("quantity")!!,
                            it.getString("sellingPrice")!!,
                            it.getString("location")!!,
                            it.get("imagesUrlList")!! as ArrayList<String>
                        )
                    }.toList()
                )
            }
            .addOnFailureListener { exception ->
                Log.w("QueryFruitsToSell", "Error getting documents: ", exception)
            }
    }

    fun getImageDownloadLink(lastPathSegment: String) {
        val fruitRef = storageRef.child("images/$lastPathSegment")

        fruitRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageDownloadUrl.postValue(task.result.toString())
            }
        }.addOnFailureListener {
            Log.d("DownloadUrl", "getImageDownloadLink: cannot get download url at the moment!")
        }
    }
}