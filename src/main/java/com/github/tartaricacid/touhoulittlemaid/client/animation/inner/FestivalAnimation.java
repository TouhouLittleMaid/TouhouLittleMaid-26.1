package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.ULocale;
import net.minecraft.resources.Identifier;

import java.util.Calendar;
import java.util.HashMap;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class FestivalAnimation {
    private static final ULocale CHINESE = new ULocale("@calendar=chinese");

    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/festival/new_year.js"), getNewYear());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/festival/christmas.js"), getChristmas());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/festival/spring_festival.js"), getSpringFestival());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/festival/duanwu.js"), getDuanwu());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/festival/mid_autumn.js"), getMidAutumn());
    }

    public static IAnimation<EntityMaidRenderState> getNewYear() {
        return (state, models) -> {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
            boolean isNewYear = month == Calendar.JANUARY && dayInMonth == 1;
            togglePair(models, "newYearShow", "newYearHidden", isNewYear);
        };
    }

    public static IAnimation<EntityMaidRenderState> getChristmas() {
        return (state, models) -> {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
            boolean isChristmas = month == Calendar.DECEMBER && (dayInMonth == 24 || dayInMonth == 25);
            togglePair(models, "christmasShow", "christmasHidden", isChristmas);
        };
    }

    public static IAnimation<EntityMaidRenderState> getSpringFestival() {
        return (state, models) -> {
            com.ibm.icu.util.Calendar calendar = ChineseCalendar.getInstance(CHINESE);
            int month = calendar.get(ChineseCalendar.MONTH);
            int dayInMonth = calendar.get(ChineseCalendar.DAY_OF_MONTH);
            // 官方的春节假期是农历年三十到初六
            // 但是有的腊月没有三十……所以我们从廿九算起
            boolean isSpringFestival = (month == ChineseCalendar.DECEMBER && dayInMonth >= 29) || (month == ChineseCalendar.JANUARY && dayInMonth <= 6);
            togglePair(models, "springFestivalShow", "springFestivalHidden", isSpringFestival);
        };
    }

    public static IAnimation<EntityMaidRenderState> getDuanwu() {
        return (state, models) -> {
            com.ibm.icu.util.Calendar calendar = ChineseCalendar.getInstance(CHINESE);
            int month = calendar.get(ChineseCalendar.MONTH);
            int dayInMonth = calendar.get(ChineseCalendar.DAY_OF_MONTH);
            boolean isDuanwu = month == ChineseCalendar.MAY && dayInMonth == 5;
            togglePair(models, "duanwuShow", "duanwuHidden", isDuanwu);
        };
    }

    public static IAnimation<EntityMaidRenderState> getMidAutumn() {
        return (state, models) -> {
            com.ibm.icu.util.Calendar calendar = ChineseCalendar.getInstance(CHINESE);
            int month = calendar.get(ChineseCalendar.MONTH);
            int dayInMonth = calendar.get(ChineseCalendar.DAY_OF_MONTH);
            boolean isMidAutumn = month == ChineseCalendar.AUGUST && dayInMonth == 15;
            togglePair(models, "midAutumnShow", "midAutumnHidden", isMidAutumn);
        };
    }

    private static void togglePair(HashMap<String, BedrockPart> models, String showName, String hiddenName, boolean visible) {
        BedrockPart show = models.get(showName);
        if (show != null) {
            show.visible = visible;
        }

        BedrockPart hidden = models.get(hiddenName);
        if (hidden != null) {
            hidden.visible = !visible;
        }
    }
}
