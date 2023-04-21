package com.gremoryyx.kerjasama

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*


class RegisterBasicInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_basic_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf("Gender", "Male", "Female")
        val adapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = view.findViewById<Spinner>(R.id.gender_spinner)
        spinner.adapter = adapter

        val datePickerButton = view.findViewById<Button>(R.id.date_picker_button)
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

    }

    private var listener: OnFragmentInteractionListener? = null
    private var mListener: RegisterBasicInfoFragment.OnFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun sendDataToActivity() {
        val data = Bundle()
        data.putString("name", "John Doe") // Replace with the actual data from the input fields
        // Add other data as needed
        listener?.onFragmentInteraction(data)
    }



    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val button: Button = requireView().findViewById(R.id.date_picker_button)
            button.text = "${dayOfMonth}/${month + 1}/$year"
        }

        val datePickerDialog = DatePickerDialog(requireContext(), onDateSetListener, year, month, day)

        datePickerDialog.setOnShowListener {
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.argb(255, 51, 51, 51))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.argb(255, 237, 45, 45))
        }

        datePickerDialog.show()
    }



    // Pass to activity interface
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(data: Bundle)
    }

}

class CustomArrayAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun isEnabled(position: Int): Boolean {
        // Disable the first item, make other items selectable
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)

        // Change the appearance of the first item to indicate it's not selectable
        val textView = view as TextView
        if (position == 0) {
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.argb(255, 51, 51, 51))
        }
        return view
    }
}
