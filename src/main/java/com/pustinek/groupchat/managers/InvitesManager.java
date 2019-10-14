package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
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
        Main.debug("Reloading invites...");

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
     * @param inviterID UUID of the player that invited the player
     * @param groupID   UUID of the group that the player was invited to
     */
    public void invitePlayerToGroup(UUID inviteeID, UUID inviterID, UUID groupID, Callback<GroupInvite> callback) {
        GroupInvite invite = new GroupInvite(groupID, inviteeID, inviterID);

        Main.getDatabase().addInviteX(invite, new Callback<Integer>(plugin) {
            @Override
            public void onResult(Integer result) {
                invite.setId(result);
                groupInvites.add(invite);
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
     * @param invite         Object of the invite
     * @param inviteAccepted true - if player accepted and wants to join the group, else false
     */
    public void respondToGroupInvite(GroupInvite invite, Boolean inviteAccepted) {
        //TODO: remove entry from DB and local map

        Main.getDatabase().removeInvite(invite.getId(), new Callback<Integer>(plugin) {
            @Override
            public void onResult(Integer result) {
                groupInvites.remove(invite);
                if (inviteAccepted) {
                    Group group = Main.getGroupManager().getGroupClone(invite.getGroupID());
                    group.addMember(invite.getInviteeID());
                    Main.getGroupManager().updateGroup(group, null);
                }
                super.onResult(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });

    }

    /**
     * Retrieve all the group invites the player has
     *
     * @param playerID UUID of the player that requests to see his invites
     */
    public ArrayList<GroupInvite> getPlayerGroupInvites(UUID playerID) {
        ArrayList<GroupInvite> result = new ArrayList<>();
        result = groupInvites.stream().filter(invite -> invite.getInviteeID().equals(playerID)).collect(Collectors.toCollection(ArrayList::new));
        return result;
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
