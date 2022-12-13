package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment: Fragment() {

    //callback интерфейс
    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    //Свойство, позволяющее удерживать объект, реализующий интерфейс Callbacks
    private var callbacks: Callbacks? = null


    //Подключаем ViewModel
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView

        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        crimeRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer{crimes->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }

            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Разрешаем данному фрагменту получение обратных вызовов menu через fragmentManager
        setHasOptionsMenu(true)
    }

    //Функция, которая связывает адаптер с данными и с утилизатором
    private fun updateUI(crimes: List<Crime>){
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    //Создание CrimeHolder, который будет отрисовывать в RecyclerView отдельные item из
    //list_item_crime.xml
    //inner class - вложенный класс, который имеет доступ до элементов внешнего класса
    private inner class SimpleCrimeHolder(view: View):RecyclerView.ViewHolder(view),
    View.OnClickListener{
        lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val crimeImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        //Назначаем слушателя нажатий на весь CrimeHolder
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.getDateInstance().format(this.crime.date)
            crimeImageView.visibility = when(crime.isSolved){
                true -> View.VISIBLE
                false -> View.GONE
            }
        }

        //РЕализация функции oneClick интерфейса View.OnClickListener
        override fun onClick(p0: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    //Холдер для серьезных преступлений
    private inner class SeriousCrimeHolder(view: View):RecyclerView.ViewHolder(view),
        View.OnClickListener{
        lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.serious_crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.serious_crime_date)

        //Назначаем слушателя нажатий на весь CrimeHolder
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }

        //РЕализация функции oneClick интерфейса View.OnClickListener
        override fun onClick(p0: View?) {
            Toast.makeText(context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
        }
    }


    //Адаптер для создания и работы над CrimeHolder
    private inner class CrimeAdapter(var crimes: List<Crime>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        //Функция, создающая CrimeHolder и связывающая его с представлением item
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)

            return SimpleCrimeHolder(view)
//            return when(viewType){
//                R.layout.list_item_crime -> SimpleCrimeHolder(view)
//
//                else -> SeriousCrimeHolder(view)
//            }


        }

        //Заполнение холдера данными из модели
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val crime = crimes[position]

            when(holder){
                is SimpleCrimeHolder -> holder.bind(crime)
                is SeriousCrimeHolder -> holder.bind(crime)
            }

        }

//        override fun getItemViewType(position: Int): Int {
//            return when(crimesData[position].requiredPolice){
//                0 -> R.layout.list_item_crime
//
//                else -> R.layout.list_item_serious_crime
//            }
//        }

        // Возвращает кол-во items
        override fun getItemCount() = crimes.size

    }

    //Функции обратного вызова для создания меню приложения (в Активити та же функция)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    //Функция обратного вызова, реагирующая на нажатие item у menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                return true
            }
            else-> return super.onOptionsItemSelected(item)
        }


    }


    //Функции для установки и отмены свойства callbacks
    //Вызывается при вызове fragment через mainActivity
    //context в данном случае - вызывающая activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    //Функция, возвращающая экземпляр класа при вызове из mainActivity
    companion object{
        fun newInstance(): CrimeListFragment{
            return CrimeListFragment()
        }
    }
}