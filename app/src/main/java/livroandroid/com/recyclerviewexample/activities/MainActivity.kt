package livroandroid.com.recyclerviewexample.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.database.Database
import livroandroid.com.recyclerviewexample.entity.Aluno

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Quando o botão salvar for clicado acionará a função salvarAluno()
        btnSalvar.setOnClickListener {
           salvarAluno()
        }

        //Quando o botão consultar for clicado acionará a função consultarAlunos()
        btnConsultar.setOnClickListener {
            consultarAlunos()
        }
    }

    //Função consultarAlunos() que retorna toda a coleção de alunos que está armazenada no banco de dados.
    private fun consultarAlunos(){

        if (isDeviceConnected()) { //Verifica se há conexão com a internet para realizar a consulta.

            val task = Database().consultar("users") //Realiza a consulta da coleção usuários no banco de dados.

            val listaAlunos = ArrayList<Aluno>() //Cria um objeto ArrayList de Alunos

            progressBar.visibility = View.VISIBLE //Torna a progressBar visíviel na tela até a consulta ser finalizada.

            task?.addOnSuccessListener { result -> //Se a função consulta retornar sucesso, será executado esse trecho de código

                progressBar.visibility = View.GONE //A progressBar não ficará mais visível

                if (result != null) { //Se o objeto retornado for diferente de nulo

                    //Para cada elemento dentro do objeto retornado, capturar através de um foreach
                    result.forEach {

                        //Cada elemento do objeto retornado será capturado na variável “it” e em seguida utilizamos aqui o método toObject e informamos a classe Aluno para que seja criado um objeto Alunos com todos os dados do elemento
                        //var aluno = it.toObject(Aluno::class.java)
                        //Aqui, adicionamos cada objeto aluno em no ArrayList
                        //listaAlunos.add(aluno)

                        /*Cada elemento do objeto retornado será capturado na variável “it” e em seguida utilizamos aqui o método toObject
                        * e informamos a classe Aluno para que seja criado um objeto Alunos com todos os dados do elemento
                        */
                        var aluno = Aluno(it.id,
                                    it.data["nome"].toString(),
                                    it.data["sobrenome"].toString(),
                                    it.data["idade"].toString().toInt(),
                                    it.data["avatar"].toString().toInt())

                        //Aqui, adicionamos cada objeto aluno em no ArrayList
                        listaAlunos.add(aluno)
                    }

                    val intent = Intent(this, ListaUsuarios::class.java) //Cria uma intent para a Activity ListaUsuarios
                    intent.putExtra("Alunos", listaAlunos) //Vincula o objeto listaAlunos que contém todos os objetos recuperados do banco dados a intent.
                    startActivity(intent)//Inicia a próxima Activity

                } else {

                    showMessage("Não há alunos para exibir!") //Caso não haja alunos no banco de dados será exibida essa mensagem.
            }
                //Se a consulta não for executada com sucesso, será retornada uma falha
            }?.addOnFailureListener {

                progressBar.visibility = View.GONE
                showMessage("Houve um erro na consulta de alunos!") //Se houve falha na consulta mostrar mensagem para o usuário
                Log.d("APPERROR", it.message)//Adiciona a mensagem de erro no log
            }
        }else {
            showMessage("Sem conexão com a internet")//Se não houver conexão com a internet exibir mensagem para o usuário e não proceder com a consulta
        }
    }

    //Declaração da função salvarAluno(), que permite salvar um objeto aluno no banco de dados do firebase firestore
    private fun salvarAluno(){

        //Função verifyInputs(), permite verificar se os dados digitados pelo usuário são válidos
        var isValid: Boolean = verifyInputs()

        //Captura o id da imagem que está guardada na pasta drawable que será usada como avatar do aluno
        val avatar = (R.drawable.avatar_android)

        //Se o retorno da função verifyInputs()foi com sucesso, o código executará o bloco a seguir:
        if (isValid) {

            //criação de um identificador para o aluno que será usado como id do documento do banco
            val id = txtEditNome.text
                .toString()
                .trim()
                .toLowerCase()
                .substring(0, 1) + txtEditSobrenome.text
                .toString()
                .trim()
                .toLowerCase()
                .substring(0, txtEditSobrenome.text.toString().length)

            //criação de um objeto do tipo Aluno
            val aluno = Aluno(
                id,
                txtEditNome.text.toString(),
                txtEditSobrenome.text.toString(),
                txtEditIdade.text.toString().toInt(),
                avatar
            )

            //função para testar se há conexão com a internet
            if (isDeviceConnected()) {
                progressBar.visibility = View.VISIBLE //utilização de uma ProgressBar tornando-a visível

                val task = Database().incluir("users", id, aluno) //chamada do método incluir da classe Datase

                task.addOnCompleteListener { result ->
                    //quando a tarefa for concluída, executará o código a seguir

                    if (result.isSuccessful) {//se a tarefa for com sucesso não exibir a progressBar, mostrar mensagem e limpar os campos
                        progressBar.visibility = View.GONE
                        showMessage("Aluno cadastro com sucesso!")
                        clearFields()
                    } else {
                        progressBar.visibility =
                            View.GONE //se a tarefa não for concluída com sucesso, a progressBar também não será exibida e será mostrada uma mensagem para o usuário
                        showMessage("Houve erro no cadastro do Aluno")
                    }
                }
                //se não houver conexão com a internet, exibe mensagem.
            } else {
                showMessage("Sem conexão com a internet")
            }
        }
    }

   //Limpa os camos do formulário e dá foco no primeiro campo
    private fun clearFields() {
        txtEditNome.text!!.clear()
        txtEditSobrenome.text!!.clear()
        txtEditIdade.text!!.clear()
        txtEditNome.requestFocus()
    }

    //Função que recebe uma string e exibe na tela do usuário
    private fun showMessage(msg: String) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    //Função para verificar se a entrada do usuário é válida, caso não, será exibida uma mensagem de erro e o usuário não poderá prosseguir com o cadastro
    private fun verifyInputs(): Boolean {

        var isValidInput = true

        if (txtEditNome.text.toString().trim().isEmpty()) {
            txtInputNome.error = "O campo não pode ser vazio!"
            isValidInput = false
        }

        if (txtEditSobrenome.text.toString().trim().isEmpty()){
            txtInputSobrenome.error = "O campo não pode ser vazio!"
            isValidInput = false
        }

        if(txtEditIdade.text.toString().trim().length < 2){
            txtInputIdade.error = "O valor não pode ser menor que dois dígitos!"
            isValidInput = false
        }

        return isValidInput
    }

     //Verifica se o device está conectado a internet
    private fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
