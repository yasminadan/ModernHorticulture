package ke.co.yadan.kiwianddragonhorticulture.model

data class GridItem(
    val username: String,
    val phoneNumber: String,
    val quantity: String,
    val sellingPrice: String,
    val typeOfFruit: String,
    val location: String,
    val imagesUrlList: ArrayList<String>
)
