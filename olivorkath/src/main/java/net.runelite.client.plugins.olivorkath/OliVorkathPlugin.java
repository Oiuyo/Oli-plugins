package net.runelite.client.plugins.olivorkath;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.iutils.MenuUtils;
import net.runelite.client.plugins.iutils.MouseUtils;
import net.runelite.client.plugins.iutils.WalkUtils;
import net.runelite.client.plugins.iutils.iUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;

@Extension
@PluginDependency(iUtils.class)
@PluginDescriptor(
	name = "OliVorkath",
	description = "Some QoL to make Vorkath more AFK",
	tags = {"oli", "oiuyo", "vork", "vorkath"}
)
@Slf4j
public class OliVorkathPlugin extends Plugin
{
	@Inject
	private OliVorkathConfig config;

	@Inject
	private Client client;

	@Inject
	private iUtils utils;

	@Inject
	private WalkUtils walk;

	@Inject
	private MenuUtils menu;

	@Inject
	private MouseUtils mouse;

	private Rectangle bounds;

	private int timeout;

	private NPC vorkath;

	@Provides
	OliVorkathConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OliVorkathConfig.class);
	}

	@Override
	protected void startUp()
	{

	}

	@Override
	protected void shutDown()
	{

	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		if (npc.getName() == null)
		{
			return;
		}

		if (npc.getName().equals("Vorkath"))
		{
			vorkath = event.getNpc();
		}

		if (npc.getName().equals("Zombified Spawn") && config.killSpawn())
		{
			MenuEntry entry = new MenuEntry("Cast", "", npc.getIndex(), MenuAction.SPELL_CAST_ON_NPC.getId(), 0, 0, false);
			utils.oneClickCastSpell(WidgetInfo.SPELL_CRUMBLE_UNDEAD, entry, npc.getConvexHull().getBounds(), 100);
		}
	}


	@Subscribe
	private void onProjectileSpawned(ProjectileSpawned event)
	{
		final Projectile projectile = event.getProjectile();
		if (projectile.getId() == ProjectileID.VORKATH_BOMB_AOE && config.dodgeBomb()) {
			final WorldPoint loc = client.getLocalPlayer().getWorldLocation();
			final LocalPoint localLoc = LocalPoint.fromWorld(client, loc);
			LocalPoint dodgeRight = new LocalPoint(localLoc.getX() + 256, localLoc.getY());
			LocalPoint dodgeLeft = new LocalPoint(localLoc.getX() - 256, localLoc.getY());
			if (localLoc.getX() < 6208) {
				walk.sceneWalk(dodgeRight, 0, 100);
			} else {
				walk.sceneWalk(dodgeLeft, 0, 100);
			}
			timeout = 4;
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (timeout > 0)
		{
			timeout--;
		}
		if (timeout == 1 && config.fastRetaliate()) {
			//menu.setEntry(new MenuEntry("Attack", "<col=ffff00>Vorkath<col=ff0000>  (level-732)", 30040, MenuAction.NPC_SECOND_OPTION.getId(), 0, 0, false));
			utils.doNpcActionMsTime(vorkath, MenuAction.NPC_SECOND_OPTION.getId(), 0 );
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		Widget widget = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);

		MenuEntry entry = new MenuEntry("Activate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485774, false);

		if (widget != null)
		{
			bounds = widget.getBounds();
		}

		String prayerMessage = ("Your prayers have been disabled!");
		String spawnExplode = ("The spawn violently explodes, unfreezing you as it does so.");
		String unfrozenMessage = ("You become unfrozen as you kill the spawn.");

		if ((event.getMessage().equals(prayerMessage) || event.getMessage().contains(prayerMessage)) && config.enablePrayer())
		{
			menu.setEntry(entry);
			mouse.click(bounds);
		}
		else if ((event.getMessage().equals(spawnExplode) || (event.getMessage().equals(unfrozenMessage))) && config.fastRetaliate())
		{
			//menu.setEntry(new MenuEntry("Attack", "<col=ffff00>Vorkath<col=ff0000>  (level-732)", 30040, MenuAction.NPC_SECOND_OPTION.getId(), 0, 0, false));
			utils.doNpcActionMsTime(vorkath, MenuAction.NPC_SECOND_OPTION.getId(), 0 );
		}
	}

}
