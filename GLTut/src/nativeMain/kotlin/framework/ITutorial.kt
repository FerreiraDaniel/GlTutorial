package framework

interface ITutorial {
    fun defaults(displayMode: Int, width: Int, height: Int): Int

    fun getWindowTitle(): String

    fun keyboard(window: Int?, key: UByte, x: Int, y: Int)

    //Called whenever the window is resized. The new window size is given, in pixels.
//This is an opportunity to call glViewport or glScissor to keep up with the change in size.
    fun reshape(w: Int, h: Int)

    //Called after the window and OpenGL are initialized. Called exactly once, before the Tut_02_Playing_with_Colors.main loop.
    fun init(framework: IFramework)


    fun display()
}