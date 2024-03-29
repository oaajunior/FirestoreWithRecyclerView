package livroandroid.com.recyclerviewexample.entity


import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Aluno (var id: String = "", var nome: String = "", var sobrenome: String = "", var idade: Int = 0, var avatar : String = "") : Parcelable