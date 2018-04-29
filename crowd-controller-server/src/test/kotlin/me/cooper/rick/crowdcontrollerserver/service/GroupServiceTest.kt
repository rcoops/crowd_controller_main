package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.group.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.*
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestGroup
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestUser
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestUserList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class GroupServiceTest {

    @InjectMocks
    private lateinit var groupService: GroupServiceImpl

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var roleRepository: RoleRepository
    @Mock
    private lateinit var groupRepository: GroupRepository
    @Mock
    private lateinit var locationResolverService: LocationResolverService
    @Mock
    private lateinit var webSocketController: WebSocketController

    private lateinit var testGroup: Group
    private lateinit var testGroupedAdmin: User

    @Rule
    @JvmField
    val thrown = ExpectedException.none()

    @Before
    fun setup() {
        testGroup = buildTestGroup(TEST_GROUP_ID, TEST_ADMIN_ID)
        setupTestGroupedAdmin()

        // group repository mocks
        doReturn(testGroup).`when`(groupRepository).findOne(TEST_GROUP_ID)
        doReturn(null).`when`(groupRepository).findOne(TEST_NON_EXISTENT_GROUP_ID)

        // role repository mocks
        doReturn(listOf(Role(1L, "ROLE_GROUP_ADMIN")))
                .`when`(roleRepository).findAllByNameIn(listOf("ROLE_GROUP_ADMIN"))

        // user repository mocks
        doReturn(testGroupedAdmin).`when`(userRepository).findOne(testGroupedAdmin.id)
        doReturn(testGroup.admin).`when`(userRepository).findOne(testGroup.admin!!.id)
        doReturn(null).`when`(userRepository).findOne(TEST_NON_EXISTENT_ADMIN_ID)
    }

    @Test
    fun testGroup() {
        // When the service is called to return a group (dto)
        val groupDto = groupService.group(TEST_GROUP_ID)

        // Then the group dto retrieved has the correct details
        assertEquals(groupDto.id, testGroup.id)
        assertEquals(groupDto.adminId, testGroup.admin!!.id)
    }

    @Test(expected = GroupNotFoundException::class)
    fun testGroupNotFoundExceptionOnNonExistingGroupId() {
        // When the service is called to return a group (dto) for a non existent id
        groupService.group(TEST_NON_EXISTENT_GROUP_ID)

        // Then a GroupNotFoundException is thrown
    }

    @Test
    fun testGroups() {
        // Given the group repository is mocked to return a list of 2 test groups
        val testGroup2 = buildTestGroup(TEST_GROUP_ID + 1, TEST_ADMIN_ID + 1)
        val allGroups = listOf(testGroup, testGroup2)
        doReturn(allGroups).`when`(groupRepository).findAll()

        // When the service is called to return dto representations of 'all' groups
        val groupDtos = groupService.groups()

        // Then the correct number of dtos are retrieved
        assertEquals(groupDtos.size, allGroups.size)
        // And their details are as expected
        assertEquals(groupDtos[0].id, testGroup.id)
        assertEquals(groupDtos[0].adminId, testGroup.admin!!.id)
        assertEquals(groupDtos[1].id, testGroup2.id)
        assertEquals(groupDtos[1].adminId, testGroup2.admin!!.id)
    }

    @Test
    fun testCreate() {
        // Given a list of members
        val members = buildTestUserList(5, role = userRole)
        // And one is designated to be the group admin
        val admin = members[0]
        val mockGroup = Group.fromUsers(admin, members).copy(id = 1L)
        // And repostories are mocked to return expected results
        doReturn(admin).`when`(userRepository).findOne(admin.id)
        doReturn(members).`when`(userRepository).findAllByIdIn(members.map(User::id).toSet())
        doReturn(admin).`when`(userRepository)
                .saveAndFlush(any(User::class.java))
        doReturn(mockGroup).`when`(groupRepository).save(any(Group::class.java))

        // When the service is called to create a group
        val groupDto = groupService.create(CreateGroupDto(admin.id, members.map(User::toGroupMemberDto)))

        // Then the returned dto has matching id, admin id and number of members
        assertEquals(groupDto.id, mockGroup.id)
        assertEquals(groupDto.adminId, mockGroup.admin!!.id)
        assertEquals(groupDto.members.size, mockGroup.members.size)
    }

    @Test
    fun testCreateForGroupedMembers() {
        // Given a list of members that are intended to be added to a group
        val unGroupedMembers = buildTestUserList(2, role = userRole)
        // And some are grouped
        val groupedMembers = buildTestUserList(2, 3, userRole, testGroup)
        val members = groupedMembers + unGroupedMembers
        // And the user repository is mocked to return the members when their ids are supplied
        doReturn(members).`when`(userRepository).findAllByIdIn(members.map(User::id).toSet())
        // And the admin of the group is set
        val admin = unGroupedMembers[0]
        doReturn(admin).`when`(userRepository).findOne(admin.id)

        // A UserInGroupException is expected
        thrown.expect(UserInGroupException::class.java)
        thrown.expectMessage("Users: [${groupedMembers.map(User::toDto).mapToName()}] already belong to a group")

        // When the service attempts to create a group including the ids of these grouped members
        groupService.create(CreateGroupDto(admin.id, members.map(User::toGroupMemberDto)))
    }

    @Test
    fun testCreateForNonExistentAdminId() {
        // Expect an UserNotFoundException
        thrown.expect(UserNotFoundException::class.java)
        thrown.expectMessage("User with identifier: $TEST_NON_EXISTENT_ADMIN_ID not found")
        // When the service is called to create a group using a non existent admin id
        groupService.create(CreateGroupDto(TEST_NON_EXISTENT_ADMIN_ID))
    }

    @Test
    fun testCreateForAlreadyGroupedAdmin() {
        // Expect an UserInGroupException
        thrown.expect(UserInGroupException::class.java)
        thrown.expectMessage("User ${testGroupedAdmin.username} already belongs to a different group")

        groupService.create(CreateGroupDto(testGroupedAdmin.id))
    }

    @Test
    fun testUpdate() {
        val additionalUser = buildTestUser(2L, "New Admin", userRole)
        val testGroup = this.testGroup.copy(members = setOf(testGroup.admin!!, additionalUser))
        // admin switch
        doReturn(additionalUser.copy(group = testGroup, groupAccepted = true))
                .`when`(userRepository).findOne(additionalUser.id)

        doReturn(testGroup).`when`(groupRepository).findOne(testGroup.id)

        groupService.update(testGroup.id!!, groupService.toGroupDto(testGroup))
    }

    @Test
    fun testUpdateWithIncorrectBody() {
        // Given a path Id which does not match that of the associated DTO
        val pathId = 1 + testGroup.id!!
        val testGroupDto = groupService.toGroupDto(testGroup)

        // Expect an InvalidBodyException
        thrown.expect(InvalidBodyException::class.java)
        thrown.expectMessage("Path resource identifier $pathId & body identifier ${testGroupDto.id} do not match")

        // When the service is asked to update a group with these details
        groupService.update(pathId, testGroupDto)
    }

    @Test
    fun testUpdateWithNonExistentGroup() {
        // Given a dto for a non-existent group
        val nonExistentGroupDto = GroupDto(TEST_NON_EXISTENT_GROUP_ID)
        //Expect a GroupNotFoundException
        thrown.expect(GroupNotFoundException::class.java)
        thrown.expectMessage("Group with id: $TEST_NON_EXISTENT_GROUP_ID not found")
        // When attempting to update the group
        groupService.update(nonExistentGroupDto.id, nonExistentGroupDto)
    }

    @Test(expected = IllegalPromotionException::class)
    fun testUpdateAdminPromotionForUnconfirmedMember() {
        // Given a group that does not include testGroupedAdmin
        val testUnconfirmedAdmin = testGroup.admin!!.copy(groupAccepted = false)
        val testGroupWithUnconfirmedAdmin = testGroup.copy(admin =  testUnconfirmedAdmin)
        // And the group repository is mocked to return that group when queried
        doReturn(testGroupWithUnconfirmedAdmin).`when`(groupRepository).findOne(testGroupWithUnconfirmedAdmin.id)
        // And the user repository is mocked to return the admin when queried
        doReturn(testUnconfirmedAdmin).`when`(userRepository).findOne(testUnconfirmedAdmin.id)
        // When the service is asked to update
        groupService.update(testGroupWithUnconfirmedAdmin.id!!, groupService.toGroupDto(testGroupWithUnconfirmedAdmin))

        // Expect an IllegalPromotionException
    }

    @Test
    fun testRemoveFromGroupPromotesNewAdmin() {
        // Given a test group with 2 accepted members
        val testAdditionalUser = User(3L, "New Admin", groupAccepted = true)
        val testGroup = this.testGroup.copy(members = setOf(testGroup.admin!!, testAdditionalUser))
        val groupArgCaptor = ArgumentCaptor.forClass(Group::class.java)
        // And the group repository is mocked to return that group when queried
        doReturn(testGroup).`when`(groupRepository).findOne(testGroup.id)
        // and the user repository is mocked to return the test user when an entity with its id is saved
        doReturn(testAdditionalUser).`when`(userRepository).save(argThat<User> { it.id == testAdditionalUser.id })

        // When the current admin of the group is removed
        groupService.removeFromGroup(testGroup.id!!, testGroup.admin!!.id)

        // Then the group is saved
        verify(groupRepository, times(1)).saveAndFlush(groupArgCaptor.capture())
        // And the new admin is the remaining user
        assertEquals(testAdditionalUser, groupArgCaptor.value.admin)
    }

    @Test
    fun testRemoveFromGroupRemovesGroupWithOneMember() {
        // When the one accepted member of a group is removed
        groupService.removeFromGroup(testGroup.id!!, testGroup.admin!!.id)

        // Then the group is deleted
        verify(groupRepository, times(1)).delete(TEST_GROUP_ID)
    }

    @Test
    fun testAdmin() {
        // When the service is asked to retrieve a group admin
        val adminUsername = groupService.admin(testGroup.id!!)

        // Then the admin is returned
        assertEquals(testGroup.admin!!.username, adminUsername)
    }

    private fun setupTestGroupedAdmin() {
        testGroupedAdmin = User(
                id = TEST_GROUPED_ADMIN_ID,
                username = TEST_GROUPED_ADMIN_USERNAME,
                group = testGroup
        )
    }

    companion object {
        private const val TEST_GROUP_ID = 1L
        private const val TEST_NON_EXISTENT_GROUP_ID = -1L
        private const val TEST_NON_EXISTENT_ADMIN_ID = -1L
        private const val TEST_ADMIN_ID = 2L
        private const val TEST_GROUPED_ADMIN_USERNAME = "admin"
        private const val TEST_GROUPED_ADMIN_ID = 666L
        private val userRole = Role()
        fun List<UserDto>.mapToName() = map(UserDto::username).joinToString(", ")
    }

}
