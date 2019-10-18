package com.gitlab.kordlib.kordx.commands.command

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.flow.Precondition

class Command<T : EventContext>(
        val name: String,
        val moduleName: String,
        val context: CommandContext<*, *, *>,
        val metaData: MetaData,
        val arguments: List<Argument<*, *>>,
        val modules: Map<String, Module>,
        val preconditions: List<Precondition<T>>,
        private val execution: suspend (T, List<*>) -> Unit
) {
    val module: Module get() = modules[moduleName] ?: error("expected module")

    suspend operator fun invoke(context: T, arguments: List<*>) = execution(context, arguments)
}