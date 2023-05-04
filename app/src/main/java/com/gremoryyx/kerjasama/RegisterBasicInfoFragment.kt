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

    private var listener: OnBasicInfoFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_basic_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the gender spinner
        val genders = listOf("Gender", "Male", "Female")
        val adapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // append the adapter to the spinner
        val spinner = view.findViewById<Spinner>(R.id.gender_spinner)
        spinner.adapter = adapter

        // Set up the date picker
        val datePickerButton = view.findViewById<Button>(R.id.date_picker_button)
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

    }

    // Interface for sending data to the activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBasicInfoFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    // function that send data to the activity
    fun sendDataToActivity(): Bundle {
        val data = Bundle()

        val nameEditText: EditText = requireView().findViewById(R.id.name_as_ic_register)
        val icNumberEditText: EditText = requireView().findViewById(R.id.ic_number_register)
        val genderSpinner: Spinner = requireView().findViewById(R.id.gender_spinner)
        val dateOfBirthButton: Button = requireView().findViewById(R.id.date_picker_button)
        val phoneNumberEditText: EditText = requireView().findViewById(R.id.phone_number_register)

        data.putString("name", nameEditText.text.toString())
        data.putString("ic_number", icNumberEditText.text.toString())
        data.putString("gender", genderSpinner.selectedItem.toString())
        data.putString("date_of_birth", dateOfBirthButton.text.toString())
        data.putString("phone_number", phoneNumberEditText.text.toString())

        return data
    }

    // Destroys the listener
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // Show date picker dialog
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
            // Set button text color to primary color
            view?.findViewById<com.google.android.material.button.MaterialButton>(R.id.date_picker_button)?.setTextColor(Color.argb(255, 51, 51, 51))
        }

        datePickerDialog.show()
    }



    // Pass to activity interface
    interface OnBasicInfoFragmentInteractionListener {
        fun onBasicInfoFragmentInteraction(data: Bundle)
    }

}

// Class function that takes in a list of strings and returns a custom ArrayAdapter
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as TextView

        if (position == 0) {
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.argb(255, 51, 51, 51))
        }

        // Set the color of the selected item
        if (position == (parent as Spinner).selectedItemPosition && position != 0) {
            textView.setTextColor(Color.argb(255, 51, 51, 51))
        }

        return view
    }

}
