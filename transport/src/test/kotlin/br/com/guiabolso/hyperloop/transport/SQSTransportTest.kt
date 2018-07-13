package br.com.guiabolso.hyperloop.transport

import br.com.guiabolso.hyperloop.transport.aws.SQSTransport
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.SendMessageResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Test


class SQSTransportTest {

    @Test
    fun `test can send message`() {
        val amazonSQS = mock<AmazonSQS>()
        val queueURL = "http://queue.url"
        val sqsTransport = SQSTransport(amazonSQS, queueURL)

        val md5 = "some-md5"
        val messageId = "some-message-id"

        val messageResult = SendMessageResult().withMD5OfMessageBody(md5).withMessageId(messageId)

        whenever(amazonSQS.sendMessage(any())).thenReturn(messageResult)

        val result = sqsTransport.sendMessage("Message: Whiskas sache XPTO")

        verify(amazonSQS).sendMessage(any())

        assertEquals(md5, result.messageMD5)
        assertEquals(messageId, result.messageId)
    }
}