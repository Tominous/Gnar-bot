package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.BotLoader;

import javax.annotation.Nullable;

import static com.rethinkdb.RethinkDB.r;

@JsonSerialize
@JsonDeserialize
public abstract class ManagedObject {
    private final String id;
    @JsonIgnore
    private final String table;

    @JsonIgnore
    public ManagedObject(String id, String table) {
        this.id = id;
        this.table = table;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void delete() {
        BotLoader.BOT.getDb().deleteData(table, Long.valueOf(id));
    }

    @JsonIgnore
    public void save() {
        BotLoader.BOT.getDb().insertData(table, Long.valueOf(id), this);
    }
}