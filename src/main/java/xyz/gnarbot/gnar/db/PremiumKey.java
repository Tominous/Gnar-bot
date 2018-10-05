package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;

@JsonIgnoreProperties("expiresBy")
public class PremiumKey extends ManagedObject {
    public enum Type {
        PREMIUM("Donor level Premium Access"), // GUILD
        PREMIUM_OVERRIDE("Developer Override Premium Access"), //Eventual user overrides
        USER("OwO what's this");

        final String description;

        Type(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final long duration;
    private final Type type;
    private Redeemer redeemer;

    @ConstructorProperties({"id", "type", "duration", "override"})
    public PremiumKey(String id, Type type, long duration) {
        super(id, "keys");
        this.type = type;
        this.duration = duration;
    }

    public Type getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public PremiumKey setRedeemer(Redeemer redeemer) {
        this.redeemer = redeemer;
        return this;
    }

    public Redeemer getRedeemer() {
        return redeemer;
    }

    @Override
    public String toString() {
        return type + "(" + getId() + ", " + duration + ")";
    }
}
