package com.github.princesslana.eriscasper.examples;

import com.github.princesslana.eriscasper.Bot;
import com.github.princesslana.eriscasper.BotContext;
import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.action.Actions;
import com.github.princesslana.eriscasper.data.Users;
import com.github.princesslana.eriscasper.data.event.MessageCreateEvent;
import io.reactivex.Completable;

public class EchoBot implements Bot {

  @Override
  public Completable apply(BotContext ctx) {
    return ctx.getEvents()

        // Same type as PingBot in examples
        .ofType(MessageCreateEvent.class)
        .map(MessageCreateEvent::unwrap)

        // Need to check for bot's own message
        .filter(d -> !Users.isBot(d.getAuthor()))
        .filter(d -> d.getContent().map(c -> c.startsWith("+echo")).orElse(false))
        .flatMapCompletable(
            d -> {
              String replyMessage =
                  d.getContent().map(c -> c.replaceFirst("\\+echo", "")).orElse("");

              // Empty and Invalid Arguments
              if (replyMessage.trim().isEmpty()) {
                replyMessage = "This command requires 1 argument";
              } else if (replyMessage.charAt(0) != ' ') {
                replyMessage = "Invalid Command";
              }
              return ctx.execute(Actions.sendMessage(d.getChannelId(), replyMessage));
            });
  }

  public static void main(String args[]) {
    ErisCasper.create().run(new EchoBot());
  }
}
