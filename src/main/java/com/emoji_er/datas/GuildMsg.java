package com.emoji_er.datas;

import net.dv8tion.jda.core.entities.Guild;

public class GuildMsg implements Datas{
        final private String text;
        final private boolean reponse;
        final private Guild guild;

    public GuildMsg(String text, Guild guild, boolean reponse) {
        this.text = text;
        this.reponse = reponse;
        this.guild = guild;
    }

    @Override
    public String getText() {
            return text;
    }

    public boolean isReponse() {
        return reponse;
    }

    public Guild getGuild() {
        return guild;
    }
}
