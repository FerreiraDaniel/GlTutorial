package tut_02_playing_with_colors_vertex_colors

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val glWrapper = GLWrapper()
    val tutorial = VertexColors(glWrapper)
    val framework = Framework()
    framework.launchTutorial(tutorial)

}