package ca.hojat.samples.assimp

import org.lwjgl.assimp.Assimp.aiGetCompileFlags
import org.lwjgl.assimp.Assimp.aiGetExportFormatCount
import org.lwjgl.assimp.Assimp.aiGetExportFormatDescription
import org.lwjgl.assimp.Assimp.aiGetImportFormatCount
import org.lwjgl.assimp.Assimp.aiGetImportFormatDescription
import org.lwjgl.assimp.Assimp.aiGetLegalString
import org.lwjgl.assimp.Assimp.aiGetVersionMajor
import org.lwjgl.assimp.Assimp.aiGetVersionMinor
import org.lwjgl.assimp.Assimp.aiGetVersionRevision

fun main() {

    println(aiGetLegalString())
    println("aiGetVersionMajor() = ${aiGetVersionMajor()}")
    println("aiGetVersionMinor() = ${aiGetVersionMinor()}")
    println("aiGetVersionRevision() = ${aiGetVersionRevision()}")
    println("aiGetCompileFlags() = ${aiGetCompileFlags()}")

    println("\nImport formats:")

    for (i in 0..<aiGetImportFormatCount()) {
        val description = aiGetImportFormatDescription(i)!!
        println("\t${i + 1}. ${description.mNameString()} (${description.mFileExtensionsString()})")
    }

    println("\nExport formats:")

    for (i in 0..<aiGetExportFormatCount()) {
        val desc = aiGetExportFormatDescription(i)!!
        println("\t${i + 1}. ${desc.descriptionString()} (${desc.fileExtensionString()})")
    }
}