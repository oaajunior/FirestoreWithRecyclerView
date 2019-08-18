package livroandroid.com.recyclerviewexample.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import livroandroid.com.recyclerviewexample.database.Database
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lista_usuarios.view.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main.view.progressBar
import kotlinx.android.synthetic.main.listaview.view.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.entity.Aluno
import java.util.ArrayList

class MyAdapter(private val myDataset: ArrayList<Aluno>, private val progressBarLista: ProgressBar):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var contexto :Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.listaview, parent, false)
        contexto = parent.context
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.firstName.text = myDataset[position].nome
        holder.lastName.text = myDataset[position].sobrenome
        holder.avatarImage.setImageResource( myDataset[position].avatar)
        holder.idade.text = myDataset[position].idade.toString()

        //Caso o usuário clique no ícone para deletar um elemento da lista
        holder.icDelete.setOnClickListener {

            progressBarLista.visibility = View.VISIBLE
            val result = Database().delete("users", myDataset[position].id  )

            if (result != null){

                result.addOnSuccessListener {

                    progressBarLista.visibility = View.GONE
                    myDataset.removeAt(position)
                    this.notifyItemRemoved(position)
                    Toast.makeText(contexto, "Aluno removido com sucesso!", Toast.LENGTH_LONG).show()

                }.addOnFailureListener {

                    progressBarLista.visibility = View.GONE
                    Toast.makeText(contexto, "Houve erro na deleção do registro!", Toast.LENGTH_LONG).show()
                    Log.d("APPERROR3", it.message)
                }
            }else{
                progressBarLista.visibility = View.GONE
                Toast.makeText(contexto, "Registro não existe no servidor!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount() : Int{
        return myDataset.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var firstName = itemView.txtName
        var lastName = itemView.txtLastName
        var avatarImage = itemView.imageView
        var lista = itemView.listaPessoa
        var idade = itemView.txtIdade
        var icEdit = itemView.ic_edit
        var icDelete = itemView.ic_delete

    }
}