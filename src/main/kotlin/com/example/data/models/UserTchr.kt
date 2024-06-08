import kotlinx.serialization.Serializable

@Serializable
data class UserTchr(
    val Teacherid:String,
    val username: String,
    val profileImageUrl: String
)