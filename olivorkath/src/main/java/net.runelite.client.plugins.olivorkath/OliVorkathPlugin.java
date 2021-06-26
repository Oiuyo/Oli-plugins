package net.runelite.client.plugins.olivorkath;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.iutils.*;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	private InventoryUtils inventory;

	@Inject
	private MenuUtils menu;

	@Inject
	private MouseUtils mouse;

	@Inject
	private PlayerUtils player;

	private Rectangle bounds;

	private int timeout;

	private NPC vorkath;

	private List<WorldPoint> acidSpots = new ArrayList<>();

	private List<WorldPoint> acidFreePath = new ArrayList<>();

	private final Set<Integer> DIAMOND_SET = Set.of(ItemID.DIAMOND_DRAGON_BOLTS_E, ItemID.DIAMOND_BOLTS_E);

	private final Set<Integer> RUBY_SET = Set.of(ItemID.RUBY_DRAGON_BOLTS_E, ItemID.RUBY_BOLTS_E);


	public OliVorkathPlugin() {
	}

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
	private void onNpcDespawned(NpcDespawned event)
	{
		final NPC npc = event.getNpc();

		if (npc.getName() == null)
		{
			return;
		}

		Widget widget = client.getWidget(10485775);

		if (widget != null)
		{
			bounds = widget.getBounds();
		}

		if (npc.getName().equals("Vorkath"))
		{
			vorkath = null;
				if (config.switchBolts() && !player.isItemEquipped(RUBY_SET) && inventory.containsItem(RUBY_SET))
				{
					WidgetItem rubyBolts = inventory.getWidgetItem(RUBY_SET);
					utils.doItemActionMsTime(rubyBolts, MenuAction.ITEM_SECOND_OPTION.getId(), 9764864, 100);
				}
				if (config.enablePrayer() && client.getVar(Varbits.QUICK_PRAYER) == 1)
				{
					MenuEntry entry = new MenuEntry("Deactivate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);

					menu.setEntry(entry);

					mouse.click(bounds);
				}
		}
	}

	@Subscribe
	private void onProjectileSpawned(ProjectileSpawned event)
	{
		final Projectile projectile = event.getProjectile();

		final WorldPoint loc = client.getLocalPlayer().getWorldLocation();

		final LocalPoint localLoc = LocalPoint.fromWorld(client, loc);

		if (projectile.getId() == ProjectileID.VORKATH_BOMB_AOE && config.dodgeBomb())
		{
			LocalPoint dodgeRight = new LocalPoint(localLoc.getX() + 256, localLoc.getY());
			LocalPoint dodgeLeft = new LocalPoint(localLoc.getX() - 256, localLoc.getY());
			if (localLoc.getX() < 6208)
			{
				walk.sceneWalk(dodgeRight, 0, 100);
			} else {
				walk.sceneWalk(dodgeLeft, 0, 100);
			}
			timeout = 4;
		}
		if (projectile.getId() == ProjectileID.VORKATH_ICE)
		{
			walk.sceneWalk(localLoc, 0, 100);
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (timeout > 0)
		{
			timeout--;
		}
		if (vorkath != null && calculateHealth(vorkath) > 0)
		{
			if (calculateHealth(vorkath) < 260 && config.switchBolts())
			{
				if (!player.isItemEquipped(DIAMOND_SET) && inventory.containsItem(DIAMOND_SET))
				{
					WidgetItem diamondBolts = inventory.getWidgetItem(DIAMOND_SET);
					utils.doItemActionMsTime(diamondBolts, MenuAction.ITEM_SECOND_OPTION.getId(), 9764864, 100);
					if (config.fastRetaliate())
					{
						utils.doNpcActionMsTime(vorkath, MenuAction.NPC_SECOND_OPTION.getId(), 200);
					}
				}
			}
		}

		if (timeout == 1 && config.fastRetaliate()) {
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

		Widget widget = client.getWidget(10485775);

		MenuEntry entry = new MenuEntry("Activate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);

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
		 if ((event.getMessage().equals(spawnExplode) || (event.getMessage().equals(unfrozenMessage))))
		{
			if (config.fastRetaliate())
			{
				utils.doNpcActionMsTime(vorkath, MenuAction.NPC_SECOND_OPTION.getId(), 100);
			}
		}
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged event)
	{
		if (vorkath != null)
		{
			final Actor actor = event.getActor();
			if (actor.getAnimation() == 7950 && actor.getName().contains("Vorkath"))
			{
				Widget widget = client.getWidget(10485775);

				if (widget != null)
				{
					bounds = widget.getBounds();
				}

				if (config.enablePrayer() && client.getVar(Varbits.QUICK_PRAYER) == 0)
				{
					MenuEntry entry = new MenuEntry("Activate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);

					menu.setEntry(entry);

					mouse.click(bounds);
				}
				if (config.switchBolts() && !player.isItemEquipped(RUBY_SET) && inventory.containsItem(RUBY_SET))
				{
					WidgetItem rubyBolts = inventory.getWidgetItem(RUBY_SET);
					utils.doItemActionMsTime(rubyBolts, MenuAction.ITEM_SECOND_OPTION.getId(), 9764864, 100);
				}
			}
			if (actor.getAnimation() == 7949 && actor.getName().contains("Vorkath"))
			{
				if (config.enablePrayer() && client.getVar(Varbits.QUICK_PRAYER) == 1)
				{
					MenuEntry entry = new MenuEntry("Deactivate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);

					menu.setEntry(entry);

					mouse.click(bounds);
				}
			}
		}
	}

	private int calculateHealth(NPC target)
	{
		// Based on OpponentInfoOverlay HP calculation & taken from the default slayer plugin
		if (target == null || target.getName() == null)
		{
			return -1;
		}

		final int healthScale = target.getHealthScale();
		final int healthRatio = target.getHealthRatio();
		final Integer maxHealth = 750;

		if (healthRatio < 0 || healthScale <= 0 || maxHealth == null)
		{
			return -1;
		}

		return (int)((maxHealth * healthRatio / healthScale) + 0.5f);
	}
}
