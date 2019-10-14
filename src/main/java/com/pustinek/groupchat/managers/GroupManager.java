package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Callback;
import com.pustinek.groupchat.utils.StreamUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GroupManager extends Manager {
    private static HashMap<UUID, Group> groups = new HashMap<>(); /*Group UUID, Group*/
    private static Map<UUID, List<UUID>> groupInvites = new HashMap<>(); /*invited player UUID, group UUID*/
    private final Main plugin;

    private static Map<Integer, HashMap<UUID, UUID>> gInvites = new HashMap<>();

    public GroupManager(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public void shutdown() {
        Main.debug("Cleaning up GroupManager...");
        groups = null;
        groupInvites = null;
        gInvites = null;
    }


    //===================================================
    public HashMap<UUID, Group> getGroups() {
        return groups;
    }

    /**
     * Get group by its ID
     *
     * @param groupID name of the group
     * @return single matching group
     */
    public Group getGroup(UUID groupID) {
        return groups.get(groupID);
    }

    public Group getGroupClone(UUID groupID) {
        return new Group(groups.get(groupID));
    }

    public Group getGroupClone(String groupName) {
        List<Group> groupList = groups.values().stream().filter(group -> group.getName().equalsIgnoreCase(groupName)).collect(Collectors.toList());
        if (groupList.isEmpty()) return null;
        return new Group(groupList.get(0));
    }

    /**
     * Get group by its name
     *
     * @param groupName name of the group
     * @return single matching group
     */
    @Nullable
    public Group getGroup(String groupName) {
        List<Group> groupList = groups.values().stream().filter(group -> group.getName().equalsIgnoreCase(groupName)).collect(Collectors.toList());
        if (groupList.isEmpty()) return null;
        return groupList.get(0);
    }

    /**
     * Get groups where the specified player is the owner.
     *
     * @param ownerUUID owners UUID
     * @return List of found groups
     */
    public List<Group> getOwnerGroups(UUID ownerUUID) {
        List<Group> x = groups.values().stream().filter(group -> group.getOwner().equals(ownerUUID)
        ).collect(Collectors.toList());

        return x;

    }

    /**
     * Get groups where the specified player is the member of the group.
     *
     * @param memberUUID uuid of the member
     * @return List of groups
     */
    public List<Group> getMemberGroups(UUID memberUUID) {
        return groups.values().stream().filter(group -> group.getMembers().contains(memberUUID)
        ).collect(Collectors.toList());
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
                                    entry.getValue().getMembers().contains(member))
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
     * Get group by its name
     *
     * @param groupName name of the group
     * @return single matching group
     */
    @Nullable
    @Deprecated
    public Group getGroupByName(String groupName) {
        List<Group> groupList = groups.values().stream().filter(group -> group.getName().equalsIgnoreCase(groupName)).collect(Collectors.toList());
        if (groupList.isEmpty()) return null;
        return groupList.get(0);
    }


    public List<UUID> getPlayerInvites(UUID playerID) {
        return groupInvites.get(playerID);
    }


    /**
     * Invite player to specified group
     *
     * @param groupID UUID of the group
     * @param invitee UUID of the player to invite
     * @param inviter UUID of the player that invited the player
     */
    public void invitePlayerToGroup(UUID groupID, UUID invitee, UUID inviter, Callback<Integer> callback) {
        Main.getDatabase().addInvite(invitee, inviter, groupID, new Callback<Integer>(plugin) {
            @Override
            public void onResult(Integer result) {
                if (result == 1) {
                    groupInvites.computeIfAbsent(invitee, k -> new ArrayList<>()).add(groupID);
                }
                super.onResult(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });

        Main.debug("Inviting player " + Bukkit.getPlayer(invitee).getName() + " to join the group " + getGroups().get(groupID).getName());
    }

    /**
     * Create a group with initial name and owner
     *
     * @param name  name of the group
     * @param owner uuid of the owner/player that created it
     */
    public void createGroup(String name, UUID owner) {
        UUID id = UUID.randomUUID();
        Group group = new Group(id, owner, name, name, new ArrayList<>(Collections.singletonList(owner)));


        Main.debug("Created group with name (" + group.getName() + ")");

        Main.getDatabase().addGroup(group, new Callback<Group>(plugin) {
            @Override
            public void onResult(Group result) {
                Main.debug("Successfully created group");
                groups.put(id, result);
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
        Main.debug("Reloading groups...");

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
    public void deleteGroup(Group group, Callback<UUID> callback) {

        Main.getDatabase().removeGroup(group, new Callback<UUID>(plugin) {
            @Override
            public void onResult(UUID result) {
                if (callback != null) {
                    callback.callSyncResult(group.getId());
                }
                groups.remove(group.getId());
            }

            @Override
            public void onError(Throwable throwable) {
                if (callback != null) {
                    callback.callSyncError(throwable);
                }

            }
        });

    }

    public void updateGroup(Group newGroup, Callback<Integer> callback) {
        Main.getDatabase().addGroup(newGroup, new Callback<Group>(plugin) {
            @Override
            public void onResult(Group result) {
                groups.put(newGroup.getId(), newGroup);
                super.onResult(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });


    }


    public void changeGroupPrefix(UUID id, String prefix) {
        Main.debug("Prefix value: " + prefix);
        Group group = groups.get(id);
        if (group != null) {
            Group groupClone = new Group(group);
            Main.debug("Changing prefix from  " + group.getPrefix() + " to " + groupClone.getPrefix());
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
     * Kick player from group
     *
     * @param groupID  UUID of the group
     * @param playerID UUID of the player to kick
     */
    public void kickPlayer(UUID groupID, UUID playerID) {
        Group group = getGroupClone(groupID);
        group.removeMember(playerID);

        Main.getDatabase().addGroup(group, new Callback<Group>(plugin) {
            @Override
            public void onResult(Group result) {
                Main.debug("Player was successfully kicked from the group");
                groups.put(groupID, group);
                super.onResult(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Main.debug("Failed to kick player from the group");
                super.onError(throwable);
            }
        });


    }




    public void changeGroupName(UUID id, String name) {
        //TODO: create a name validator
        groups.get(id).setName(name);
    }


}
