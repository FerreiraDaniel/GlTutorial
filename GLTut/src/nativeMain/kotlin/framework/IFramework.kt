package framework

import libgl.GLenum
import libgl.GLuint

interface IFramework {
    fun loadShader(eShaderType: Int, filePathList: List<String>): GLuint

    fun createProgram(shaderList: List<GLuint>): GLuint
}