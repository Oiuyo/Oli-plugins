package net.runelite.client.plugins.barbassault.overlays;

import java.awt.Polygon;
import java.awt.Shape;

import com.google.common.base.Strings;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import java.awt.Color;
import net.runelite.client.plugins.barbassault.util.Role;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.plugins.barbassault.barbassaultConfig;
import net.runelite.client.plugins.barbassault.barbassaultPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;

public class BASceneOverlay extends Overlay
{
    private final Client client;
    private final barbassaultPlugin plugin;
    private final barbassaultConfig config;

    @Inject
    private BASceneOverlay(final Client client, final barbassaultPlugin plugin, final barbassaultConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    public Dimension render(final Graphics2D graphics) {
        if (this.plugin.getInGame() == 0 || this.plugin.getRole() == null) {
            return null;
        }
        final Role role = this.plugin.getRole();
        if (role == Role.COLLECTOR) {
            this.markEggs(graphics, role);
        }
        return null;
    }

    private void markEggs(final Graphics2D graphics, final Role role) {
        if (!this.config.shouldMarkCollectorEggs()) {
            return;
        }
        final String listen = role.getListen(this.client);
        if (!this.plugin.getYellowEggs().isEmpty()) {
            this.plugin.getYellowEggs().forEach((w, c) -> this.drawTile(graphics, w, c, Color.YELLOW));
        }
        if (Strings.isNullOrEmpty(listen) || listen.equals("- - -")) {
            return;
        }
        final String s = listen;
        switch (s) {
            case "Red eggs": {
                this.plugin.getRedEggs().forEach((w, c) -> this.drawTile(graphics, w, c, Color.RED));
                break;
            }
            case "Blue eggs": {
                this.plugin.getBlueEggs().forEach((w, c) -> this.drawTile(graphics, w, c, Color.BLUE));
                break;
            }
            case "Green eggs": {
                this.plugin.getGreenEggs().forEach((w, c) -> this.drawTile(graphics, w, c, Color.GREEN));
                break;
            }
        }
    }

    private void drawTile(final Graphics2D g, final WorldPoint w, final int c, final Color color) {
        final LocalPoint lp = LocalPoint.fromWorld(this.client, w);
        if (lp == null) {
            return;
        }
        final Polygon poly = Perspective.getCanvasTileAreaPoly(this.client, lp, 1);
        if (poly != null) {
            OverlayUtil.renderPolygon(g, (Shape)poly, color);
        }
    }
}