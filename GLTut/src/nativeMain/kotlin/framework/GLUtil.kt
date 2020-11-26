package framework

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import libgl.*

object GLUtil {
    private fun handleCreateProgramIssue(
        program: GLuint,
        status: Int
    ) {
        if (status != GL_FALSE) {
            return
        }
        memScoped {
            val infoLogLength = readIntValue {
                glGetProgramiv!!(program, GL_INFO_LOG_LENGTH.toUInt(), it)
            }

            val strInfoLog = allocArray<GLcharVar>(infoLogLength)
            glGetProgramInfoLog!!(program, infoLogLength, null, strInfoLog.getPointer(memScope))
            val error = strInfoLog.getPointer(memScope).toKString()
            println("Linker failure: $error\n")
        }
    }

    fun linkProgram(shaderList: List<GLuint>): GLuint {
        val program = glCreateProgram!!()
        shaderList.forEach { shader ->
            glAttachShader!!(program, shader)
        }

        glLinkProgram!!(program)

        val status = readIntValue {
            glGetProgramiv!!(program, GL_LINK_STATUS.toUInt(), it)
        }
        handleCreateProgramIssue(program, status)

        return program
    }
}