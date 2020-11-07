package it.eureka.katas.birthdaygreeting

import org.subethamail.smtp.MessageContext
import org.subethamail.smtp.helper.BasicMessageListener

class TestMessageListener(val recipients: MutableList<String> = mutableListOf()) : BasicMessageListener {
    
    override fun messageArrived(context: MessageContext?, from: String?, to: String?, data: ByteArray?) {
        to?.let { recipient -> recipients.add(recipient) }
    }
}
