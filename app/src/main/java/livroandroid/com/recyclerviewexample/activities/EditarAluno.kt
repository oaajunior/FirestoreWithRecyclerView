package livroandroid.com.recyclerviewexample.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_editar_aluno.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.database.Database
import livroandroid.com.recyclerviewexample.entity.Aluno
import livroandroid.com.recyclerviewexample.util.Util
import livroandroid.com.recyclerviewexample.util.Util.showMessage
import java.text.FieldPosition

//Classe para editar os dados do aluno
class EditarAluno : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_aluno)

        //Recuperação dos dados da intent
        val aluno = intent.getParcelableExtra<Aluno>("aluno")
        txtEditNomeAluno.text =  Editable.Factory.getInstance().newEditable(aluno.nome)
        txtEditSobrenomeAluno.text =   Editable.Factory.getInstance().newEditable(aluno.sobrenome)
        txtEditIdadeAluno.text = Editable.Factory.getInstance().newEditable(aluno.idade.toString())

        btnSalvarAluno.setOnClickListener {
            salvarAluno(aluno)
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    //Função criada para guardar os dados do cadastro do aluno alterado no banco de dados
    private fun salvarAluno(aluno: Aluno){

        val isValid: Boolean = verifyInputs()
        val data = Intent()

        if (isValid) {

            //criação de um objeto do tipo Aluno
            val aluno = Aluno(
                aluno.id,
                txtEditNomeAluno.text.toString(),
                txtEditSobrenomeAluno.text.toString(),
                txtEditIdadeAluno.text.toString().toInt(),
                aluno.avatar)

            if (Util.isDeviceConnected(this)){

                progressBar.visibility = View.VISIBLE

                val task = Database().incluir("users",  aluno.id, aluno) //chamada do método incluir da classe Datase

                task.addOnCompleteListener { result ->
                    //quando a tarefa for concluída, executará o código a seguir

                    if (result.isSuccessful) {//se a tarefa for com sucesso não exibir a progressBar, mostrar mensagem e limpar os campos
                        progressBar.visibility = View.GONE
                        data.putExtra("lista_usuario", "Aluno atualizado com sucesso!")
                        data.putExtra("aluno", aluno)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    } else {
                        progressBar.visibility = View.GONE //se a tarefa não for concluída com sucesso, a progressBar também não será exibida e será mostrada uma mensagem para o usuário
                        data.putExtra("lista_usuario", "Houve um erro ao atualizar o cadastro!")
                        setResult(Activity.RESULT_CANCELED, data)
                        finish()
                    }
                }
                //se não houver conexão com a internet, exibe mensagem.
            } else {
                data.putExtra("lista_usuario", "Sem conexão com a internet!")
                setResult(Activity.RESULT_CANCELED, data)
                finish()
            }
        }
    }

    //Função para verificar se todos os campos do cadastro do aluno foram preenchidos
    private fun verifyInputs(): Boolean {

        var isValidInput = true

        if (txtEditNomeAluno.text.toString().trim().isEmpty()) {
            txtInputNomeAluno.error = "O campo não pode ser vazio!"
            isValidInput = false
        }

        if (txtEditSobrenomeAluno.text.toString().trim().isEmpty()){
            txtInputSobrenomeAluno.error = "O campo não pode ser vazio!"
            isValidInput = false
        }

        if(txtEditIdadeAluno.text.toString().trim().length < 2){
            txtInputIdadeAluno.error = "O valor não pode ser menor que dois dígitos!"
            isValidInput = false
        }

        return isValidInput
    }
}
