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
            status.clientAccessServerMap.plusAssign(staff.clientAccessServer)
        }
        status.priorityOfShuntMap += staff.priorityOfShunt
        return status
    }

    companion object {
        val mapper: StaffStatusMapper = Mappers.getMapper(StaffStatusMapper::class.java)
    }
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class ChatMessageMapper {
    abstract fun mapToFromMessage(message: Message): ChatMessage

    abstract fun mapToToMessage(message: ChatMessage): Message

    companion object {
        val mapper: ChatMessageMapper = Mappers.getMapper(ChatMessageMapper::class.java)
    }
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class ConversationMapper {
    abstract fun mapFromStatus(conversationStatus: ConversationStatus): Conversation

    fun mapFromStatusWithEnum(conversationStatus: ConversationStatus): Conversation {
        val conversation = mapper.mapFromStatus(conversationStatus)
        conversation.fromType = conversationStatus.fromType.name
        conversation.beginner = conversationStatus.beginner.name
        conversation.convType = conversationStatus.convType.name
        conversation.transferType = conversationStatus.transferType?.name
        conversation.relatedType = conversationStatus.relatedType.name
        conversation.closeReason = conversationStatus.closeReason?.name
        conversation.status = conversationStatus.status?.name
        conversation.terminator = conversationStatus.terminator?.name
        return conversation
    }

    companion object {
        val mapper: ConversationMapper = Mappers.getMapper(ConversationMapper::class.java)
    }
}