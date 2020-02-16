package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.resource.Invite;

public class InviteRoute {

  private final String path;

  private InviteRoute(String code) {
    this.path = "/invites/" + code;
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/invite#get-invite">
   *     https://discordapp.com/developers/docs/resources/invite#get-invite</a>
   */
  public Route<Void, Invite> getInvite() {
    return getInvite(true);
  }

  public Route<Void, Invite> getInvite(boolean withCounts) {
    return Route.get(path + ";with_counts=" + withCounts, Invite.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/invite#delete-invite">
   *     https://discordapp.com/developers/docs/resources/invite#delete-invite</a>
   */
  public Route<Void, Invite> deleteInvite() {
    return Route.delete(path, Invite.class);
  }

  public static InviteRoute on(String code) {
    return new InviteRoute(code);
  }

  public static InviteRoute on(Invite invite) {
    return on(invite.getCode());
  }
}
