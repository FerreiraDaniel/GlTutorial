package tut_04_objects_at_rest_otho_cube

import framework.*
import gl_wrapper.IGLWrapper
import kotlinx.cinterop.*
import libgl.*
import libglut.*

@ExperimentalUnsignedTypes
class OrthoCube(private val glWrapper: IGLWrapper) : ITutorial {
    private val resourcesFolderName = "resources"
    private val folderName = "Tut 04 Objects at Rest"
    private val subFolderName = "data"
    private val vertexShaderFileName = "OrthoWithOffset.vert"
    private val fragmentShader = "StandardColors.frag"

    private var theProgram: UInt = 0.toUInt()
    private var offsetUniform: Int = 0

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

        offsetUniform = glWrapper.glGetUniformLocation(theProgram, "offsetUniform")
    }

    private val vertexData = cValuesOf(
        0.25f,  0.25f, 0.75f, 1.0f,
        0.25f, -0.25f, 0.75f, 1.0f,
        -0.25f,  0.25f, 0.75f, 1.0f,

        0.25f, -0.25f, 0.75f, 1.0f,
        -0.25f, -0.25f, 0.75f, 1.0f,
        -0.25f,  0.25f, 0.75f, 1.0f,

        0.25f,  0.25f, -0.75f, 1.0f,
        -0.25f,  0.25f, -0.75f, 1.0f,
        0.25f, -0.25f, -0.75f, 1.0f,

        0.25f, -0.25f, -0.75f, 1.0f,
        -0.25f,  0.25f, -0.75f, 1.0f,
        -0.25f, -0.25f, -0.75f, 1.0f,

        -0.25f,  0.25f,  0.75f, 1.0f,
        -0.25f, -0.25f,  0.75f, 1.0f,
        -0.25f, -0.25f, -0.75f, 1.0f,

        -0.25f,  0.25f,  0.75f, 1.0f,
        -0.25f, -0.25f, -0.75f, 1.0f,
        -0.25f,  0.25f, -0.75f, 1.0f,

        0.25f,  0.25f,  0.75f, 1.0f,
        0.25f, -0.25f, -0.75f, 1.0f,
        0.25f, -0.25f,  0.75f, 1.0f,

        0.25f,  0.25f,  0.75f, 1.0f,
        0.25f,  0.25f, -0.75f, 1.0f,
        0.25f, -0.25f, -0.75f, 1.0f,

        0.25f,  0.25f, -0.75f, 1.0f,
        0.25f,  0.25f,  0.75f, 1.0f,
        -0.25f,  0.25f,  0.75f, 1.0f,

        0.25f,  0.25f, -0.75f, 1.0f,
        -0.25f,  0.25f,  0.75f, 1.0f,
        -0.25f,  0.25f, -0.75f, 1.0f,

        0.25f, -0.25f, -0.75f, 1.0f,
        -0.25f, -0.25f,  0.75f, 1.0f,
        0.25f, -0.25f,  0.75f, 1.0f,

        0.25f, -0.25f, -0.75f, 1.0f,
        -0.25f, -0.25f, -0.75f, 1.0f,
        -0.25f, -0.25f,  0.75f, 1.0f,
    )


    private val colorData = cValuesOf(

        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,

        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,

        0.8f, 0.8f, 0.8f, 1.0f,
        0.8f, 0.8f, 0.8f, 1.0f,
        0.8f, 0.8f, 0.8f, 1.0f,

        0.8f, 0.8f, 0.8f, 1.0f,
        0.8f, 0.8f, 0.8f, 1.0f,
        0.8f, 0.8f, 0.8f, 1.0f,

        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,

        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,

        0.5f, 0.5f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.0f, 1.0f,

        0.5f, 0.5f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.0f, 1.0f,

        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,

        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,

        0.0f, 1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f, 1.0f,

        0.0f, 1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f, 1.0f
    )


    private var vertexBufferObject: GLuint = 0.toUInt()
    private var colorBufferObject: GLuint = 0.toUInt()


    private var vao: GLuint = 0.toUInt()

    private fun initializeVertexBuffer(vertexData: CValues<FloatVar>): UInt {
        val vertexBufferObjects = glWrapper.glGenBuffers(1)
        val vertexBufferObject = vertexBufferObjects[0]

        val glArrayBuffer = GL_ARRAY_BUFFER
        glWrapper.glBindBuffer(glArrayBuffer, vertexBufferObject)
        glWrapper.glBufferData(glArrayBuffer, vertexData, GL_STATIC_DRAW)
        glWrapper.glBindBuffer(glArrayBuffer, 0)

        return vertexBufferObject
    }

    private fun initializeVertexBuffers() {
        vertexBufferObject = initializeVertexBuffer(vertexData)
        colorBufferObject = initializeVertexBuffer(colorData)
    }


    //Called after the window and OpenGL are initialized. Called exactly once, before the main loop.
    override fun init(framework: IFramework) {
        initializeProgram(framework)
        initializeVertexBuffers()

        val vaos = glWrapper.glGenVertexArrays(1)
        vao = vaos[0]
        glWrapper.glBindVertexArray(vao)

        glWrapper.glEnable(GL_CULL_FACE)
        glWrapper.glCullFace(GL_BACK)
        glWrapper.glFrontFace(GL_CW)
    }




    //Called to update the display.
    //You should call glutSwapBuffers after all of your rendering to display what you rendered.
    //If you need continuous updates of the screen, call glutPostRedisplay() at the end of the function.
    override fun display()  {
        glWrapper.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glWrapper.glClear(GL_COLOR_BUFFER_BIT)
        glWrapper.glUseProgram(theProgram)


        glWrapper.glUniform2f(offsetUniform, 0.5f, 0.25f)

        //Position 0
        glWrapper.glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject)
        glWrapper.glEnableVertexAttribArray(0)
        glWrapper.glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 0, null)

        //Position 1
        glWrapper.glBindBuffer(GL_ARRAY_BUFFER, colorBufferObject)
        glWrapper.glEnableVertexAttribArray(1)
        glWrapper.glVertexAttribPointer(1, 4, GL_FLOAT, GL_FALSE, 0, null)


        glWrapper.glDrawArrays(GL_TRIANGLES, 0, 36)

        glWrapper.glDisableVertexAttribArray(0)
        glWrapper.glDisableVertexAttribArray(1)
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
        return "Tutorial 04, Objects at rest. Orthogonal cube"
    }

    override fun defaults(displayMode: Int, width: Int, height: Int): Int {
        return displayMode
    }
}

