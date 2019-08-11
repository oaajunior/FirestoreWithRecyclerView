package livroandroid.com.recyclerviewexample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listaview.view.*
import livroandroid.com.recyclerviewexample.R
import livroandroid.com.recyclerviewexample.entity.Aluno
import java.util.ArrayList

class MyAdapter(private val myDataset: ArrayList<Aluno>):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var contexto : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.listaview, parent, false)
        contexto = parent.context
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.firstName.text = myDataset[position].nome
        holder.lastName.text = myDataset[position].sobrenome
        holder.avatarImage.setImageResource( myDataset[position].avatar)

        holder.lista.setOnClickListener {
            var pos = position + 1
            Toast.makeText(contexto,"cliquei aqui $pos ", Toast.LENGTH_LONG).show()
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

    }
}