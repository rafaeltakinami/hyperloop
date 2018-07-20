package br.com.guiabolso.hyperloop.transport.aws

import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.SendMessageRequest


class SQSTransport(
        private val sqs: AmazonSQS,
        private val queueURL: String
) : Transport {

    override fun sendMessage(message: String): MessageResult {
        val sendMessageRequest = SendMessageRequest()
                .withQueueUrl(queueURL)
                .withMessageBody(message)

        val messageResult = sqs.sendMessage(sendMessageRequest)

        return MessageResult(messageResult.messageId, messageResult.mD5OfMessageBody)
    }

}