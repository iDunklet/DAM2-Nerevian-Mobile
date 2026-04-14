package com.example.nerevian.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R

class DocsFragment : Fragment(R.layout.fragment_document) {

    private lateinit var docsAdapter: DocsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvDocs = view.findViewById<RecyclerView>(R.id.rvDocs)

        rvDocs.layoutManager = LinearLayoutManager(requireContext())

        val mockDocsData = listOf(

            DocListItem.Header("EXP-8821", "SHA -> VLC"),
            DocListItem.File("Bill of Lading", "Disponible", "3.4 MB", true),
            DocListItem.File("Factura Comercial", "Disponible", "1.2 MB", true),

            DocListItem.Header("EXP-8822", "NGB -> BCN"),
            DocListItem.File("Packing List", "Pendiente", "0 KB", false),
            DocListItem.File("Certificado de Origen", "Pendiente", "0 KB", false),

            DocListItem.Header("EXP-8823", "QIN -> MAD"),
            DocListItem.File("DUA de Exportación", "Disponible", "2.1 MB", true),
            DocListItem.File("Seguro de Carga", "Pendiente", "0 KB", false)
        )


        docsAdapter = DocsAdapter(mockDocsData)
        rvDocs.adapter = docsAdapter
    }
}