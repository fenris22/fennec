package cx.tfe.fennec.features

import cx.tfe.fennec.features.impl.Debug
import cx.tfe.fennec.features.impl.HideyhoSolver
import cx.tfe.fennec.features.impl.debug.ShowEntities
import cx.tfe.fennec.features.impl.safari.SafariOverlay

object ModuleManager {

    val modules: List<Module> = listOf(
        SafariOverlay,
        HideyhoSolver,
    )
    val debugModules: List<Module> = listOf(
        Debug,
        ShowEntities,
    )

}