package cx.tfe.fennec.config

data class FennecConfig(
    var configVersion: String = "1.0.0",
    var safariOverlayEnabled: Boolean = false,
    var hideyhoSolver: Boolean = false,
    var highlightMobs: Boolean = false,
)
