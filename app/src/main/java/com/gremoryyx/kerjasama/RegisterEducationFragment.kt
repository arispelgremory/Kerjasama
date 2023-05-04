package com.gremoryyx.kerjasama

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

class RegisterEducationFragment : Fragment() {

    private var listener: OnEducationFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_register_education,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the highest qualifications spinner
        val qualifications = listOf("Highest Qualifications", "No Qualifications", "UPSR", "PMR/PT3", "SPM", "STPM", "Diploma", "Degree", "Master", "PhD")
        val adapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, qualifications)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // append the adapter to the spinner
        val spinner = view.findViewById<Spinner>(R.id.highest_qualifications_spinner)
        spinner.adapter = adapter

    }

    // Interface for sending data to the activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEducationFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    // function that send data to the activity
    fun sendDataToActivity(): Bundle {
        val data = Bundle()

        val highestQualificationsSpinner: Spinner = requireView().findViewById(R.id.highest_qualifications_spinner)
        data.putString("highest_qualifications", highestQualificationsSpinner.selectedItem.toString())
        return data
    }

    // Destroys the listener
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // Pass to activity interface
    interface OnEducationFragmentInteractionListener {
        fun onEducationFragmentInteraction(data: Bundle)
    }

}