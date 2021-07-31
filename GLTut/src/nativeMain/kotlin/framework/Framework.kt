package framework


import framework.FileUtil.readAllText
import kotlinx.cinterop.*
import platform.GLUT.*
import platform.OpenGL3.*
import platform.OpenGLCommon.*

private var currentTutorial: ITutorial? = null
private var currentWindow: Int? = null

private fun display() {
    currentTutorial?.display()
}

//Called whenever the window is resized. The new window size is given, in pixels.
//This is an opportunity to call glViewport or glScissor to keep up with the change in size.
private fun reshape(w: Int, h: Int) {
    currentTutorial?.reshape(w, h)
}

private fun keyboard(key: UByte, x: Int, y: Int) {
    println("keyboard key: $key x: $x y: $y")
    currentTutorial?.keyboard(currentWindow, key, x, y)
    glutDestroyWindow(x)
}

class Framework: IFramework {

    private fun registerCallBacks(tutorial: ITutorial, window: Int) {
        currentTutorial = tutorial
        currentWindow = window

        glutDisplayFunc(staticCFunction(::display))
        glutReshapeFunc(staticCFunction(::reshape))
        glutKeyboardFunc(staticCFunction(::keyboard))
    }

    private fun initializeWindow(tutorial: ITutorial): Int {
        memScoped {
            val argc = alloc<IntVar>().apply { value = 0 }
            glutInit(argc.ptr, null)
        }

        // Display Mode
        val width = 500
        val height = 500
        var displayMode = GLUT_3_2_CORE_PROFILE or GLUT_DOUBLE or GLUT_ALPHA or GLUT_DEPTH or GLUT_STENCIL
        displayMode = tutorial.defaults(displayMode, width, height)

        glutInitDisplayMode(displayMode.convert())
        glutInitWindowSize(width, height)
        glutInitWindowPosition(300, 200)


        // create Window
        return glutCreateWindow(tutorial.getWindowTitle())
    }

    fun launchTutorial(tutorial: ITutorial) {
        val window = initializeWindow(tutorial)

        tutorial.init(this)

        registerCallBacks(tutorial, window)

        glutMainLoop()
    }

        private fun shaderTypeToString(shaderType: GLenum): String {
            return when (shaderType) {
                GL_VERTEX_SHADER.toUInt() -> "vertex"
                GL_GEOMETRY_SHADER.toUInt() -> "geometry"
                GL_FRAGMENT_SHADER.toUInt() -> "fragment"
                else -> ""
            }
        }


        private fun handleCreateShaderCompileIssue(
            shaderType: GLenum,
            shader: GLuint,
            compilationStatus: Int
        ) {
            if (compilationStatus != GL_FALSE) {
                return
            }
            memScoped {
                val infoLogLength = readIntValue {
                    glGetShaderiv(shader, GL_INFO_LOG_LENGTH.toUInt(), it)
                }

                val strInfoLog = allocArray<GLcharVar>(infoLogLength)
                glGetShaderInfoLog(shader, infoLogLength, null, strInfoLog.getPointer(memScope))
                val error = strInfoLog.getPointer(memScope).toKString()
                val shaderTypeString = shaderTypeToString(shaderType)
                println("Compile failure in shader:\n$shaderTypeString \n$error")
            }
        }

        private fun createShader(eShaderType: GLenum, strShaderFile: String): GLuint {

            val shader = glCreateShader(eShaderType)

            memScoped {
                val glVersion = glGetString(GL_VERSION)?.toKString()
                if(glVersion != null) {
                    println("Gl version: $glVersion")
                }


                val strShaderFilePointer = strShaderFile.cstr.getPointer(memScope)

                val strFileData = listOf(strShaderFilePointer).toCValues().getPointer(MemScope())

                glShaderSource(shader, 1, strFileData, null)

                glCompileShader(shader)

                val statusPointer = alloc<IntVarOf<Int>>().apply { value = 0 }
                glGetShaderiv(shader, GL_COMPILE_STATUS.toUInt(), statusPointer.ptr)
                handleCreateShaderCompileIssue(
                    eShaderType,
                    shader,
                    statusPointer.value
                )

            }
            return shader
        }


        private fun loadShader(eShaderType: GLenum, filePathList: List<String>): GLuint {
            val shader = readAllText(filePathList)
            return createShader(eShaderType, shader)
        }

        override fun loadShader(eShaderType: Int, filePathList: List<String>) =
            loadShader(eShaderType.toUInt(), filePathList)

        override fun createProgram(shaderList: List<GLuint>): GLuint {
            val program = GLUtil.linkProgram(shaderList)
            shaderList.forEach { shader ->
                glDetachShader(program, shader)
            }

            return program
        }


}