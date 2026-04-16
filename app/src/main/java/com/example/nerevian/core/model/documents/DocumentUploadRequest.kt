package com.example.nerevian.core.model.documents

data class DocumentUploadRequest(
    val fileName: String,
    val originalName: String,
    val typeId: Int,
    val path: String,
    val weight: String,
    val userId: Int
)