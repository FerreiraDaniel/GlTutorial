package tut_03_opengl_is_moving_triangle_multiple_shaders

import framework.*
import gl_wrapper.*
import kotlinx.cinterop.*
import libglew.glewInit
import platform.GLUT.*
import platform.OpenGL3.*
import platform.OpenGLCommon.*

@ExperimentalUnsignedTypes
class FragChangeColor(private val glWrapper: IGLWrapper) : ITutorial {
    private val resourcesFolderName = "resources"
    private val folderName = "Tut 03 OpenGLs Moving Triangle"
    private val subFolderName = "data"
    private val vertexShaderFileName = "calcOffset.vert"
    private val fragmentShader = "calcColor.frag"

    var theProgram: GLuint = 0.toUInt()
    var elapsedTimeUniform: GLint = 0

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


        theProgram = framework.createProgram(shaderList)

        elapsedTimeUniform = glWrapper.glGetUniformLocation(theProgram, "time")

        val loopDurationUnf = glWrapper.glGetUniformLocation(theProgram, "loopDuration")
        val fragLoopDurUnf = glWrapper.glGetUniformLocation(theProgram, "fragLoopDuration")

        glWrapper.glUseProgram(theProgram)
        glWrapper.glUniform1f(loopDurationUnf, 5.0f)
        glWrapper.glUniform1f(fragLoopDurUnf, 10.0f)
        glWrapper.glUseProgram(0)
    }

    private val vertexPositions = cValuesOf(
        0.0f, 0.5f, 0.0f, 1.0f,
        0.5f, -0.366f, 0.0f, 1.0f,
        -0.5f, -0.366f, 0.0f, 1.0f
    )


    private var positionBufferObject: GLuint = 0.toUInt()
    private var vao: GLuint = 0.toUInt()

    private fun initializeVertexBuffer() {
        val positionBufferObjects = glWrapper.glGenBuffers(1)
        positionBufferObject = positionBufferObjects[0]
        val glArrayBuffer = GL_ARRAY_BUFFER
        glWrapper.glBindBuffer(glArrayBuffer, positionBufferObject)
        glWrapper.glBufferData(glArrayBuffer, vertexPositions, GL_STATIC_DRAW)

        glWrapper.glBindBuffer(glArrayBuffer, 0)
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
    override fun display()  {
        glWrapper.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glWrapper.glClear(GL_COLOR_BUFFER_BIT)
        glWrapper.glUseProgram(theProgram)

        val elapsedTime = glutGet(GLUT_ELAPSED_TIME) / 1000.0f
        glWrapper.glUniform1f(elapsedTimeUniform, elapsedTime)

        glWrapper.glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject)
        glWrapper.glEnableVertexAttribArray(0)
        glWrapper.glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 0, null)

        glWrapper.glDrawArrays(GL_TRIANGLES, 0, 3)

        glWrapper.glDisableVertexAttribArray(0)
        glWrapper.glUseProgram(0)
        glutSwapBuffers()
        glutPostRedisplay()
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
        return "Tut 03 opengl moving triangle, More Power To The Shaders"
    }

    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }
}

