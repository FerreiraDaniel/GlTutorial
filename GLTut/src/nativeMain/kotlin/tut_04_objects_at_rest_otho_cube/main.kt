package tut_04_objects_at_rest_otho_cube

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val glWrapper = GLWrapper()
    val tutorial = OrthoCube(glWrapper)
    val framework = Framework()
    framework.launchTutorial(tutorial)

}