package aasmc.ru.playground

import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer

/**
 * Default size for reading buffers.
 */
const val DEFAULT_CHUNK_SIZE = 1024

fun copy(
    inputStream: InputStream,
    outputStream: OutputStream
): Long {
    return copy(inputStream, outputStream, DEFAULT_CHUNK_SIZE)
}

fun copy(
    inputStream: InputStream,
    outputStream: OutputStream,
    bufferSize: Int
): Long {
    val buffer = ByteArray(bufferSize)
    var count = 0L
    var n = inputStream.read(buffer)
    while (-1 != n) {
        outputStream.write(buffer, 0, n)
        count += n
        n = inputStream.read(buffer)
    }
    return count
}

fun copy(
    reader: Reader,
    writer: Writer
): Long {
    return copy(reader, writer, DEFAULT_CHUNK_SIZE)
}

fun copy(
    reader: Reader,
    writer: Writer,
    bufferSize: Int
): Long {
    val buffer = CharArray(bufferSize)
    var count = 0L
    var n = reader.read(buffer)
    while (-1 != n) {
        writer.write(buffer, 0, n)
        count += n
        n = reader.read(buffer)
    }
    return count
}

















