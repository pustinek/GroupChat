package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.enums.GroupMemberRoles;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import com.pustinek.groupchat.models.GroupMember;
import com.pustinek.groupchat.utils.Callback;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class InvitesManager extends Manager {

    private static ArrayList<GroupInvite> groupInvites = new ArrayList<>();
    private final Main plugin;


    public InvitesManager(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public void shutdown() {
        Main.debug("Cleaning up InvitesManager...");
        groupInvites = null;
    }

    /**
     * (Re)load invites from the database and save them to static variable
     *
     * @param callback return number of loaded invites from database
     */
    public void reloadInvites(final Callback<Integer> callback) {
        Main.getDatabase().connect(new Callback<Integer>(plugin) {
            @Override
            public void onResult(Integer result) {
                Main.getDatabase().getInvites(new Callback<ArrayList<GroupInvite>>(plugin) {
                    @Override
                    public void onResult(ArrayList<GroupInvite> result) {
                        groupInvites = result;
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
     * @param inviteeID UUID of the invited player
     * @param inviteeUsername Username of the invited player
     * @param inviterID UUID of the player that invited the player
     * @param groupID   UUID of the group that the player was invited to
     */
    public void invitePlayerToGroup(UUID inviteeID, String inviteeUsername, UUID inviterID, UUID groupID, Boolean addToDB, boolean redisSync, Callback<GroupInvite> callback) {
        GroupInvite invite = new GroupInvite(groupID, inviteeID, inviterID, inviteeUsername);


        groupInvites.add(invite);
        if (redisSync)
            Main.getRedisManager().addInvitePublish(invite);

        if (addToDB)
            Main.getDatabase().addInvite(invite, new Callback<Integer>(plugin) {
                @Override
                public void onResult(Integer result) {
                    invite.setId(result);
                    if (callback != null) {
                        callback.callSyncResult(invite);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (callback != null) {
                        callback.callSyncError(throwable);
                    }
                    Main.error("Something went wrong when inviting player to group");
                    Main.error(throwable);
                }
            });


    }

    /**
     * @param invite                        Object of the invite
     * @param inviteAccepted                true - if player accepted and wants to join the group, else false
     * @param removeInviteFromDatabase      true - it will remove the invitee entry from database
     * @param redisSync                      send data via redis to other servers
     */
    public void respondToGroupInvite(GroupInvite invite, boolean inviteAccepted, boolean removeInviteFromDatabase, boolean redisSync) {
        boolean wasRemove = groupInvites.remove(invite);


        if (inviteAccepted) {
            Group group = Main.getGroupManager().getGroupClone(invite.getGroupID());
            if (group == null) {
                // Invalid group, remove it
                groupInvites.remove(invite);
                Main.getDatabase().removeInvite(invite.getId(), null);
                return;
            }

            GroupMember groupMember = new GroupMember(invite.getInviteeUsername(), invite.getInviteeID(), GroupMemberRoles.MEMBER);
            // Don't allow duplicate members !
            if (group.getMembers().contains(groupMember)) {
                Main.warrning("Player " + groupMember.getUsername() + " tried to join a group " + group.getName() + ", But is already a member !");
                return;
            }
            group.addMember(groupMember);
            Main.getChatManager().sendGenericGroupMessage(group, (groupMember.getUsername() + " Joined the group. "));
            Main.getGroupManager().updateGroup(group, removeInviteFromDatabase);
            if (redisSync) Main.getRedisManager().responseToGroupInvitePublish(invite, true);
        } else {
            if (redisSync) Main.getRedisManager().responseToGroupInvitePublish(invite, false);
        }

        if (removeInviteFromDatabase && wasRemove) {
            Main.getDatabase().removeInvite(invite.getId(), new Callback<Integer>(plugin) {
                @Override
                public void onResult(Integer result) {
                    super.onResult(result);
                }

                @Override
                public void onError(Throwable throwable) {
                    Main.error(throwable);
                    super.onError(throwable);
                }
            });
        }


    }

    /**
     * Retrieve all the group invites the player has
     *
     * @param playerID UUID of the player that requests to see his invites
     */
    public ArrayList<GroupInvite> getPlayerGroupInvites(UUID playerID) {
        return groupInvites.stream().filter(invite -> invite.getInviteeID().equals(playerID)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieve all the players that have pending invite to a group
     *
     * @param groupUUID UUID of the group that whishes to invite
     */
    public ArrayList<GroupInvite> getGroupPlayerInvites(UUID groupUUID) {
        ArrayList<GroupInvite> result = new ArrayList<>();
        result = groupInvites.stream().filter(invite -> invite.getGroupID().equals(groupUUID)).collect(Collectors.toCollection(ArrayList::new));
        return result;
    }
}
