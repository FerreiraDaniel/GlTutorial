package framework

import platform.OpenGLCommon.GLuint


interface IFramework {
    fun loadShader(eShaderType: Int, filePathList: List<String>): GLuint

    fun createProgram(shaderList: List<GLuint>): GLuint
}