package livroandroid.com.recyclerviewexample.database


import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class Database {

    //Acessando uma instância do Cloud Firestore a partir de uma Activity
    private val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    //Método para realizar a inclusão de um documento no banco de dados.
   fun incluir(collection: String, document : String, data: Any) :Task<*>{

        val task = db.collection(collection).document(document).set(data)
        return task
    }

    //Método para realizar a inclusão de um documento gerando um id aleatório no banco de dados.
    fun incluir(collection: String,  data: Any) :Task<*>{

        val task = db.collection(collection).document()
        val result  = task.set(data)
        return result
    }

    //Função que consulta um documento específico
    fun consultar(collection: String, document : String): Task<DocumentSnapshot>? {

        val returnDocument = db.collection(collection).document(document).get()
        return returnDocument
    }

    //Função que recebe o nome de uma coleção como parâmetro e retorna um objeto do tipo Task<QuerySnapshot>? como resultado. É interessante observar que esse objeto pode ser nulo.
    fun consultar(collection: String): Task<QuerySnapshot>? {

       val task = db.collection(collection).get()
       return task
    }

    //Função para deletar um documento do banco de dados
    fun delete(collection: String, document:String): Task<*>? {

        val task = db.collection(collection).document(document).delete()

        return task;
    }

    //Função que armazena um arquivo de foto no Firebase Storage
    fun armazenarFoto(pathString: String, uriFotoAluno: Uri) : UploadTask{

        val task = storageRef.child(pathString).putFile(uriFotoAluno)

        return task
    }
}

