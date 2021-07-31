package gl_wrapper

import framework.readIntValue
import framework.toKString
import kotlinx.cinterop.*
import platform.OpenGL3.glGenVertexArrays
import platform.OpenGLCommon.*


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
            cPointer -> glGenVertexArrays(numberOfObjects, cPointer)
        }
    }

    override fun glBindVertexArray(array: UInt) = platform.OpenGL3.glBindVertexArray(array)

    override fun glGetShaderiv(shader: GLShader, pName: Int): Int = readIntValue {
        val pNameUInt = pName.toUInt()
        platform.OpenGL3.glGetShaderiv(shader, pNameUInt, it)
    }

    override fun glViewport(x: Int, y: Int, width: Int, height: Int) =
        platform.OpenGL3.glViewport(x, y, width, height)

    override fun glGetShaderInfoLog(shader: GLShader, maxLength: Int): String {
        memScoped {
            val strInfoLog = allocArray<GLcharVar>(maxLength)
            platform.OpenGL3.glGetShaderInfoLog(shader, maxLength, null, strInfoLog.getPointer(memScope))
            return strInfoLog.getPointer(memScope).toKString()
        }
    }

    override fun glCreateShader(shaderType: Int): GLShader {
        val shaderTypeGL = shaderType.getGlEnum()
        return platform.OpenGL3.glCreateShader(shaderTypeGL)
    }

    override fun glGetString(name: Int): String {
        val stringType = name.getGlEnum()
        return platform.OpenGL3.glGetString(stringType)!!.toKString()

    }

    override fun glShaderSource(shader: GLShader, string: String) {
        memScoped {
            val strShaderFilePointer = string.cstr.getPointer(memScope)
            val strFileData = listOf(strShaderFilePointer).toCValues().getPointer(MemScope())
            platform.OpenGL3.glShaderSource(shader, 1, strFileData, null)
        }
    }

    override fun glCompileShader(shader: GLShader) = platform.OpenGL3.glCompileShader(shader)

    override fun glDeleteShader(shader: GLShader) = platform.OpenGL3.glDeleteShader(shader)

    override fun glCreateProgram(): GLProgram = platform.OpenGL3.glCreateProgram()

    override fun glGetProgramiv(program: GLProgram, pName: Int) =
        readIntValue {
            val pNameGL = pName.getGlEnum()
            platform.OpenGL3.glGetProgramiv(program, pNameGL, it)
        }

    override fun glGetProgramInfoLog(program: GLProgram, maxLength: Int): String {
        memScoped {
            val strInfoLog = allocArray<GLcharVar>(maxLength)
            platform.OpenGL3.glGetProgramInfoLog(program, maxLength, null, strInfoLog.getPointer(memScope))
            return strInfoLog.getPointer(memScope).toKString()
        }
    }

    override fun glAttachShader(program: GLProgram, shader: GLShader) = platform.OpenGL3.glAttachShader(program, shader)

    override fun glLinkProgram(program: GLProgram) = platform.OpenGL3.glLinkProgram(program)


    override fun glUseProgram(program: GLProgram) = platform.OpenGL3.glUseProgram(program)

    override fun glUseProgram(program: Int) = glUseProgram(program.toUInt())

    override fun glDetachShader(program: GLProgram, shader: GLShader) = platform.OpenGL3.glDetachShader(program, shader)


    override fun glGenBuffers(n: Int): List<UInt> {
        return generateListOfObject(n) {
                cPointer -> platform.OpenGL3.glGenBuffers(n, cPointer)
        }
    }

    override fun glBindBuffer(target: Int, buffer: UInt) {
        val glTarget = target.getGlEnum()
        platform.OpenGL3.glBindBuffer(glTarget, buffer)
    }

    override fun glBindBuffer(target: Int, buffer: Int) = glBindBuffer(target, buffer.toUInt())

    override fun glBufferData(target: Int, data: CValues<FloatVarOf<Float>>, usage: Int) {
        memScoped {
            val dataPointer = data.getPointer(memScope)
            val dataSize = data.size.toLong()
            platform.OpenGL3.glBufferData(target.getGlEnum(), dataSize, dataPointer, usage.getGlEnum())
        }
    }

    override fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) = platform.OpenGL3.glClearColor(
        red,
        green,
        blue,
        alpha
        )

    override fun glClear(mask: UInt) = platform.OpenGL3.glClear(mask)

    override fun glClear(mask: Int) = glClear(mask.toUInt())



    override fun glEnableVertexAttribArray(index: UInt) = platform.OpenGL3.glEnableVertexAttribArray(index)

    override fun glEnableVertexAttribArray(index: Int) = glEnableVertexAttribArray(index.toUInt())


    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Int,
        stride: Int,
        pointer: COpaquePointer?
    ) {
        platform.OpenGL3.glVertexAttribPointer(index.toUInt(),
            size,
            type.toUInt(),
            normalized.toUByte(),
            stride,
            pointer
            )
    }

    override fun glDrawArrays(mode: Int, first: Int, count: Int) {
        platform.OpenGL3.glDrawArrays(mode.toUInt(), first, count)
    }

    override fun glDisableVertexAttribArray(index: Int) {
        platform.OpenGL3.glDisableVertexAttribArray(index.toUInt())
    }

    override fun glUniform1f(location: Int, v0: Float) = platform.OpenGL3.glUniform1f(location, v0)

    override fun glUniform2f(location: Int, v0: Float, v1: Float) = platform.OpenGL3.glUniform2f(location, v0, v1)

    override fun glGetUniformLocation(program: UInt, name: String): Int {
        return platform.OpenGL3.glGetUniformLocation(program, name)
    }

    override fun glBufferSubData(target: Int, offset: Long, size: Long, data: CPointer<FloatVarOf<Float>>) {
        platform.OpenGL3.glBufferSubData(target.toUInt(), offset, size, data)
    }

    override fun glEnable(glCullFace: Int) {
        platform.OpenGL3.glEnable(glCullFace.toUInt())
    }

    override fun glCullFace(mode: Int) {
        platform.OpenGL3.glCullFace(mode.toUInt())
    }

    override fun glFrontFace(mode: Int) {
        platform.OpenGL3.glFrontFace(mode.toUInt())
    }
}