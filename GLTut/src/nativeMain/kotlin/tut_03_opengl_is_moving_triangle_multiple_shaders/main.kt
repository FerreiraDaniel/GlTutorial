package tut_03_opengl_is_moving_triangle_multiple_shaders

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val tutorial = FragChangeColor(GLWrapper())
    val framework = Framework()
    framework.launchTutorial(tutorial)

}