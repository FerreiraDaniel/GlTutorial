package framework

import kotlinx.cinterop.*
import libgl.GLcharVar
import libgl.GLubyteVar

fun CPointer<GLubyteVar>.toKString(): String {
    val nativeString = this

    var index = 0

    while (nativeString[index] != UByte.MIN_VALUE) {
        index += 1
    }
    val length = index + 1

    memScoped {
        val strInfoLog = allocArray<GLcharVar>(length)
        index = 0
        while (nativeString[index] != UByte.MIN_VALUE) {
            strInfoLog[index] = nativeString[index].toByte()
            index += 1
        }
        return strInfoLog.getPointer(memScope).toKString()
    }
}

fun readIntValue(readerFunction: (CPointer<IntVarOf<Int>>) -> Unit): Int {
    memScoped {
        val integer = alloc<IntVarOf<Int>>().apply { value = 0 }
        readerFunction(integer.ptr)
        return integer.value
    }
}

fun readUIntValue(readerFunction: (CPointer<UIntVarOf<UInt>>) -> Unit): UInt {
    memScoped {
        val integer = alloc<UIntVarOf<UInt>>().apply { value = 0.toUInt() }
        readerFunction(integer.ptr)
        return integer.value
    }
}