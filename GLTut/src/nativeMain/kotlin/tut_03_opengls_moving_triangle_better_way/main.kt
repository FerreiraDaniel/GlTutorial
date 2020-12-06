package tut_03_opengls_moving_triangle_better_way

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val tutorial = VertPositionOffset(GLWrapper())
    val framework = Framework()
    framework.launchTutorial(tutorial)

}