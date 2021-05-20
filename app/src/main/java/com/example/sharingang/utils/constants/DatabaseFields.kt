package com.example.sharingang.utils.constants

/**
 * DatabaseFields contains fields that are used in the database
 * (constant collections' and documents' name)
 */
interface DatabaseFields {
    companion object {
        const val DBFIELD_NUM_UNREAD = "numUnread"
        const val DBFIELD_MESSAGE = "message"
        const val DBFIELD_FROM = "from"
        const val DBFIELD_TO = "to"
        const val DBFIELD_LAST_MESSAGE = "lastMessage"
        const val DBFIELD_CHATS = "chats"
        const val DBFIELD_MESSAGEPARTNERS = "messagePartners"
        const val DBFIELD_MESSAGES = "messages"
        const val DBFIELD_USERS = "users"
        const val DBFIELD_REPORTS = "reports"
        const val DBFIELD_REPORTER = "reporter"
        const val DBFIELD_REASON = "reason"
        const val DBFIELD_DESCRIPTION = "description"
        const val DBFIELD_REPORTEDAT = "reportedAt"
    }

}
