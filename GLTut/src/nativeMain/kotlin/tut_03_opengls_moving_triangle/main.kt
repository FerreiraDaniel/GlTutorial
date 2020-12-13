package tut_03_opengls_moving_triangle

import framework.Framework
import gl_wrapper.GLWrapper
import tut_03_opengls_moving_triangle_better_way.VertPositionOffset


fun main() {
    val tutorial = CpuPositionOffset(GLWrapper())
    val framework = Framework()
    framework.launchTutorial(tutorial)

}