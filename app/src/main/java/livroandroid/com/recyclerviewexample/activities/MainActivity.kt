package livroandroid.com.recyclerviewexample.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.adapter.MyAdapter
import livroandroid.com.recyclerviewexample.database.Database
import livroandroid.com.recyclerviewexample.entity.Aluno

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSalvar.setOnClickListener {
           salvarAluno()
        }

        btnConsultar.setOnClickListener {

            consultarAlunos()
        }
    }

    private fun consultarAlunos(){

        val task = Database.consultar("users")
        val listaAlunos = ArrayList<Aluno>()
        progressBar.visibility =View.VISIBLE

        task?.addOnSuccessListener {result ->

            progressBar.visibility =View.GONE

            if(result != null){

                result.forEach {
                    var aluno = it.toObject(Aluno::class.java)
                    listaAlunos.add(aluno)
                }

                val intent = Intent(this, ListaUsuarios::class.java)
                intent.putExtra("Alunos", listaAlunos)
                startActivity(intent)
            }else{

                showMessage("Não há alunos para exibir!")
            }
        }?.addOnFailureListener {

            showMessage("Houve um erro na consulta de alunos!")
        }
    }
    private fun salvarAluno(){
        var isValid: Boolean = verifyInputs()
        val avatar = (R.drawable.avatar_android)

        if (isValid) {

            val aluno = Aluno(
                txtEditNome.text.toString(),
                txtEditSobrenome.text.toString(),
                txtEditIdade.text.toString().toInt(),
                avatar
            )
            val id = txtEditNome.text.toString().toLowerCase().substring(0,1) + txtEditSobrenome.text.toString().toLowerCase().substring(0, txtEditSobrenome.text.toString().length)

            progressBar.visibility = View.VISIBLE
            val task = Database.incluir("users", id, aluno)

            task.addOnCompleteListener { result ->
                if(result.isSuccessful){
                    progressBar.visibility = View.GONE
                    showMessage("Aluno cadastro com sucesso!")
                    clearFields()
                }else{
                    progressBar.visibility = View.GONE
                    showMessage("Houve erro no cadastro do Aluno")
                }
            }
        }
    }


    private fun clearFields() {
        txtEditNome.text!!.clear()
        txtEditSobrenome.text!!.clear()
        txtEditIdade.text!!.clear()
        txtEditNome.requestFocus()
    }

    private fun showMessage(msg: String) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun verifyInputs(): Boolean {

        var isValidInput = true

        if (txtEditNome.text.toString().isEmpty()) {
            txtInputNome.error = "O campo não pode ser vazio!"
            isValidInput = false
        }

        if (txtEditSobrenome.text.toString().isEmpty()){
            txtInputSobrenome.error = "O campo não pode ser vazio!"
            isValidInput = false

        }

        if(txtEditIdade.text.toString().length < 2){
            txtInputIdade.error = "O valor não pode ser menor que dois dígitos!"
            isValidInput = false
        }

        return isValidInput
    }


}
