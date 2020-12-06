package tut1_hello_triangle

import framework.*
import gl_wrapper.*
import kotlinx.cinterop.*
import libgl.*
import libglut.*

class Tutorial1(private val glWrapper: IGLWrapper) : ITutorial {


    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }

    //Called whenever the window is resized. The new window size is given, in pixels.
    //This is an opportunity to call glViewport or glScissor to keep up with the change in size.
    override fun reshape(w: Int, h: Int) {
        glWrapper.glViewport(0, 0, w, h)
    }


    override fun getWindowTitle(): String {
        return "Cap 1, First triangle"
    }


    //Called whenever a key on the keyboard was pressed.
    //The key is given by the ''key'' parameter, which is in ASCII.
    //It's often a good idea to have the escape key (ASCII value 27) call glutLeaveMainLoop() to
    //exit the program.
    override fun keyboard(window: Int?, key: UByte, x: Int, y: Int) {
        when (key) {
            KeyboardKeys.Esc.value -> {
                if (window != null) {
                    glutDestroyWindow(window)
                }

                return
            }

        }
    }

    private fun shaderTypeToString(shaderType: Int): String {
        return when (shaderType) {
            GL_VERTEX_SHADER -> "vertex"
            GL_GEOMETRY_SHADER -> "geometry"
            GL_FRAGMENT_SHADER -> "fragment"
            else -> ""
        }
    }


    private fun handleCreateShaderCompileIssue(
        glWrapper: IGLWrapper,
        shaderType: Int,
        shader: GLuint,
        compilationStatus: Int
    ) {
        if (compilationStatus != GL_FALSE) {
            return
        }

        val infoLogLength = glWrapper.glGetShaderiv(shader, GL_INFO_LOG_LENGTH)

        val error = glWrapper.glGetShaderInfoLog(shader, infoLogLength)
        val shaderTypeString = shaderTypeToString(shaderType)
        println("Compile failure in shader:\n$shaderTypeString \n$error")
    }

    private fun createShader(glWrapper: IGLWrapper, eShaderType: Int, strShaderFile: String): GLuint {

        val shader = glWrapper.glCreateShader(eShaderType)

        memScoped {
            val glVersion = glWrapper.glGetString(GL_VERSION)
            println("Gl version: $glVersion")


            glWrapper.glShaderSource(shader, strShaderFile)

            glWrapper.glCompileShader(shader)

            val compileStatus = glWrapper.glGetShaderiv(shader, GL_COMPILE_STATUS)
            handleCreateShaderCompileIssue(
                glWrapper,
                eShaderType,
                shader,
                compileStatus
            )

        }
        return shader
    }

    private fun handleCreateProgramIssue(
        program: GLuint,
        status: Int
    ) {
        if (status != GL_FALSE) {
            return
        }
        val infoLogLength = glWrapper.glGetProgramiv(program, GL_INFO_LOG_LENGTH)
        val error = glWrapper.glGetProgramInfoLog(program, infoLogLength)
        println("Linker failure: $error\n")
    }

    private fun createProgram(shaderList: List<GLuint>): GLuint {
        val program = glWrapper.glCreateProgram()
        shaderList.forEach { shader ->
            glWrapper.glAttachShader(program, shader)
        }

        glWrapper.glLinkProgram(program)


        val status = glWrapper.glGetProgramiv(program, GL_LINK_STATUS)
        handleCreateProgramIssue(program, status)
        shaderList.forEach { shader ->
            glWrapper.glDetachShader(program, shader)
        }

        return program
    }

    var theProgram: GLuint = 0.toUInt()

    private val strVertexShader =
        "#version 330\n" +
                "layout(location = 0) in vec4 position;\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = position;\n" +
                "}\n"

    private val strFragmentShader =
        "#version 330\n" +
                "out vec4 outputColor;\n" +
                "void main()\n" +
                "{\n" +
                "   outputColor = vec4(1.0f, 1.0f, 1.0f, 0.0f);\n" +
                "}\n"

    private fun initializeProgram() {
        glewInit()

        val glVertexShader = createShader(glWrapper, GL_VERTEX_SHADER, strVertexShader)
        val glFragmentShader = createShader(glWrapper, GL_FRAGMENT_SHADER, strFragmentShader)

        val shaderList = listOf(glVertexShader, glFragmentShader)

        println("The shaders $shaderList were created")

        theProgram = createProgram(shaderList)

        shaderList.forEach { shader ->
            glWrapper.glDeleteShader(shader)
        }
        println("The program was well created $theProgram")
    }


    private val vertexPositions = cValuesOf(
        0.75f, 0.75f, 0.0f, 1.0f,
        0.75f, -0.75f, 0.0f, 1.0f,
        -0.75f, -0.75f, 0.0f, 1.0f,
    )


    var positionBufferObject: GLuint = 0.toUInt()
    var vao: GLuint = 0.toUInt()

    private fun initializeVertexBuffer() {
        val positionBufferObjects: List<UInt> = glWrapper.glGenBuffers(1)
        positionBufferObject = positionBufferObjects[0]

        val glArrayBuffer = GL_ARRAY_BUFFER
        glWrapper.glBindBuffer(glArrayBuffer, positionBufferObject)
        glWrapper.glBufferData(glArrayBuffer, vertexPositions, GL_STATIC_DRAW)
        glWrapper.glBindBuffer(glArrayBuffer, 0)
    }

    //Called after the window and OpenGL are initialized. Called exactly once, before the Tut_02_Playing_with_Colors.main loop.
    override fun init(framework: IFramework) {
        initializeProgram()
        initializeVertexBuffer()

        var vertexArray = glWrapper.glGenVertexArrays(1)
        vao = vertexArray[0]

        glWrapper.glBindVertexArray(vao)
    }

    //Called to update the display.
    //You should call glutSwapBuffers after all of your rendering to display what you rendered.
    //If you need continuous updates of the screen, call glutPostRedisplay() at the end of the function.
    override fun display() {
        glWrapper.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glWrapper.glClear(GL_COLOR_BUFFER_BIT)
        glWrapper.glUseProgram(theProgram)
        glWrapper.glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject)
        glWrapper.glEnableVertexAttribArray(0)
        glWrapper.glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 0, null)

        glWrapper.glDrawArrays(GL_TRIANGLES, 0, 3)
        glWrapper.glDisableVertexAttribArray(0)
        glWrapper.glUseProgram(0)
        glutSwapBuffers()
    }

}