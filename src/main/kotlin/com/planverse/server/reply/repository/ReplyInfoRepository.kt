package com.planverse.server.reply.repository

import com.planverse.server.reply.entity.ReplyInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyInfoRepository : JpaRepository<ReplyInfoEntity, Long> {
}