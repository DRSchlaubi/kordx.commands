package com.gitlab.kordlib.kordx.commands.model.command

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.model.precondition.Precondition
import com.gitlab.kordlib.kordx.commands.internal.CommandsBuilder
import com.gitlab.kordlib.kordx.commands.model.module.Module
import com.gitlab.kordlib.kordx.commands.model.metadata.MutableMetadata
import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorContext
import org.koin.core.Koin

/**
 * DSL builder for a [Command].
 */
@CommandsBuilder
class CommandBuilder<S, A, COMMANDCONTEXT : CommandEvent>(
        val name: String,
        val moduleName: String,
        val context: ProcessorContext<S, A, COMMANDCONTEXT>,
        val metaData: MutableMetadata = MutableMetadata(),
        val preconditions: MutableList<Precondition<COMMANDCONTEXT>> = mutableListOf()
) {
    lateinit var execution: suspend (COMMANDCONTEXT, List<*>) -> Unit
    var arguments: List<Argument<*, A>> = emptyList()

    fun build(modules: Map<String, Module>, koin: Koin): Command<COMMANDCONTEXT> {
        return Command(name, moduleName, context, metaData.toMetaData(), arguments, modules, preconditions, koin, execution)
    }

    /**
     * Defines a [Precondition] for this specific command with a given [priority][Precondition.priority].
     */
    fun precondition(
            priority: Long = 0,
            precondition: suspend COMMANDCONTEXT.() -> Boolean
    ) {
        preconditions += com.gitlab.kordlib.kordx.commands.model.precondition.precondition(context, priority, precondition)
    }
}
