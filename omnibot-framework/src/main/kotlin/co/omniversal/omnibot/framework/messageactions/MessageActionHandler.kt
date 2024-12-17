package co.omniversal.omnibot.framework.messageactions

import net.dv8tion.jda.api.entities.Message

interface MessageActionHandler {
    /**
     * Determines whether the message should be handled by this handler.
     * @param message Discord message to evaluate
     * @return true if the message should be handled, false otherwise
     */
    suspend fun handles(message: Message): Boolean

    /**
     * Executes the message action
     * @param message The message that triggered the action
     */
    suspend fun execute(message: Message)
}