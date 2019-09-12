package livroandroid.com.recyclerviewexample.adapter


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.listaview.view.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.entity.Aluno
import java.util.ArrayList

//Classe para montar o RecyclerViewAdapter
class MyAdapter(private val myDataset: ArrayList<Aluno>, private val callback:(Int, String)->Unit) :RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    lateinit var contexto: Context
    //Função para inflar o layout da lista criada no RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.listaview, parent, false)
        contexto = parent.context
        return MyViewHolder(view)
    }

    //Função para fazer o bind dos elementos da lista com os itens da RecyclerView (lista visual)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.firstName.text = myDataset[position].nome
        holder.lastName.text = myDataset[position].sobrenome
        holder.idade.text = myDataset[position].idade.toString()

        //Uso do Glide para exibir a imagem do aluno
        if (myDataset[position].avatar != ""){

            Glide.with(contexto)
                .load(Uri.parse(myDataset[position].avatar))
                .into(holder.avatarImage)
        }

        //Listener para ficar escutando caso o usuário clique no ícone para deletar um elemento da lista
        holder.icDelete.setOnClickListener {

            callback(position, "remover")
        }

        //Listener para ficar escutando caso o usuário clique no ícone para editar um elemento da lista
        holder.icEdit.setOnClickListener {

            callback(position, "editar")
        }
    }

    //Função para retornar o tamanho da lista
    override fun getItemCount() : Int{
        return myDataset.size
    }

    //Classe interna para criação do ViewHolder
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var firstName = itemView.txtName
        var lastName = itemView.txtLastName
        var avatarImage = itemView.imageView
        var idade = itemView.txtIdade
        var icEdit = itemView.ic_edit
        var icDelete = itemView.ic_delete
    }

}