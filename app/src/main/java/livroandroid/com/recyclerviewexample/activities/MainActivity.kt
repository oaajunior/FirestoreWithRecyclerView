package livroandroid.com.recyclerviewexample.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.take_a_photo.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.util.Util
import livroandroid.com.recyclerviewexample.util.Util.showMessage
import livroandroid.com.recyclerviewexample.database.Database
import livroandroid.com.recyclerviewexample.entity.Aluno
import livroandroid.com.recyclerviewexample.util.ImageUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var file :File? = null
    private val imgFoto by lazy {
        findViewById<ImageView>(R.id.imgFotoAlunoTemp)
    }
    private var uriFotoAluno : Uri? = null
    private lateinit var dialog: Dialog
    private val REQUEST_COD_GALLERY = 0
    private val REQUEST_COD_PHOTO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Quando o botão salvar for clicado acionará a função salvarAluno()
        btnSalvarAluno.setOnClickListener {
           salvarAluno()
        }

        //Quando o botão consultar for clicado acionará a função consultarAlunos()
        btnCancelar.setOnClickListener {
            consultarAlunos()
        }

        imgFotoAlunoTemp.setOnClickListener {
            dialog = Dialog(this)
            dialog.setContentView(R.layout.take_a_photo)
            dialog.show()

            dialog.imgCamera.setOnClickListener {
                dialog.dismiss()
                takePhoto()
            }

            dialog.imgGallery.setOnClickListener {
                dialog.dismiss()
                getImagesGallery()
            }
        }
    }

    //Função para chamar a intent da galeria para seleção de uma foto
    private fun getImagesGallery() {
        val intentGallery = Intent(Intent.ACTION_GET_CONTENT)
        intentGallery.setType("image/*")
        startActivityForResult(intentGallery, REQUEST_COD_GALLERY)
    }

    //Função consultarAlunos() que retorna toda a coleção de alunos que está armazenada no banco de dados.
    private fun consultarAlunos(){

        if (Util.isDeviceConnected(this)) { //Verifica se há conexão com a internet para realizar a consulta.

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
                                    it.data["avatar"].toString())

                        //Aqui, adicionamos cada objeto aluno em no ArrayList
                        listaAlunos.add(aluno)
                    }

                    val intent = Intent(this, ListaUsuarios::class.java) //Cria uma intent para a Activity ListaUsuarios
                    intent.putExtra("Alunos", listaAlunos) //Vincula o objeto listaAlunos que contém todos os objetos recuperados do banco dados a intent.
                    startActivity(intent)//Inicia a próxima Activity

                } else {

                    showMessage(this, "Não há alunos para exibir!") //Caso não haja alunos no banco de dados será exibida essa mensagem.
            }
                //Se a consulta não for executada com sucesso, será retornada uma falha
            }?.addOnFailureListener {

                progressBar.visibility = View.GONE
                showMessage(this,"Houve um erro na consulta de alunos!") //Se houve falha na consulta mostrar mensagem para o usuário
                Log.d("APPERROR", it.message)//Adiciona a mensagem de erro no log
            }
        }else {
            showMessage(this, "Sem conexão com a internet")//Se não houver conexão com a internet exibir mensagem para o usuário e não proceder com a consulta
        }
    }

    //Declaração da função salvarAluno(), que permite salvar um objeto aluno no banco de dados do firebase firestore
    private fun salvarAluno(){

        //Função verifyInputs(), permite verificar se os dados digitados pelo usuário são válidos
        var isValid: Boolean = verifyInputs()
        var avatar = ""

        //Se o retorno da função verifyInputs()foi com sucesso, o código executará o bloco a seguir:
        if (isValid) {

            //criação de um identificador para o aluno que será usado como id do documento do banco
            val id = txtEditNomeAluno.text
                .toString()
                .trim()
                .toLowerCase()
                .substring(0, 1) + txtEditSobrenomeAluno.text
                .toString()
                .trim()
                .toLowerCase()
                .substring(0, txtEditSobrenomeAluno.text.toString().length)

            //criação de um objeto do tipo Aluno


            //função para testar se há conexão com a internet
            if (Util.isDeviceConnected(this)) {
                progressBar.visibility = View.VISIBLE //utilização de uma ProgressBar tornando-a visível

                if (uriFotoAluno != null) {

                    val pathString = "images/${uriFotoAluno?.path?.substringAfterLast("/")}" //Definição da variavel que contem o nome do arquivo de foto

                    val uploadTask = Database().armazenarFoto(pathString, uriFotoAluno!!) // Chamada da função para armazenar a foto no Firebase Storage
                    avatar = uriFotoAluno.toString()

                    uploadTask.addOnCompleteListener { resultUpload ->

                        if (resultUpload.isSuccessful) {

                            resultUpload.result?.storage?.downloadUrl?.addOnCompleteListener {resultDownloadUrl ->

                                if(resultDownloadUrl.isSuccessful){

                                    avatar = resultDownloadUrl.result.toString()
                                    val aluno = Aluno(
                                        id,
                                        txtEditNomeAluno.text.toString(),
                                        txtEditSobrenomeAluno.text.toString(),
                                        txtEditIdadeAluno.text.toString().toInt(),
                                        avatar
                                    )
                                    val task = Database().incluir(
                                        "users",
                                        id,
                                        aluno
                                    ) //chamada do método incluir da classe Datase

                                    task.addOnCompleteListener { result ->
                                        //quando a tarefa for concluída, executará o código a seguir

                                        if (result.isSuccessful) {//se a tarefa for com sucesso não exibir a progressBar, mostrar mensagem e limpar os campos
                                            progressBar.visibility = View.GONE
                                            showMessage(this, "Aluno cadastro com sucesso!")
                                            clearFields()
                                        } else {
                                            progressBar.visibility =
                                                View.GONE //se a tarefa não for concluída com sucesso, a progressBar também não será exibida e será mostrada uma mensagem para o usuário
                                            showMessage(this, "Houve erro no cadastro do Aluno")
                                        }
                                    }
                                }else{
                                    showMessage(this, "Houve erro ao fazer o download da URL da foto!")
                                }
                            }

                        } else{
                            progressBar.visibility = View.GONE
                            showMessage(this, "Houve erro no cadastro do Aluno (envio da foto)")
                        }
                    }
                } else{

                    val aluno = Aluno(
                        id,
                        txtEditNomeAluno.text.toString(),
                        txtEditSobrenomeAluno.text.toString(),
                        txtEditIdadeAluno.text.toString().toInt(),
                        avatar
                    )

                    val task = Database().incluir(
                        "users",
                        id,
                        aluno
                    ) //chamada do método incluir da classe Datase

                    task.addOnCompleteListener { result ->
                        //quando a tarefa for concluída, executará o código a seguir

                        if (result.isSuccessful) {//se a tarefa for com sucesso não exibir a progressBar, mostrar mensagem e limpar os campos
                            progressBar.visibility = View.GONE
                            showMessage(this, "Aluno cadastro com sucesso!")
                            clearFields()
                        } else {
                            progressBar.visibility =
                                View.GONE //se a tarefa não for concluída com sucesso, a progressBar também não será exibida e será mostrada uma mensagem para o usuário
                            showMessage(this, "Houve erro no cadastro do Aluno")
                        }
                    }
                }
                //se não houver conexão com a internet, exibe mensagem.
            } else {
                showMessage(this,"Sem conexão com a internet")
            }
        }
    }

   //Limpa os camos do formulário e dá foco no primeiro campo
    private fun clearFields() {
        txtEditNomeAluno.text!!.clear()
        txtEditSobrenomeAluno.text!!.clear()
        txtEditIdadeAluno.text!!.clear()
        txtEditNomeAluno.requestFocus()
       imgFoto.setImageResource(R.drawable.take_a_photo)
    }

    //Função para verificar se a entrada do usuário é válida, caso não, será exibida uma mensagem de erro e o usuário não poderá prosseguir com o cadastro
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

    //Função que chama a activity da camera e já define um nome padrão para o arquivo de foto tirada.
    private fun takePhoto() {

        val dateTime = SimpleDateFormat("ddMMyyyy_hhmmss")
        val fileName = "img_" + (dateTime.format(Date()).toString())
        file = getPathExternal(fileName)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uriFotoAluno = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", file!!)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoAluno)
        startActivityForResult(intent, REQUEST_COD_PHOTO)
    }

    //Função para capturar o caminho externo do arquivo informado
    private fun getPathExternal(filename:String) :File?{

        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (path != null){

            path.mkdir()
        }
        val file = File(path, filename)
        return file
    }

    /*Função que será executada quando uma outra Activity retornar  para esta.
      Neste caso, para as chamadas de activities da Galeria ou da Camera.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_COD_PHOTO && resultCode == Activity.RESULT_OK){
            val resultImage = getSuitableImage(file)
            showImage(resultImage)

        }else if (requestCode == REQUEST_COD_GALLERY && resultCode == Activity.RESULT_OK){
            if (data != null) {
                uriFotoAluno = data.data
                val resultImage = getImageGallery(uriFotoAluno)
                showImage(resultImage)
            }
        }
    }

    //Função para a partir de uma URL recuperar o Bitmap de uma imagem
    private fun getImageGallery(uriPhotoGallery: Uri?) :Bitmap {

        return MediaStore.Images.Media.getBitmap(contentResolver, uriPhotoGallery)

    }

    //Função para redimensionar o tamanho da foto conforme o espaço que ela vai ocupar na tela
    private fun getSuitableImage(file: File?) : Bitmap? {

        if (file != null && file.exists()) {

            val w = imgFoto.width
            val h = imgFoto.height
            return ImageUtils.resize(file, w, h)
        }
        return null
    }

    //Função para mostrar a imagem da camera ou galeria na tela
    private fun showImage(bitmap: Bitmap?) {

        if (bitmap != null) imgFoto.setImageBitmap(bitmap)
    }


}
