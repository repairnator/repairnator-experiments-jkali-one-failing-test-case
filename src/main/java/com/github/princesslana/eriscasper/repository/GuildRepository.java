package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Guild;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface GuildRepository {
  Maybe<Guild> getGuild(Snowflake id);

  Observable<Guild> getGuilds();
}
