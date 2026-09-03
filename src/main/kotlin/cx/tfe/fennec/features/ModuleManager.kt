package cx.tfe.fennec.features

import cx.tfe.fennec.features.impl.Debug
import cx.tfe.fennec.features.impl.safari.SafariOverlay

object ModuleManager {

    val modules: List<Module> = listOf(
        SafariOverlay,
    )
    val debugModules: List<Module> = listOf(
        Debug,
    )

}