package Tut1_Hello_Triangle

import framework.*
import kotlinx.cinterop.*
import libgl.*
import libglut.*

class Tutorial1: ITutorial {


    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }

    //Called whenever the window is resized. The new window size is given, in pixels.
//This is an opportunity to call glViewport or glScissor to keep up with the change in size.
    override fun reshape(w: Int, h: Int) {
        glViewport(0, 0, w, h)
    }

    override fun getWindowTitle(): String {
        return "Cap 1, First triangle"
    }



    //Called whenever a key on the keyboard was pressed.
//The key is given by the ''key'' parameter, which is in ASCII.
//It's often a good idea to have the escape key (ASCII value 27) call glutLeaveMainLoop() to
//exit the program.
    override fun keyboard(window: Int?, key: UByte, x: Int, y: Int)
    {
        when(key) {
            27.toUByte() -> {
                if(window != null) {
                    glutDestroyWindow(window)
                }

                return
            }

        }
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
                glGetShaderiv!!(shader, GL_INFO_LOG_LENGTH.toUInt(), it)
            }

            val strInfoLog = allocArray<GLcharVar>(infoLogLength)
            glGetShaderInfoLog!!(shader, infoLogLength, null, strInfoLog.getPointer(memScope))
            val error = strInfoLog.getPointer(memScope).toKString()
            val shaderTypeString = shaderTypeToString(shaderType)
            println("Compile failure in shader:\n$shaderTypeString \n$error")
        }
    }

    fun createShader(eShaderType: GLenum, strShaderFile: String): GLuint {

        val shader = glCreateShader!!(eShaderType)

        memScoped {
            val glVersion = glGetString(GL_VERSION)!!.toKString()
            println("Gl version: $glVersion")

            val strShaderFilePointer = strShaderFile.cstr.getPointer(memScope)

            val strFileData = listOf(strShaderFilePointer).toCValues().getPointer(MemScope())

            glShaderSource!!(shader, 1, strFileData, null)

            glCompileShader!!(shader)

            val statusPointer = alloc<IntVarOf<Int>>().apply { value = 0 }
            glGetShaderiv!!(shader, GL_COMPILE_STATUS.toUInt(), statusPointer.ptr)
            handleCreateShaderCompileIssue(
                    eShaderType,
                    shader,
                    statusPointer.value
            )

        }
        return shader
    }

    fun handleCreateProgramIssue(
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

    fun createProgram(shaderList: List<GLuint>): GLuint {
        val program = glCreateProgram!!();
        shaderList.forEach { shader ->
            glAttachShader!!(program, shader)
        }

        glLinkProgram!!(program)

        val status = readIntValue {
            glGetProgramiv!!(program, GL_LINK_STATUS.toUInt(), it)
        }
        handleCreateProgramIssue(program, status)
        shaderList.forEach { shader ->
            glDetachShader!!(program, shader)
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

        val glVertexShader = createShader(GL_VERTEX_SHADER.toUInt(), strVertexShader)
        val glFragmentShader = createShader(GL_FRAGMENT_SHADER.toUInt(), strFragmentShader)

        val shaderList = listOf(glVertexShader, glFragmentShader)

        println("The shaders $shaderList were created")

        theProgram = createProgram(shaderList)

        shaderList.forEach { shader ->
            glDeleteShader!!(shader)
        }
        println("The program was well created $theProgram")
    }


    val vertexPositions = cValuesOf(
            0.75f, 0.75f, 0.0f, 1.0f,
            0.75f, -0.75f, 0.0f, 1.0f,
            -0.75f, -0.75f, 0.0f, 1.0f,
    )


    var positionBufferObject: GLuint = 0.toUInt()
    var vao: GLuint = 0.toUInt()

    fun initializeVertexBuffer() {
        positionBufferObject = readUIntValue {
            glGenBuffers!!(1, it)
        }
        val glArrayBuffer = GL_ARRAY_BUFFER.toUInt()
        glBindBuffer!!(glArrayBuffer, positionBufferObject)
        memScoped {
            val vertexPositionsPointer = vertexPositions.getPointer(memScope)
            val vertexPositionsSize = vertexPositions.size.toLong()
            glBufferData!!(glArrayBuffer, vertexPositionsSize, vertexPositionsPointer, GL_STATIC_DRAW.toUInt())
        }

        glBindBuffer!!(glArrayBuffer, 0.toUInt())
    }

    //Called after the window and OpenGL are initialized. Called exactly once, before the Tut_02_Playing_with_Colors.main loop.
    override fun init() {
        initializeProgram()
        initializeVertexBuffer()

        vao = readUIntValue {
            glGenVertexArrays!!(1, it)
        }


        glBindVertexArray!!(vao)
    }

    //Called to update the display.
//You should call glutSwapBuffers after all of your rendering to display what you rendered.
//If you need continuous updates of the screen, call glutPostRedisplay() at the end of the function.
    override fun display() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram!!(theProgram)
        glBindBuffer!!(GL_ARRAY_BUFFER.toUInt(), positionBufferObject)
        glEnableVertexAttribArray!!(0.toUInt())
        glVertexAttribPointer!!(0.toUInt(), 4, GL_FLOAT.toUInt(), GL_FALSE.toUByte(), 0, null)

        glDrawArrays(GL_TRIANGLES, 0, 3)
        glDisableVertexAttribArray!!(0.toUInt())
        glUseProgram!!(0.toUInt())
        glutSwapBuffers()
    }

}