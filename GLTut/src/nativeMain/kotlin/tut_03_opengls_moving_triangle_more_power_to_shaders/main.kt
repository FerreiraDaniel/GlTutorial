package tut_03_opengls_moving_triangle_more_power_to_shaders

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val tutorial = VertCalcOffset(GLWrapper())
    val framework = Framework()
    framework.launchTutorial(tutorial)

}