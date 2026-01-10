package com.example.demo.helpers

import com.intellij.psi.PsiDirectory

fun PsiDirectory.createFileIfNotExists(
    fileName: String,
    content: String
): com.intellij.psi.PsiFile {
    val existing = findFile(fileName)
    if (existing != null) return existing
    val file = createFile(fileName)
    file.viewProvider.document?.setText(content.trimIndent())
    return file
}
