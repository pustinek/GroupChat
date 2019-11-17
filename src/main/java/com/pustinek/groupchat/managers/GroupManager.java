package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.enums.GroupMemberRoles;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupMember;
import com.pustinek.groupchat.utils.Callback;
import com.pustinek.groupchat.utils.StreamUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GroupManager extends Manager {
    private static HashMap<UUID, Group> groups = new HashMap<>(); /*Group UUID, Group*/
    private final Main plugin;

    public GroupManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void shutdown() {
        Main.debug("Cleaning up GroupManager...");
        groups = null;
    }


    //===================================================

    /**
     * Get all groups
     *
     * @return HashMap for all the groups
     */
    public HashMap<UUID, Group> getGroups() {
        return groups;
    }

    /**
     * Get group clone by its name
     *
     * @param groupID   UUID of the group
     * @return single matching group
     *
     */
    @Nullable
    public Group getGroupClone(UUID groupID) {
        if (groups.containsKey(groupID))
            return new Group(groups.get(groupID));
        return null;
    }

    /**
     * Get group clone by its name
     *
     * @param       groupName name of the group
     * @return single matching group
     */
    public Group getGroupClone(String groupName) {
        List<Group> groupList = groups.values().stream().filter(group -> group.getName().equalsIgnoreCase(groupName)).collect(Collectors.toList());
        if (groupList.isEmpty()) return null;
        return new Group(groupList.get(0));
    }

    /**
     * Get groups where the specified player is the owner.
     *
     * @param ownerUUID owners UUID
     * @return List of found groups
     */
    public List<Group> getOwnerGroups(UUID ownerUUID) {
        return groups.values().stream().filter(group -> group.getOwner().equals(ownerUUID)
        ).collect(Collectors.toList());
    }

    /**
     * Get groups where the specified player is the member of the group.
     *
     * @param memberUUID uuid of the member
     * @return List of groups
     */
    public List<Group> getMemberGroups(UUID memberUUID) {
        return groups.values().stream().filter(group -> group.getUUIDOfMembers().contains(memberUUID)).collect(Collectors.toList());
        //return groups.values().stream().filter(group -> group.getMembers().contains(memberUUID)
        //).collect(Collectors.toList());
    }


    /**
     * Get group by name and his member
     *
     * @param groupName name of the group
     * @param member    uuid of the member
     * @return single matching group
     */
    public Group getGroupByNameAndMember(String groupName, UUID member) {

        Group group = null;
        try {
            group = groups
                    .entrySet()
                    .stream()
                    .filter(
                            entry -> entry.getValue().getName().equalsIgnoreCase(groupName) &&
                                    entry.getValue().getUUIDOfMembers().contains(member))
                    .collect(StreamUtils.singletonCollector())
                    .getValue();
        } catch (IllegalStateException e) {
            Main.debug("Error occurred trying to get group by name and member - group didn't exist");
        }
        if (group != null) {
            group = new Group(group);
        }
        return group;
    }


    /**
     * Create a group with initial name and owner
     *
     * @param name  name of the group
     * @param player player that created it
     */
    public void createGroup(String name, Player player) {
        UUID id = UUID.randomUUID();
        GroupMember groupMember = new GroupMember(player.getName(), player.getUniqueId(), GroupMemberRoles.OWNER);
        Group group = new Group(id, player.getUniqueId(), name, name, new ArrayList<>(Collections.singletonList(groupMember)));


        Main.debug("Created group with name (" + group.getName() + ")");

        Main.getDatabase().addGroup(group, new Callback<Group>(plugin) {
            @Override
            public void onResult(Group result) {
                Main.debug("Successfully created group");
                groups.put(id, result);
                Main.getRedisManager().updateGroupPublish(group);
                super.onResult(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Main.error("Something went wrong when creating a group");
                Main.error(throwable);
                super.onError(throwable);
            }
        });

    }



    /**
     * (Re)load groups from the database and save them to static variable
     *
     * @param callback return number of loaded groups from database
     */
    public void reloadGroups(final Callback<Integer> callback) {

        Main.getDatabase().connect(new Callback<Integer>(plugin) {
            @Override
            public void onResult(Integer result) {
                super.onResult(result);

                Main.getDatabase().getGroups(new Callback<Collection<Group>>(plugin) {
                    @Override
                    public void onResult(Collection<Group> result) {
                        for (Group group : result) {
                            groups.put(group.getId(), group);
                        }
                        if (callback != null) callback.callSyncResult(result.size());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (callback != null) callback.callSyncError(throwable);
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                if (callback != null) callback.callSyncError(throwable);
            }
        });
    }

    /**
     * Delete the group from database and memory
     *
     * @param group    group you wish to delete
     * @param callback UUID of the deleted group on success, null if unsuccessful.
     */
    public void deleteGroup(Group group, Boolean deleteFromDB, Callback<UUID> callback) {

        if (!deleteFromDB) {
            groups.remove(group.getId());

            if (callback != null)
                callback.callSyncResult(group.getId());
            return;
        }

        Main.getDatabase().removeGroup(group, new Callback<UUID>(plugin) {
            @Override
            public void onResult(UUID result) {
                if (callback != null) {
                    callback.callSyncResult(group.getId());
                }
                groups.remove(group.getId());
                Main.getRedisManager().removeGroupPublish(group);
            }

            @Override
            public void onError(Throwable throwable) {
                if (callback != null) {
                    callback.callSyncError(throwable);
                }

            }
        });

    }

    /**
     * Update group in memory and in Database
     *
     * @param newGroup new group that you wish to update
     * @param updateDB should it update database entry
     */
    public void updateGroup(@NotNull Group newGroup, boolean updateDB, boolean redisSync) {

        groups.put(newGroup.getId(), newGroup);
        if (redisSync) Main.getRedisManager().updateGroupPublish(newGroup);
        if (updateDB) Main.getDatabase().addGroup(newGroup, null);

    }

    /**
     * Change group prefix
     *
     * @param groupID  UUID of the group
     * @param prefix    New prefix
     *
     */
    public void changeGroupPrefix(UUID groupID, String prefix) {
        Group group = groups.get(groupID);
        if (group != null) {
            Group groupClone = new Group(group);
            groupClone.setPrefix(prefix);
            Main.getDatabase().addGroup(groupClone, new Callback<Group>(plugin) {
                @Override
                public void onResult(Group result) {
                    Main.debug("Successfully changed group prefix to " + groupClone.getPrefix());
                    group.setPrefix(prefix);
                }

                @Override
                public void onError(Throwable throwable) {
                    Main.error("Something went wrong when updating the prefix of the group");
                    Main.error(throwable);
                }
            });

        }
    }

    /**
     * Kick player from the group
     *
     * @param groupID  UUID of the group
     * @param playerID UUID of the player to kick
     * @return boolean has the player been kicked
     */
    public boolean kickPlayer(UUID groupID, UUID playerID, boolean updateDB, boolean redisSync) {
        Group group = getGroupClone(groupID);
        if (group == null) return false;
        Main.getChatManager().sendGenericGroupMessage(group, " Player " + group.getGroupMember(playerID).getUsername() + " was successfully kicked from the group");
        group.removeMember(playerID);
        updateGroup(group, updateDB, redisSync);
        return true;
    }

    /**
     * Leave the group
     *
     * @param groupID  UUID of the group
     * @param playerID UUID of the player to kick
     * @return boolean has the player successfully left the group
     */
    public boolean leaveGroup(UUID groupID, UUID playerID, boolean updateDB, boolean redisSync) {
        Group group = getGroupClone(groupID);

        if (group == null) return false;
        Main.getChatManager().sendGenericGroupMessage(group, " Player " + group.getGroupMember(playerID).getUsername() + " left the group");
        group.removeMember(playerID);
        updateGroup(group, updateDB, redisSync);
        return true;
    }


}
