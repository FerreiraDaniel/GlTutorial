package tut_02_playing_with_colors

import framework.*
import gl_wrapper.IGLWrapper
import kotlinx.cinterop.*
import libgl.*
import libglut.*

@ExperimentalUnsignedTypes
class FragPosition(private val glWrapper: IGLWrapper) : ITutorial {
    private val resourcesFolderName = "resources"
    private val folderName = "Tut 02 Playing with Colors"
    private val subFolderName = "data"
    private val vertexShaderFileName = "FragPosition.vert"
    private val fragmentShader = "FragPosition.frag"

    var theProgram: UInt = 0.toUInt()
    var elapsedTimeUniform: UInt = 0.toUInt()

    private fun initializeProgram(framework: IFramework) {
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

        val glVertexShader = framework.loadShader(GL_VERTEX_SHADER, vertexShaderFilePath)
        val glFragmentShader = framework.loadShader(GL_FRAGMENT_SHADER, fragmentShaderFilePath)

        val shaderList = listOf(glVertexShader, glFragmentShader)

        println("The shaders $shaderList were created")

        theProgram = framework.createProgram(shaderList)

        println("The program was well created $theProgram")
    }

    private val vertexData = cValuesOf(
        0.75f, 0.75f, 0.0f, 1.0f,
        0.75f, -0.75f, 0.0f, 1.0f,
        -0.75f, -0.75f, 0.0f, 1.0f,
    )


    var positionBufferObject: UInt = 0.toUInt()
    var vao: UInt = 0.toUInt()

    private fun initializeVertexBuffer() {
        val positionBufferObjects = glWrapper.glGenBuffers(1)
        positionBufferObject = positionBufferObjects[0]
        val glArrayBuffer = GL_ARRAY_BUFFER
        glWrapper.glBindBuffer(glArrayBuffer, positionBufferObject)
        glWrapper.glBufferData(glArrayBuffer, vertexData, GL_STATIC_DRAW)

        glWrapper.glBindBuffer(glArrayBuffer, 0.toUInt())
    }

    //Called after the window and OpenGL are initialized. Called exactly once, before the main loop.
    override fun init(framework: IFramework) {
        initializeProgram(framework)
        initializeVertexBuffer()

        val vaos = glWrapper.glGenVertexArrays(1)
        vao = vaos[0]


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
        glWrapper.glEnableVertexAttribArray(0.toUInt())
        glWrapper.glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 0, null)

        glWrapper.glDrawArrays(GL_TRIANGLES, 0, 3)
        glWrapper.glDisableVertexAttribArray(0)
        glWrapper.glUseProgram(0.toUInt())
        glutSwapBuffers()
    }

    //Called whenever the window is resized. The new window size is given, in pixels.
//This is an opportunity to call glViewport or glScissor to keep up with the change in size.
    override fun reshape(w: Int, h: Int) {
        glWrapper.glViewport(0, 0, w, h)
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
        return "Cap 2, Fragment Position Display"
    }

    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }
}

