package com.example.nerevian.core.model.documents

import com.example.nerevian.core.model.business.offer.Offer
import com.example.nerevian.core.model.operations.Operation
import com.example.nerevian.core.model.user.User
import java.time.LocalDate


data class Document (
    val id: Int,
    var fileName: String,
    var originalFileName: String,
    var documentType: DocumentType,
    var filePath: String,
    var weight: Double,
    var offer: Offer,
    var operation: Operation,
    var realeasedBy: User,
    var realesedDate: LocalDate
    )