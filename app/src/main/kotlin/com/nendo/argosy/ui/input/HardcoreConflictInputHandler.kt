package com.nendo.argosy.ui.input

class HardcoreConflictInputHandler(
    private val getFocusIndex: () -> Int,
    private val onFocusChange: (Int) -> Unit,
    private val onKeepHardcore: () -> Unit,
    private val onDowngradeToCasual: () -> Unit,
    private val onKeepLocal: () -> Unit
) : InputHandler {

    private val actions = listOf(onKeepHardcore, onDowngradeToCasual, onKeepLocal)

    override fun onUp(): InputResult {
        val index = getFocusIndex()
        if (index > 0) onFocusChange(index - 1)
        return InputResult.HANDLED
    }

    override fun onDown(): InputResult {
        val index = getFocusIndex()
        if (index < actions.lastIndex) onFocusChange(index + 1)
        return InputResult.HANDLED
    }

    override fun onConfirm(): InputResult {
        actions.getOrNull(getFocusIndex())?.invoke()
        return InputResult.HANDLED
    }

    override fun onBack(): InputResult {
        onKeepLocal()
        return InputResult.HANDLED
    }
}
