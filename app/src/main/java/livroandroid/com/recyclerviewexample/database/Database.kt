package livroandroid.com.recyclerviewexample.database

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*

object Database {

    val db = FirebaseFirestore.getInstance()
    val TAG = "OBERDAN-ERROR"

    fun incluir(collection: String, document : String, data: Any):Task<Void>{

        val task = db.collection(collection).document(document).set(data)
        return task
    }


    fun consultar(collection: String, document : String): DocumentSnapshot? {

        var returnDocument: DocumentSnapshot? = null
        db.collection(collection).document(document).get().addOnSuccessListener { result ->

          returnDocument = result
        }
            return returnDocument
    }

    fun consultar(collection: String): Task<QuerySnapshot>? {

        val task = db.collection(collection).get()
        return task
    }
}

