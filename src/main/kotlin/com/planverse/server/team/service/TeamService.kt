package com.planverse.server.team.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamInfoRequestDTO
import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.dto.TeamMemberInfoDTO
import com.planverse.server.team.repository.TeamInfoRepository
import com.planverse.server.team.repository.TeamMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class TeamService(
    private val fileService: FileService,
    private val userInfoRepository: UserInfoRepository,
    private val teamInfoRepository: TeamInfoRepository,
    private val teamMemberInfoRepository: TeamMemberInfoRepository,
) {
    // 팀 DTO 생성 및 필요 정보 설정
    private fun getCreateTeamInfoDTO(teamId: Long): TeamInfoDTO {
        val teamInfo = teamInfoRepository.findById(teamId).orElseThrow { BaseException(StatusCode.TEAM_NOT_FOUND) }.let { TeamInfoDTO.toDto(it) }

        val profileImage = fileService.getFileUrl(Constant.FILE_TARGET_TEAM, teamId)
        val creator = teamMemberInfoRepository.findByTeamInfoIdAndCreatorAndDeleteYn(teamId, true, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.NOT_TEAM_CREATOR)
        }.let { it ->
            val user = userInfoRepository.findById(it.userInfoId).orElseThrow {
                BaseException(StatusCode.USER_NOT_FOUND)
            }

            TeamMemberInfoDTO.toDto(it, user.name, user.email)
        }

        val members = teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamId, false, Constant.DEL_N).orElse(emptyList()).map { member ->
            val user = userInfoRepository.findById(member.userInfoId).orElseThrow { BaseException(StatusCode.USER_NOT_FOUND) }
            TeamMemberInfoDTO.toDto(member, user.name, user.email)
        }

        return teamInfo.apply {
            this.teamProfileImage = profileImage
            this.teamCreatorInfo = creator
            this.teamMemberInfos = members
        }
    }

    // 팀 정보 조회 및 접근 권한 확인
    fun getTeamInfo(userInfo: UserInfo, teamId: Long): TeamInfoDTO {
        val teamInfo = getCreateTeamInfoDTO(teamId)

        // 비공개 팀인 경우 접근 권한 확인
        if (teamInfo.private == true) {
            val memberIds = mutableListOf(teamInfo.teamCreatorInfo?.userInfoId)
            memberIds.addAll(teamInfo.teamMemberInfos.orEmpty().map { it.userInfoId })

            if (userInfo.id !in memberIds) {
                throw BaseException(StatusCode.NOT_TEAM_MEMBER)
            }
        }

        return teamInfo
    }

    // 내가 생성한 팀 목록 조회
    fun getTeamListCreate(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(
            userInfo.id!!, true, Constant.DEL_N, pageable
        ).map { getCreateTeamInfoDTO(it.teamInfoId) }
    }

    // 내가 멤버인 팀 목록 조회
    fun getTeamListMember(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(
            userInfo.id!!, false, Constant.DEL_N, pageable
        ).map {
            val teamInfo = getCreateTeamInfoDTO(it.teamInfoId)

            // 비공개 팀인 경우 접근 권한 확인
            if (teamInfo.private == true) {
                val memberIds = mutableListOf(teamInfo.teamCreatorInfo?.userInfoId)
                memberIds.addAll(teamInfo.teamMemberInfos.orEmpty().map { it -> it.userInfoId })

                if (userInfo.id !in memberIds) {
                    throw BaseException(StatusCode.NOT_TEAM_MEMBER)
                }
            }

            teamInfo
        }
    }

    // 팀 생성
    @Transactional
    fun createTeam(userInfo: UserInfo, teamInfoRequestDTO: TeamInfoRequestDTO) {
        val teamId = teamInfoRepository.save(teamInfoRequestDTO.toEntity()).id!!

        // 생성자 추가
        val teamMemberInfos = mutableListOf(TeamMemberInfoDTO.toEntity(userInfo.id!!, teamId, true))

        // 초대 멤버 추가
        teamInfoRequestDTO.invite?.forEach { email ->
            if (email != userInfo.email) {
                userInfoRepository.findByEmailAndDeleteYn(email, Constant.DEL_N).ifPresent { user ->
                    teamMemberInfos.add(TeamMemberInfoDTO.toEntity(user.id!!, teamId, false))
                }
            }
        }

        teamMemberInfoRepository.saveAll(teamMemberInfos)
    }

    // 팀 정보 수정
    @Transactional
    fun modifyTeamInfo(userInfo: UserInfo, updateRequest: TeamInfoUpdateRequestDTO) {
        // 팀 생성자 확인
        val creator = teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(updateRequest.teamId, userInfo.id!!, true, Constant.DEL_N)
            .orElseThrow { BaseException(StatusCode.TEAM_NOT_FOUND) }

        // 팀 정보 수정
        val teamInfo = teamInfoRepository.findById(creator.teamInfoId).orElseThrow { BaseException(StatusCode.TEAM_NOT_FOUND) }

        if (updateRequest.name.isNotBlank()) {
            teamInfo.description = updateRequest.description
        }

        teamInfoRepository.save(teamInfo)
    }

    // 팀 멤버 초대/제외
    @Transactional
    fun modifyTeamMember(userInfo: UserInfo, updateRequest: TeamInfoUpdateRequestDTO) {
        val teamId = updateRequest.teamId

        // 팀 생성자 확인
        teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamId, userInfo.id!!, true, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let {
            // 기존 멤버 목록 조회
            val memberList = teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamId, false, Constant.DEL_N).orElse(emptyList())

            // 초대 멤버 추가
            updateRequest.invite?.takeIf { invite -> invite.isNotEmpty() }?.let { inviteList ->
                val existingMemberIds = memberList.map { member -> member.userInfoId }.toSet()

                inviteList.map { email ->
                    userInfoRepository.findByEmail(email).orElseThrow { BaseException(StatusCode.USER_NOT_FOUND) }.id!!
                }.filterNot { inviteUserId ->
                    inviteUserId in existingMemberIds
                }.forEach { userId ->
                    teamMemberInfoRepository.save(TeamMemberInfoDTO.toEntity(userId, teamId, false))
                }
            }

            // 멤버 제외
            updateRequest.exclude?.takeIf { exclude -> exclude.isNotEmpty() }?.let { excludeList ->
                val existingMemberIds = memberList.map { member -> member.userInfoId }.toSet()

                excludeList.map { email ->
                    userInfoRepository.findByEmail(email).orElseThrow { BaseException(StatusCode.USER_NOT_FOUND) }.id!!
                }.filter { excludeUserId ->
                    excludeUserId in existingMemberIds
                }.forEach { userId ->
                    if (it.userInfoId == userId) throw BaseException(StatusCode.TEAM_CREATOR_CANNOT_EXCLUDE)

                    teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamId, userId, false, Constant.DEL_N).ifPresent { member ->
                        member.deleteYn = Constant.DEL_Y
                        teamMemberInfoRepository.save(member)
                    }
                }
            }
        }
    }

    // 팀 이미지 수정
    @Transactional
    fun modifyTeamImage(userInfo: UserInfo, teamId: Long, multipartFile: MultipartFile) {
        // 팀 생성자 확인
        teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamId, userInfo.id!!, true, Constant.DEL_N).orElseThrow { BaseException(StatusCode.TEAM_NOT_FOUND) }

        // 기존 이미지 삭제 후 새 이미지 저장
        fileService.deleteFilePass(Constant.FILE_TARGET_TEAM, teamId)
        fileService.fileSave(Constant.FILE_TARGET_TEAM, teamId, multipartFile)
    }
}