package com.gitlab.kordlib.kordx.commands.model.command

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.model.metadata.Metadata
import com.gitlab.kordlib.kordx.commands.model.module.Module
import com.gitlab.kordlib.kordx.commands.model.precondition.Precondition
import com.gitlab.kordlib.kordx.commands.model.processor.CommandProcessor
import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorContext
import org.koin.core.Koin
import org.koin.core.KoinComponent

/**
 * A Command that can be [invoked][invoke] with a [context][T] and [arguments].
 *
 * @param name The word that invokes the command. Unique per [processor][com.gitlab.kordlib.kordx.commands.model.processor.CommandProcessor].
 * @param moduleName The name of the module that contains this command.
 * @param context The context linked to this command, invocations of this command with contexts that don't match will be ignored.
 * @param metadata Extra data linked to this command.
 * @param arguments The arguments required for this command, the values generated by these are expected in the [invoke] function.
 * @param modules All modules currently know by the [CommandProcessor], with their [name][Module.name] as key.
 * Used in combination with [moduleName] to find the Commands' [module].
 * @param preconditions The preconditions the command expects to have been passed before being invoked.
 * @param koin Koin instance used for dependency injection.
 * @param execution The behavior executed on [invoke].
 */
class Command<T : CommandEvent>(
        val name: String,
        val moduleName: String,
        val context: ProcessorContext<*, *, T>,
        val metadata: Metadata,
        val arguments: List<Argument<*, *>>,
        val modules: Map<String, Module>,
        val preconditions: List<Precondition<T>>,
        private val koin: Koin,
        private val execution: suspend (T, List<*>) -> Unit
) : KoinComponent {
    override fun getKoin(): Koin = koin

    /**
     * Returns the module of this command, found by looking up the [moduleName] in [modules].
     *
     * Throws an [IllegalStateException] when missing.
     */
    val module: Module get() = modules[moduleName] ?: error("expected module")

    /**
     * Invokes the command with a [context] and [arguments], suspending until its execution ends.
     *
     * Calling this function with [arguments] not generated by the command's [Command.arguments] is undefined behavior.
     */
    suspend operator fun invoke(context: T, arguments: List<*>) = execution(context, arguments)
}
