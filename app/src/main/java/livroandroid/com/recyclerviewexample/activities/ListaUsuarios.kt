package livroandroid.com.recyclerviewexample.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lista_usuarios.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.adapter.MyAdapter
import livroandroid.com.recyclerviewexample.database.Database
import livroandroid.com.recyclerviewexample.entity.Aluno
import livroandroid.com.recyclerviewexample.util.Util

//classe para listar todos os alunos cadastrados no banco de dados
class ListaUsuarios() : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var listaAlunos:ArrayList<Aluno>
    private var posicaoAluno = -1
    private val ACTIVITY_IDENTIFICATOR = 1
    private val ACTIVITY_NAME = "lista_usuario"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        setTitle("Lista de Alunos")
        listaAlunos = intent.getParcelableArrayListExtra<Aluno>("Alunos")
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(listaAlunos, ::editOrRemoveAluno)

        recyclerView = rvLista.apply{

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    //Função para verificar o retorno da função MyAdapter para verificar se queremos editar ou remover aluno
    private fun editOrRemoveAluno(posicao: Int, result:String){

        if(result == "remover"){

            excluirCadastro(posicao)

        }else if(result == "editar"){

            editarCadastro(posicao)
        }
    }

    //Função para remover um aluno do cadastro
    private fun excluirCadastro(position: Int){

        progressBarLista.visibility = View.VISIBLE
        val result = Database().delete("users", listaAlunos[position].id  )

        if (result != null){

            result.addOnSuccessListener {

                progressBarLista.visibility = View.GONE
                listaAlunos.removeAt(position)
                viewAdapter.notifyItemRemoved(position)
                viewAdapter.notifyItemRangeChanged(position, viewAdapter.itemCount -1);
                Util.showMessage(this, "Aluno removido com sucesso!")

            }.addOnFailureListener {

                progressBarLista.visibility = View.GONE
                Util.showMessage(this, "Houve erro na deleção do registro!")
            }
        }else{
            progressBarLista.visibility = View.GONE
            Util.showMessage(this, "Registro não existe no servidor")
        }
    }

    //Função para alterar os dados de um aluno. Esta função chama uma activity que mostra os dados do aluno para ser alterado
     private fun editarCadastro(position: Int) {
         val intent = Intent(this, EditarAluno::class.java)
         intent.putExtra("aluno", listaAlunos[position])
         posicaoAluno = position
         startActivityForResult(intent, ACTIVITY_IDENTIFICATOR)
     }

    /* Função onActivityResult aguarda o resultado da activity de alteração do cadastro de alunos se foi com sucesso ou não.
    * Se foi com sucesso, remove o aluno da lista e atualiza a visualização, se não, mostra a mensagem de erro, caso tenha
    * acontecido.
    */
    override fun onActivityResult(requestCode :Int, resultCode :Int, data :Intent? ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_IDENTIFICATOR) {

            if (resultCode == Activity.RESULT_OK) {
                    Util.showMessage(this, data?.getStringExtra(ACTIVITY_NAME))
                    listaAlunos.set((posicaoAluno), data!!.getParcelableExtra("aluno"))
                    viewAdapter.notifyItemChanged(posicaoAluno)
            }
            else if (resultCode == Activity.RESULT_CANCELED && data != null){

                Util.showMessage(this, data.getStringExtra(ACTIVITY_NAME))
            }
        }
    }
}
