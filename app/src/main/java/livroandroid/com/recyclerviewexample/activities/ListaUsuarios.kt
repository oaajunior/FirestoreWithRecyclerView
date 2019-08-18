package livroandroid.com.recyclerviewexample.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lista_usuarios.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.adapter.MyAdapter
import livroandroid.com.recyclerviewexample.database.Database
import livroandroid.com.recyclerviewexample.entity.Aluno

class ListaUsuarios() : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        setTitle("Lista de Alunos")
        val listaAlunos = intent.getParcelableArrayListExtra<Aluno>("Alunos")
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(listaAlunos, progressBarLista)

        recyclerView = rvLista.apply{

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
