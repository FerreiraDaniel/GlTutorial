package gl_wrapper

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValues
import kotlinx.cinterop.FloatVarOf

typealias GLProgram = UInt
typealias GLShader = UInt

interface IGLWrapper {

    /**
     * generate vertex array object names
     *
     * @param numberOfObjects Specifies the number of vertex array object names to generate.
     */
    fun glGenVertexArrays(numberOfObjects: Int): List<UInt>


    /**
     * bind a vertex array object
     *
     * @param array Specifies the name of the vertex array to bind.
     */
    fun glBindVertexArray(array: UInt)


    /**
     *  Return a parameter from a shader object
     *
     * @param shader Specifies the shader object to be queried.
     * @param pName Specifies the object parameter.
     *              Accepted symbolic names are
     *                  GL_SHADER_TYPE,
     *                  GL_DELETE_STATUS,
     *                  GL_COMPILE_STATUS,
     *                  GL_INFO_LOG_LENGTH,
     *                  GL_SHADER_SOURCE_LENGTH.
     *
     */
    fun glGetShaderiv(shader: GLShader, pName: Int): Int


    /**
     * set the viewport
     *
     * @param x, y Specify the lower left corner of the viewport rectangle, in pixels. The initial value is (0,0).
     * @param width, height Specify the width and height of the viewport.
     *              When a GL context is first attached to a window,
     *              width and height are set to the dimensions of that window.
     */
    fun glViewport(x:Int,
                   y:Int,
                   width:Int,
                   height:Int)


    /**
     *  Returns the information log for a shader object
     *
     * @param shader Specifies the shader object whose information log is to be queried.
     * @param maxLength Specifies the size of the character buffer for storing the returned information log.
     */
    fun glGetShaderInfoLog(shader: GLShader, maxLength: Int): String

    /**
     * Creates an empty shader object and returns a non-zero value by which it can be referenced.
     * A shader object is used to maintain the source code strings that define a shader.
     *
     * @param shaderType indicates the type of shader to be created.
     */
    fun glCreateShader(shaderType: Int): GLShader

    /**
     *
     * @param name Specifies a symbolic constant
     *
     * @return a string describing the current GL connection
     */
    fun glGetString(name: Int): String


    /**
     *  Replaces the source code in a shader object
     *
     *  @param shader Specifies the handle of the shader object whose source code is to be replaced.
     *  @param string Specifies an array of pointers to strings containing the source code to be loaded into the shader.
     */
    fun glShaderSource(shader: GLShader,
                       string: String)

    /**
     * Compiles a shader object
     *
     * @param shader Specifies the shader object to be compiled.
     */
    fun glCompileShader(shader: GLShader)

    /**
     *  frees the memory and invalidates the name associated with the shader object specified by shader.
     *  This command effectively undoes the effects of a call to glCreateShader.
     *
     *  If a shader object to be deleted is attached to a program object, it will be flagged for deletion,
     *  but it will not be deleted until it is no longer attached to any program object,
     *  for any rendering context (i.e., it must be detached from wherever it was attached before it will be deleted).
     *
     *  @param shader Specifies the shader object to be deleted.
     */
    fun glDeleteShader(shader: GLShader)

    /**
     *  creates an empty program object and returns a non-zero value by which it can be referenced.
     *  A program object is an object to which shader objects can be attached.
     *  This provides a mechanism to specify the shader objects that will be linked to create a program.
     *  It also provides a means for checking the compatibility of the shaders that will be used to create a program
     *  (for instance, checking the compatibility between a vertex shader and a fragment shader).
     *  When no longer needed as part of a program object, shader objects can be detached.
     */
    fun glCreateProgram(): GLProgram

    /**
     *
     * @param program Specifies the program object to be queried.
     * @param pName Specifies the object parameter.
     *
     * @return a parameter from a program object
     */
    fun glGetProgramiv(program: GLProgram, pName: Int): Int


    /**
     *  @param program Specifies the program object whose information log is to be queried.
     *  @param maxLength Specifies the size of the character buffer for storing the returned information log.
     *
     * @return the information log for a program object
     */
    fun glGetProgramInfoLog(program: GLProgram, maxLength: Int): String


    /**
     * In order to create a complete shader program,
     * there must be a way to specify the list of things that will be linked together.
     * Program objects provide this mechanism.
     * Shaders that are to be linked together in a program object must first be attached to that program object.
     *
     * @param program Specifies the program object to which a shader object will be attached.
     * @param shader Specifies the shader object that is to be attached.
     */
    fun glAttachShader(program: GLProgram, shader: GLShader)



    /**
     *  glLinkProgram links the program object specified by program.
     *  If any shader objects are attached to program,
     *  they will be used to create an executable that will run on the programmable processor.
     *
     *  @param program Specifies the handle of the program object to be linked.
     */
    fun glLinkProgram(program: GLProgram)

    /**
     *  Installs a program object as part of current rendering state
     *
     *  @param  program Specifies the handle of the program object
     */
    fun glUseProgram(program: GLProgram)

    /**
     *  Installs a program object as part of current rendering state
     *
     *  @param  program Specifies the handle of the program object
     */
    fun glUseProgram(program: Int)

    /**
     * Detaches a shader object from a program object to which it is attached
     *
     * @param program Specifies the program object from which to detach the shader object.
     * @param shader Specifies the shader object to be detached.
     */
    fun glDetachShader(program: GLProgram, shader: GLShader)

    /**
     *  generate buffer object names
     *
     *  @param n Specifies the number of buffer object names to be generated.
     */
    fun glGenBuffers(n: Int): List<UInt>


    /**
     * bind a named buffer object
     *
     * @param target Specifies the target to which the buffer object is bound
     * @param buffer Specifies the name of a buffer object.
     */
    fun glBindBuffer(target: Int, buffer: UInt)

    /**
     * bind a named buffer object
     *
     * @param target Specifies the target to which the buffer object is bound
     * @param buffer Specifies the name of a buffer object.
     */
    fun glBindBuffer(target: Int, buffer: Int)

    /**
     *  Creates and initializes a buffer object's data store
     *
     *  @param target Specifies the target to which the buffer object is bound for glBufferData
     *  @param data Specifies the data that will be copied into the data store for initialization
     *  @param usage Specifies the expected usage pattern of the data store
     */
    fun glBufferData(target: Int,
                     data: CValues<FloatVarOf<Float>>,
                     usage: Int)


    /**
     * specify clear values for the color buffers
     */
    fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float)

    /**
     * clear buffers to preset values
     *
     * @param mask Bitwise OR of masks that indicate the buffers to be cleared.
     *
     */
    fun glClear(mask: UInt)

    /**
     * clear buffers to preset values
     *
     * @param mask Bitwise OR of masks that indicate the buffers to be cleared.
     *
     */
    fun glClear(mask: Int)


    /**
     *  Enable or disable a generic vertex attribute array
     *  @param index Specifies the index of the generic vertex attribute to be enabled
     */
    fun glEnableVertexAttribArray(index: UInt)

    /**
     *  Enable a generic vertex attribute array
     *  @param index Specifies the index of the generic vertex attribute to be enabled
     */
    fun glEnableVertexAttribArray(index: Int)

    /**
     * define an array of generic vertex attribute data
     */
    fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Int, stride: Int, pointer: COpaquePointer?)


    /**
     *  Render primitives from array data
     */
    fun glDrawArrays(mode: Int, first: Int, count: Int)

    /**
     * Disable a generic vertex attribute array
     */
    fun glDisableVertexAttribArray(index: Int)

}