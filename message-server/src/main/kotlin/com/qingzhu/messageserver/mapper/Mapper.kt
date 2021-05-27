package com.qingzhu.messageserver.mapper

import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.messageserver.domain.dto.CustomerStatusDto
import com.qingzhu.messageserver.domain.dto.StaffStatusDto
import com.qingzhu.messageserver.domain.entity.*
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class CustomerStatusMapper {
    abstract fun mapFromDto(staff: CustomerStatusDto): CustomerStatus

    companion object {
        val mapper: CustomerStatusMapper = Mappers.getMapper(CustomerStatusMapper::class.java)
    }
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class StaffStatusMapper {
    protected abstract fun mapFromDto(staff: StaffStatusDto): StaffStatus

    fun fromDtoWithMap(staff: StaffStatusDto): StaffStatus {
        val status = mapFromDto(staff)
        if (staff.clientAccessServer != null) {
            status.clientAccessServerMap += staff.clientAccessServer
        }
        return status
    }

    companion object {
        val mapper: StaffStatusMapper = Mappers.getMapper(StaffStatusMapper::class.java)
    }
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class ChatMessageMapper {
    abstract fun mapToFromMessage(message: Message): ChatMessage

    companion object {
        val mapper: ChatMessageMapper = Mappers.getMapper(ChatMessageMapper::class.java)
    }
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class ConversationMapper {
    abstract fun mapFromStatus(conversationStatus: ConversationStatus): Conversation

    fun mapFromStatusWithEnum(conversationStatus: ConversationStatus): Conversation {
        val conversation = mapper.mapFromStatus(conversationStatus)
        conversation.seqId = conversationStatus.id
        conversation.fromType = conversationStatus.fromType.type
        conversation.beginner = conversationStatus.beginner.type
        conversation.convType = conversationStatus.convType.type
        conversation.transferType = conversationStatus.transferType?.type
        conversation.relatedType = conversationStatus.relatedType.type
        conversation.closeReason = conversationStatus.closeReason?.reason
        conversation.status = conversationStatus.status?.type
        conversation.terminator = conversationStatus.terminator?.type
        return conversation
    }

    companion object {
        val mapper: ConversationMapper = Mappers.getMapper(ConversationMapper::class.java)
    }
}