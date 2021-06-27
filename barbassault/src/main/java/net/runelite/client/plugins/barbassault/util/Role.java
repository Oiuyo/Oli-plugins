package net.runelite.client.plugins.barbassault.util;

import com.google.common.collect.ImmutableMap;
import net.runelite.api.widgets.Widget;
import net.runelite.api.Client;
import java.util.Map;
import net.runelite.api.widgets.WidgetInfo;

public enum Role
{
    ATTACKER(WidgetInfo.BA_ATK_ROLE_TEXT, WidgetInfo.BA_ATK_ROLE_SPRITE, 31784968, 31784971),
    DEFENDER(WidgetInfo.BA_DEF_ROLE_TEXT, WidgetInfo.BA_DEF_ROLE_SPRITE, 31916040, 31916042),
    COLLECTOR(WidgetInfo.BA_COLL_ROLE_TEXT, WidgetInfo.BA_COLL_ROLE_SPRITE, 31850504, 31850506),
    HEALER(WidgetInfo.BA_HEAL_ROLE_TEXT, WidgetInfo.BA_HEAL_ROLE_SPRITE, 31981576, 31981578);

    private final WidgetInfo roleText;
    private final WidgetInfo roleSprite;
    private final int listen;
    private final int call;
    private static final Map<String, String> CALLS_MAP;
    private static final Map<String, Integer> ITEMS_MAP;

    public String getCallEntry(final Client client) {
        final Widget widget = client.getWidget(this.call);
        if (widget == null || widget.isHidden()) {
            return null;
        }
        return Role.CALLS_MAP.getOrDefault(widget.getText(), null);
    }

    public String getListen(final Client client) {
        final Widget widget = client.getWidget(this.listen);
        if (widget == null || widget.isHidden()) {
            return null;
        }
        return widget.getText();
    }

    public int getListenItem(final Client client) {
        final Widget widget = client.getWidget(this.listen);
        if (widget == null || widget.isHidden()) {
            return -1;
        }
        return Role.ITEMS_MAP.getOrDefault(widget.getText(), -1);
    }

    private Role(final WidgetInfo roleText, final WidgetInfo roleSprite, final int listen, final int call) {
        this.roleText = roleText;
        this.roleSprite = roleSprite;
        this.listen = listen;
        this.call = call;
    }

    public WidgetInfo getRoleText() {
        return this.roleText;
    }

    public WidgetInfo getRoleSprite() {
        return this.roleSprite;
    }

    public int getListen() {
        return this.listen;
    }

    public int getCall() {
        return this.call;
    }

    static {
        CALLS_MAP = (Map)new ImmutableMap.Builder().put((Object)"Red egg", (Object)"Tell-red").put((Object)"Green egg", (Object)"Tell-green").put((Object)"Blue egg", (Object)"Tell-blue").put((Object)"Controlled/Bullet/Wind", (Object)"Tell-controlled").put((Object)"Accurate/Field/Water", (Object)"Tell-accurate").put((Object)"Aggressive/Blunt/Earth", (Object)"Tell-aggressive").put((Object)"Defensive/Barbed/Fire", (Object)"Tell-defensive").put((Object)"Tofu", (Object)"Tell-tofu").put((Object)"Crackers", (Object)"Tell-crackers").put((Object)"Worms", (Object)"Tell-worms").put((Object)"Poison Worms", (Object)"Tell-worms").put((Object)"Pois. Worms", (Object)"Tell-worms").put((Object)"Poison Tofu", (Object)"Tell-tofu").put((Object)"Pois. Tofu", (Object)"Tell-tofu").put((Object)"Poison Meat", (Object)"Tell-meat").put((Object)"Pois. Meat", (Object)"Tell-meat").build();
        ITEMS_MAP = (Map)new ImmutableMap.Builder().put((Object)"Tofu", (Object)10514).put((Object)"Crackers", (Object)10513).put((Object)"Worms", (Object)10515).put((Object)"Pois. Worms", (Object)10540).put((Object)"Pois. Tofu", (Object)10539).put((Object)"Pois. Meat", (Object)10541).put((Object)"Defensive/", (Object)22230).put((Object)"Aggressive/", (Object)22229).put((Object)"Accurate/", (Object)22228).put((Object)"Controlled/", (Object)22227).build();
    }
}