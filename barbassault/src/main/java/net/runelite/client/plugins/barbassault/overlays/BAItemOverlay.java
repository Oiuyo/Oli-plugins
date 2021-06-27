package net.runelite.client.plugins.barbassault.overlays;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import net.runelite.client.plugins.barbassault.util.Role;
import java.awt.Shape;
import net.runelite.api.widgets.WidgetItem;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.barbassault.barbassaultConfig;
import net.runelite.client.plugins.barbassault.barbassaultPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class BAItemOverlay extends WidgetItemOverlay
{
    private final Client client;
    private final barbassaultPlugin plugin;
    private final barbassaultConfig config;

    @Inject
    private BAItemOverlay(final Client client, final barbassaultPlugin plugin, final barbassaultConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.showOnInventory();
        this.showOnEquipment();
    }

    public void renderItemOverlay(final Graphics2D graphics, final int itemId, final WidgetItem widgetItem) {
        if (this.plugin.getInGame() == 0 || this.plugin.getRole() == null) {
            return;
        }
        final Role role = this.plugin.getRole();
        final int id = role.getListenItem(this.client);
        final Color color = getColor(this.config, role);
        if (id == -1 || color == null) {
            return;
        }
        if (itemId == id) {
            this.outlineBounds(graphics, widgetItem.getCanvasBounds(), color);
        }
    }

    private static Color getColor(final barbassaultConfig config, final Role role) {
        switch (role) {
            case ATTACKER: {
                return config.shouldMarkArrows() ? config.getArrowMarkerColor() : null;
            }
            case DEFENDER: {
                return config.shouldMarkBait() ? config.getBaitMarkerColor() : null;
            }
            case HEALER: {
                return config.shouldMarkPoison() ? config.getPoisonMarkerColor() : null;
            }
            default: {
                return null;
            }
        }
    }

    private void outlineBounds(final Graphics2D g, final Shape s, final Color c) {
        if (s == null) {
            return;
        }
        g.setColor(c);
        g.setStroke(new BasicStroke(1.0f));
        g.draw(s);
    }
}