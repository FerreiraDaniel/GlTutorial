package gl_wrapper

import framework.readIntValue
import framework.toKString
import kotlinx.cinterop.*
import libgl.GLcharVar
import libgl.GLuintVar

class GLWrapper: IGLWrapper {
    private inline fun generateListOfObject(numberOfObjects: Int, glFunction: (CPointer<UIntVarOf<UInt>>) -> Unit ): List<UInt> {
        memScoped {
            val objects = allocArray<GLuintVar>(numberOfObjects)
            val pointer = objects.getPointer(memScope)
            glFunction(pointer)
            return (0 until numberOfObjects).mapIndexed { index: Int, _: Int ->
                objects[index]
            }
        }
    }

    override fun glGenVertexArrays(numberOfObjects: Int): List<UInt> {
        return generateListOfObject(numberOfObjects) {
            cPointer -> libgl.glGenVertexArrays!!(numberOfObjects, cPointer)
        }
    }

    override fun glBindVertexArray(array: UInt) = libgl.glBindVertexArray!!(array)

    override fun glGetShaderiv(shader: GLShader, pName: Int): Int = readIntValue {
        val pNameUInt = pName.toUInt()
        libgl.glGetShaderiv!!(shader, pNameUInt, it)
    }

    override fun glViewport(x: Int, y: Int, width: Int, height: Int) =
        libgl.glViewport(x, y, width, height)

    override fun glGetShaderInfoLog(shader: GLShader, maxLength: Int): String {
        memScoped {
            val strInfoLog = allocArray<GLcharVar>(maxLength)
            libgl.glGetShaderInfoLog!!(shader, maxLength, null, strInfoLog.getPointer(memScope))
            return strInfoLog.getPointer(memScope).toKString()
        }
    }

    override fun glCreateShader(shaderType: Int): GLShader {
        val shaderTypeGL = shaderType.getGlEnum()
        return libgl.glCreateShader!!(shaderTypeGL)
    }

    override fun glGetString(name: Int): String {
        val stringType = name.getGlEnum()
        return libgl.glGetString(stringType)!!.toKString()

    }

    override fun glShaderSource(shader: GLShader, string: String) {
        memScoped {
            val strShaderFilePointer = string.cstr.getPointer(memScope)
            val strFileData = listOf(strShaderFilePointer).toCValues().getPointer(MemScope())
            libgl.glShaderSource!!(shader, 1, strFileData, null)
        }
    }

    override fun glCompileShader(shader: GLShader) = libgl.glCompileShader!!(shader)

    override fun glDeleteShader(shader: GLShader) = libgl.glDeleteShader!!(shader)

    override fun glCreateProgram(): GLProgram = libgl.glCreateProgram!!()

    override fun glGetProgramiv(program: GLProgram, pName: Int) =
        readIntValue {
            val pNameGL = pName.getGlEnum()
            libgl.glGetProgramiv!!(program, pNameGL, it)
        }

    override fun glGetProgramInfoLog(program: GLProgram, maxLength: Int): String {
        memScoped {
            val strInfoLog = allocArray<GLcharVar>(maxLength)
            libgl.glGetProgramInfoLog!!(program, maxLength, null, strInfoLog.getPointer(memScope))
            return strInfoLog.getPointer(memScope).toKString()
        }
    }

    override fun glAttachShader(program: GLProgram, shader: GLShader) = libgl.glAttachShader!!(program, shader)

    override fun glLinkProgram(program: GLProgram) = libgl.glLinkProgram!!(program)


    override fun glUseProgram(program: GLProgram) = libgl.glUseProgram!!(program)

    override fun glUseProgram(program: Int) = glUseProgram(program.toUInt())

    override fun glDetachShader(program: GLProgram, shader: GLShader) = libgl.glDetachShader!!(program, shader)


    override fun glGenBuffers(n: Int): List<UInt> {
        return generateListOfObject(n) {
                cPointer -> libgl.glGenBuffers!!(n, cPointer)
        }
    }

    override fun glBindBuffer(target: Int, buffer: UInt) {
        val glTarget = target.getGlEnum()
        libgl.glBindBuffer!!(glTarget, buffer)
    }

    override fun glBindBuffer(target: Int, buffer: Int) = glBindBuffer(target, buffer.toUInt())

    override fun glBufferData(target: Int, data: CValues<FloatVarOf<Float>>, usage: Int) {
        memScoped {
            val dataPointer = data.getPointer(memScope)
            val dataSize = data.size.toLong()
            libgl.glBufferData!!(target.getGlEnum(), dataSize, dataPointer, usage.getGlEnum())
        }
    }

    override fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) = libgl.glClearColor(
        red,
        green,
        blue,
        alpha
        )

    override fun glClear(mask: UInt) = libgl.glClear(mask)

    override fun glClear(mask: Int) = glClear(mask.toUInt())



    override fun glEnableVertexAttribArray(index: UInt) = libgl.glEnableVertexAttribArray!!(index)

    override fun glEnableVertexAttribArray(index: Int) = glEnableVertexAttribArray(index.toUInt())


    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Int,
        stride: Int,
        pointer: COpaquePointer?
    ) {
        libgl.glVertexAttribPointer!!(index.toUInt(),
            size,
            type.toUInt(),
            normalized.toUByte(),
            stride,
            pointer
            )
    }

    override fun glDrawArrays(mode: Int, first: Int, count: Int) {
        libgl.glDrawArrays(mode.toUInt(), first, count)
    }

    override fun glDisableVertexAttribArray(index: Int) {
        libgl.glDisableVertexAttribArray!!(index.toUInt())
    }

    override fun glUniform1f(location: Int, v0: Float) = libgl.glUniform1f!!(location, v0)

    override fun glUniform2f(location: Int, v0: Float, v1: Float) = libgl.glUniform2f!!(location, v0, v1)

    override fun glGetUniformLocation(program: UInt, name: String): Int {
        memScoped {
            val offsetCPointer = name.cstr.getPointer(memScope)
            return libgl.glGetUniformLocation!!(program, offsetCPointer)
        }
    }

    override fun glBufferSubData(target: Int, offset: Long, size: Long, data: CPointer<FloatVarOf<Float>>) {
        libgl.glBufferSubData!!(target.toUInt(), offset, size, data)
    }

    override fun glEnable(glCullFace: Int) {
        libgl.glEnable(glCullFace.toUInt())
    }

    override fun glCullFace(mode: Int) {
        libgl.glCullFace(mode.toUInt())
    }

    override fun glFrontFace(mode: Int) {
        libgl.glFrontFace(mode.toUInt())
    }
}