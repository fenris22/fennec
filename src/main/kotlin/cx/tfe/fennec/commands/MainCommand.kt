package cx.tfe.fennec.commands

import com.github.stivais.commodore.Commodore
import cx.tfe.fennec.Fennec.DEV_MODE
import cx.tfe.fennec.config.ConfigManager
import cx.tfe.fennec.features.ModuleManager
import cx.tfe.fennec.features.impl.safari.SafariOverlay
import cx.tfe.fennec.util.Helpers.tell

val mainCommand = Commodore("fennec") {
    for (module in ModuleManager.modules) {
        literal(module.name) {
            runs{
                if (module.enabled) {
                    tell("Disabled ${module.stageName}")
                } else {
                    tell("Enabled ${module.stageName}")
                }
                module.toggle()
            }
        }
    }
    if (DEV_MODE) {
        for (debugModule in ModuleManager.debugModules) {
            literal(debugModule.name) {
                runs{
                    if (debugModule.enabled) {
                        tell("Disabled ${debugModule.stageName}")
                    } else {
                        tell("Enabled ${debugModule.stageName}")
                    }
                    debugModule.toggle()
                }
            }
        }
    }
    runs {
        tell("I'm too lazy to make gui, use command autocomplete to navigate the settings.")
    }
}