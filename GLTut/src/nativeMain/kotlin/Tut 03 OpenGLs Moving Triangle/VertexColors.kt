package tut_02_playing_with_colors_vertex_colors

import framework.*
import kotlinx.cinterop.*
import libgl.*
import libglut.*

@ExperimentalUnsignedTypes
class VertexColors : ITutorial {
    private val resourcesFolderName = "resources"
    private val folderName = "Tut 02 Playing with Colors"
    private val subFolderName = "data"
    private val vertexShaderFileName = "VertexColors.vert"
    private val fragmentShader = "VertexColors.frag"

    var theProgram: GLuint = 0.toUInt()

    private fun initializeProgram() {
        glewInit()

        val vertexShaderFilePath = listOf(
            resourcesFolderName,
            folderName,
            subFolderName,
            vertexShaderFileName
        )

        val fragmentShaderFilePath = listOf(
            resourcesFolderName,
            folderName,
            subFolderName,
            fragmentShader
        )

        val glVertexShader = Framework.loadShader(GL_VERTEX_SHADER.toUInt(), vertexShaderFilePath)
        val glFragmentShader = Framework.loadShader(GL_FRAGMENT_SHADER.toUInt(), fragmentShaderFilePath)

        val shaderList = listOf(glVertexShader, glFragmentShader)


        theProgram = Framework.createProgram(shaderList)

    }

    private val vertexData = cValuesOf(
        0.0f, 0.5f, 0.0f, 1.0f,
        0.5f, -0.366f, 0.0f, 1.0f,
        -0.5f, -0.366f, 0.0f, 1.0f
    )

    private val vertexData2 = cValuesOf(
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    )


    private var vertexBufferObject: GLuint = 0.toUInt()
    private var vertexBufferObject2: GLuint = 0.toUInt()
    private var vao: GLuint = 0.toUInt()

    private fun initializeVertexBuffer(vertexData: CValues<FloatVar>): GLuint {
        var vertexBufferObject = readUIntValue {
            glGenBuffers!!(1, it)
        }
        val glArrayBuffer = GL_ARRAY_BUFFER.toUInt()
        glBindBuffer!!(glArrayBuffer, vertexBufferObject)
        memScoped {
            val vertexDataPointer = vertexData.getPointer(memScope)
            val vertexDataSize =vertexData.size.toLong()
            glBufferData!!(glArrayBuffer, vertexDataSize, vertexDataPointer, GL_STATIC_DRAW.toUInt())
        }

        glBindBuffer!!(glArrayBuffer, 0.toUInt())

        return vertexBufferObject
    }

    private fun initializeVertexBuffers() {
        vertexBufferObject = initializeVertexBuffer(vertexData)
        vertexBufferObject2 = initializeVertexBuffer(vertexData2)
    }

    //Called after the window and OpenGL are initialized. Called exactly once, before the main loop.
    override fun init() {
        initializeProgram()
        initializeVertexBuffers()

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

        //Position 0
        glBindBuffer!!(GL_ARRAY_BUFFER.toUInt(), vertexBufferObject)
        glEnableVertexAttribArray!!(0.toUInt())
        glVertexAttribPointer!!(0.toUInt(), 4, GL_FLOAT.toUInt(), GL_FALSE.toUByte(), 0, null)

        //Position 1
        glBindBuffer!!(GL_ARRAY_BUFFER.toUInt(), vertexBufferObject2)
        glEnableVertexAttribArray!!(1.toUInt())
        glVertexAttribPointer!!(1.toUInt(), 4, GL_FLOAT.toUInt(), GL_FALSE.toUByte(), 0, null)


        glDrawArrays(GL_TRIANGLES, 0, 3)




        glDisableVertexAttribArray!!(0.toUInt())
        glDisableVertexAttribArray!!(1.toUInt())
        glUseProgram!!(0.toUInt())
        glutSwapBuffers()
    }

    //Called whenever the window is resized. The new window size is given, in pixels.
//This is an opportunity to call glViewport or glScissor to keep up with the change in size.
    override fun reshape(w: Int, h: Int) {
        glViewport(0, 0, w, h)
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


    override fun getWindowTitle(): String {
        return "Tut 02 Playing with Colors, vertex colors"
    }

    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }
}

