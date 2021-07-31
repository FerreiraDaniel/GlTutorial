package tut_03_opengls_moving_triangle

import framework.*
import gl_wrapper.IGLWrapper
import kotlinx.cinterop.*
import libglew.glewInit
import platform.GLUT.*
import platform.OpenGL3.*
import platform.OpenGLCommon.*
import platform.posix.cosf
import platform.posix.fmodf
import platform.posix.sinf

@ExperimentalUnsignedTypes
class CpuPositionOffset(private val glWrapper: IGLWrapper) : ITutorial {
    private val resourcesFolderName = "resources"
    private val folderName = "Tut 03 OpenGLs Moving Triangle"
    private val subFolderName = "data"
    private val vertexShaderFileName = "standard.vert"
    private val fragmentShader = "standard.frag"

    var theProgram: GLuint = 0.toUInt()

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


    private fun computePositionOffsets(): Pair<Float, Float>
    {
        val fLoopDuration = 5.0f
        val fScale = 3.14159f * 2.0f / fLoopDuration

        val fElapsedTime = glutGet(GLUT_ELAPSED_TIME) / 1000.0f

        val fCurrTimeThroughLoop = fmodf(fElapsedTime, fLoopDuration)

        val xOffset = cosf(fCurrTimeThroughLoop * fScale) * 0.5f
        val yOffset = sinf(fCurrTimeThroughLoop * fScale) * 0.5f

        return Pair(xOffset, yOffset)
    }

    private fun adjustVertexData(fXOffset: Float, fYOffset: Float)
    {
        memScoped {
            val fNewData = allocArray<FloatVar>(vertexPositions.size)
            val pointer = vertexPositions.getPointer(memScope)
            (0 until vertexPositions.size).forEach { index ->
                    fNewData[index] = pointer[index]
            }
                var iVertex = 0
                while (iVertex < vertexPositions.size) {
                    fNewData[iVertex] += fXOffset
                    fNewData[iVertex + 1] += fYOffset
                    iVertex += 4
                }
            val glArrayBuffer = GL_ARRAY_BUFFER
            glWrapper.glBindBuffer(glArrayBuffer, positionBufferObject)

            val vertexDataSize = vertexPositions.size.toLong()
            glWrapper.glBufferSubData(glArrayBuffer, 0, vertexDataSize, fNewData.getPointer(memScope))
            glWrapper.glBindBuffer(glArrayBuffer, 0.toUInt())
        }


    }

    //Called to update the display.
    //You should call glutSwapBuffers after all of your rendering to display what you rendered.
    //If you need continuous updates of the screen, call glutPostRedisplay() at the end of the function.
    override fun display() {
        val (fXOffset, fYOffset) = computePositionOffsets()
        adjustVertexData(fXOffset, fYOffset)

        glWrapper.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glWrapper.glClear(GL_COLOR_BUFFER_BIT)
        glWrapper.glUseProgram(theProgram)

        //Position 0
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
        return "Tut 03 opengl moving triangle"
    }

    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }
}

