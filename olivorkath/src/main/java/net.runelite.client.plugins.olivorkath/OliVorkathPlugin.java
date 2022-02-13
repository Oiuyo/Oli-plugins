package net.runelite.client.plugins.olivorkath;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
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
	private PlayerUtils playerUtils;

	@Inject
	private PrayerUtils prayerUtils;

	@Inject
	private CalculationUtils calc;

	Player player;

	private Rectangle prayBounds;

	private Rectangle runOrbBounds;

	private int timeout;

	public static long sleepLength;

	public static int tickLength;

	private NPC vorkath;

	private List<WorldPoint> acidSpots = new ArrayList<>();

	private List<WorldPoint> acidFreePath = new ArrayList<>();

	private final Set<Integer> DIAMOND_SET = Set.of(ItemID.DIAMOND_DRAGON_BOLTS_E, ItemID.DIAMOND_BOLTS_E);

	private final Set<Integer> RUBY_SET = Set.of(ItemID.RUBY_DRAGON_BOLTS_E, ItemID.RUBY_BOLTS_E);

	boolean firstWalk;

	boolean attackingVork;




	public OliVorkathPlugin() {
		firstWalk = true;
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

	private long sleepDelay() {
		sleepLength = calc.randomDelay(config.sleepWeightedDistribution(), config.sleepMin(), config.sleepMax(), config.sleepDeviation(), config.sleepTarget());
		return sleepLength;
	}

	private int tickDelay() {
		int tickLength = (int) calc.randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
		log.debug("tick delay for {} ticks", tickLength);
		return tickLength;
	}

	public void attackVorkath() {
		if(vorkath.getAnimation() != 7957 && vorkath.getAnimation() != 7960){
		utils.doNpcActionMsTime(vorkath, MenuAction.NPC_SECOND_OPTION.getId(), 0 );
		}
	}

	public void equipDiamond() {
		WidgetItem diamondBolts = inventory.getWidgetItem(DIAMOND_SET);
		utils.doItemActionMsTime(diamondBolts, MenuAction.ITEM_SECOND_OPTION.getId(), 9764864, 100);
	}

	public void equipRuby() {
		WidgetItem rubyBolts = inventory.getWidgetItem(RUBY_SET);
		utils.doItemActionMsTime(rubyBolts, MenuAction.ITEM_SECOND_OPTION.getId(), 9764864, 100);
	}


	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		if (npc.getName() == null) {
			return;
		}

		if (npc.getName().equals("Vorkath")) {
			vorkath = event.getNpc();
		}

		if (npc.getName().equals("Zombified Spawn") && config.killSpawn()) {
			LegacyMenuEntry entry = new LegacyMenuEntry("Cast", "", npc.getIndex(), MenuAction.SPELL_CAST_ON_NPC.getId(), 0, 0, false);
			utils.oneClickCastSpell(WidgetInfo.SPELL_CRUMBLE_UNDEAD, entry, npc.getConvexHull().getBounds(), 100);
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) // IF an NPC is despawned/dead
	{
		final NPC npc = event.getNpc();

		if (npc.getName() == null) {
			return;
		}

		Widget widget = client.getWidget(10485775);
		if (widget != null) {
			prayBounds = widget.getBounds();
		}

		Widget runWidget = client.getWidget(10485783);
		if (runWidget != null) {
			runOrbBounds = runWidget.getBounds();
		}

		if (npc.getName().equals("Vorkath")) { // in this case if vorkath dies

			vorkath = null;

			if (config.switchBolts() && !playerUtils.isItemEquipped(RUBY_SET) && inventory.containsItem(RUBY_SET)) // switch bolts back to ruby after vorkath dies
			{
				equipRuby();
			}
			if (config.enablePrayer()&& client.getVar(Varbits.QUICK_PRAYER) == 1) {
				LegacyMenuEntry entry = new LegacyMenuEntry("Deactivate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);
				menu.setEntry(entry);
				mouse.click(prayBounds);
				if (!prayerUtils.isQuickPrayerActive())
					prayerUtils.toggleQuickPrayer(true, 0);
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
		GameObject acidPool = new GameObjectQuery().idEquals(ObjectID.ACID_POOL_32000).result(client).nearestTo(client.getLocalPlayer());
		NPC zombifiedSpawn = new NPCQuery().idEquals(NpcID.ZOMBIFIED_SPAWN_8063).result(client).nearestTo(client.getLocalPlayer());
		NPC vorkath = new NPCQuery().idEquals(NpcID.VORKATH_8061).result(client).nearestTo(client.getLocalPlayer());

		if (timeout > 0)
		{
			timeout--;
		}

		if (config.enablePrayer() && client.getVar(Varbits.QUICK_PRAYER) == 0 && vorkath.getAnimation() !=7957){
			LegacyMenuEntry entry = new LegacyMenuEntry("Activate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);
			menu.setEntry(entry);
			mouse.click(prayBounds);
			if (!prayerUtils.isQuickPrayerActive())
				prayerUtils.toggleQuickPrayer(true, 0);
		}

		if(vorkath.getAnimation() != 7957){ // Clearing acid spots and the no acid path AFTER vorkath spitting animation stops, so the acid walk automation can stop

			acidFreePath.clear(); // clear the calculated path

			acidSpots.clear(); // clear the tiles with acid

			firstWalk = false; // we are not doing acid walk

			/*

			if(!playerUtils.isRunEnabled()){
				playerUtils.enableRun(runOrbBounds); // enable run}
			}

			*/
		}

		if (vorkath != null && calculateHealth(vorkath) > 0) // Switch bolts based on HP of Vorkath + auto retaliate if config allows us
		{
			if (calculateHealth(vorkath) < 260 && config.switchBolts()
					&& vorkath.getAnimation() != 7960
					&& vorkath.getAnimation() != 7957
					&& zombifiedSpawn == null && acidPool == null) {
				if (!playerUtils.isItemEquipped(DIAMOND_SET) && inventory.containsItem(DIAMOND_SET)) {
					equipDiamond();
					if (config.fastRetaliate()) {
						attackVorkath();
					}
				}
			}
		}

		if (timeout == 1 && config.fastRetaliate() && zombifiedSpawn == null && acidSpots.isEmpty()) { // Faster retaliate if config allows us when a timeout = tickdelay = 1
			attackVorkath();
		}

		if (config.acidWalk() && !acidSpots.isEmpty() && vorkath.getAnimation() == 7957) // Acid walk automation handling on game tick
		{
			if (config.enablePrayer() && client.getVar(Varbits.QUICK_PRAYER) == 1) // Disable prayer inside of acid walk automation
			{
				LegacyMenuEntry entry = new LegacyMenuEntry("Deactivate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);
				menu.setEntry(entry);
				mouse.click(prayBounds);
				if (prayerUtils.isQuickPrayerActive())
					prayerUtils.toggleQuickPrayer(false, 0);
				return;
			}

			/*   // This might be a bit too much on the same tick? not sure

			if(playerUtils.isRunEnabled()){
				playerUtils.enableRun(runOrbBounds); // disable run
			}

			*/

			calculateAcidFreePath(); // Calculate the free tiles without acid - thanks to xKylee

			if (firstWalk) { // we walk to the first tile of the whole length of acid free tiles
				walk.sceneWalk(acidFreePath.get(1),0,100);
				firstWalk = false;
				return;
			}

			if (!firstWalk) { // we walk to the configurable tile minus 1 (standard 3 (but numbers work different so it's 4-1))
				walk.sceneWalk(acidFreePath.get(config.acidFreePathLength() -1 ),0,100 );
				firstWalk = true;
				return;
			}

			return;
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

		if (widget != null)
		{
			prayBounds = widget.getBounds();
		}

		String prayerMessage = ("Your prayers have been disabled!");
		String spawnExplode = ("The spawn violently explodes, unfreezing you as it does so.");
		String unfrozenMessage = ("You become unfrozen as you kill the spawn.");
		String poisonMessage = ("You have been poisoned by venom!");
		String poisonMessageNV = ("You have been poisoned!");
		if ((event.getMessage().equals(prayerMessage)) && config.enablePrayer() && client.getVar(Varbits.QUICK_PRAYER) == 0 ) // if the prayer disabled message appears we activate prayer
		{
			LegacyMenuEntry entry = new LegacyMenuEntry("Activate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);
			menu.setEntry(entry);
			mouse.click(prayBounds);
			if (!prayerUtils.isQuickPrayerActive())
				prayerUtils.toggleQuickPrayer(true, 0);
		}
		if ((event.getMessage().equals(spawnExplode) || (event.getMessage().equals(unfrozenMessage)))) // if we get unfrozen or the spawn explodes we auto retaliate
		{
			if (config.fastRetaliate())
			{
				attackVorkath();
			}
		}
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged event)
	{
		if (vorkath != null)
		{
			final Actor actor = event.getActor();
			if (actor.getAnimation() == 7950 && actor.getName().contains("Vorkath")) // if vorkath wakes up
			{
				Widget widget = client.getWidget(10485775);

				if (widget != null)
				{
					prayBounds = widget.getBounds();
				}

				if (config.enablePrayer()&& client.getVar(Varbits.QUICK_PRAYER) == 0) // turn on prayer of it's off
				{
					LegacyMenuEntry entry = new LegacyMenuEntry("Activate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);
					menu.setEntry(entry);
					mouse.click(prayBounds);
					if (!prayerUtils.isQuickPrayerActive())
						prayerUtils.toggleQuickPrayer(true, 0);
				}
				if (config.switchBolts() && !playerUtils.isItemEquipped(RUBY_SET) && inventory.containsItem(RUBY_SET))// switch bolts to ruby if not equipped already
				{
					equipRuby();
				}
			}
			if (actor.getAnimation() == 7949 && actor.getName().contains("Vorkath"))
			{
				if (config.enablePrayer()&& client.getVar(Varbits.QUICK_PRAYER) == 1)
				{
					LegacyMenuEntry entry = new LegacyMenuEntry("Deactivate", "Quick-prayers", 1, MenuAction.CC_OP.getId(), -1, 10485775, false);
					menu.setEntry(entry);
					mouse.click(prayBounds);
					if (prayerUtils.isQuickPrayerActive())
						prayerUtils.toggleQuickPrayer(false, 0);
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

	@Subscribe
	private void onProjectileMoved(final ProjectileMoved event) {
		Projectile projectile = event.getProjectile();
		LocalPoint position = event.getPosition();
		WorldPoint.fromLocal(
				client,
				position);

		client.getLocalPlayer().getLocalLocation();
		LocalPoint fromWorld = LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation());

		if (projectile.getId() == 1483){
			addAcidSpot(WorldPoint.fromLocal(client, position));}
		}



		private void addAcidSpot(WorldPoint worldPoint) {
		if (!acidSpots.contains(worldPoint))
			acidSpots.add(worldPoint);}

	private void calculateAcidFreePath() {
		acidFreePath.clear();
		if (vorkath == null)
			return;
		int[][][] array = { { { 0, 1 }, { 0, -1 } }, { { 1, 0 }, { -1, 0 } } };
		ArrayList<WorldPoint> bestPath = new ArrayList<>();
		double bestClicksRequired = 99.0D;
		WorldPoint worldLocation = client.getLocalPlayer().getWorldLocation();
		WorldPoint worldLocation2 = vorkath.getWorldLocation();
		int n2 = worldLocation2.getX() + 14;
		int n3 = worldLocation2.getX() - 8;
		int n4 = worldLocation2.getY() - 1;
		int n5 = worldLocation2.getY() - 8;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				WorldPoint worldPoint = new WorldPoint(
						worldLocation.getX() + i,
						worldLocation.getY() + j,
						worldLocation.getPlane());
				if (!acidSpots.contains(worldPoint)
						&& worldPoint.getY() >= n5
						&& worldPoint.getY() <= n4)
					for (int l = 0; l < 2; l++) {
						double clicksRequired;
						if ((clicksRequired = (Math.abs(i) + Math.abs(j))) < 2.0D)
							clicksRequired += (Math.abs(j * array[l][0][0]) + Math.abs(i * array[l][0][1]));
						if (l == 0)
							clicksRequired += 0.5D;
						ArrayList<WorldPoint> currentPath;
						(currentPath = new ArrayList<>()).add(worldPoint);
						for (int n7 = 1; n7 < 25; n7++) {
							WorldPoint worldPoint2 = new WorldPoint(
									worldPoint.getX() + n7 * array[l][0][0],
									worldPoint.getY() + n7 * array[l][0][1],
									worldPoint.getPlane());

							if (acidSpots.contains(worldPoint2)
									|| worldPoint2.getY() < n5
									|| worldPoint2.getY() > n4
									|| worldPoint2.getX() < n3
									|| worldPoint2.getX() > n2)
								break;
							currentPath.add(worldPoint2);
						}
						for (int n8 = 1; n8 < 25; n8++) {
							WorldPoint worldPoint3 = new WorldPoint(
									worldPoint.getX() + n8 * array[l][1][0],
									worldPoint.getY() + n8 * array[l][1][1],
									worldPoint.getPlane());

							if (acidSpots.contains(worldPoint3)
									|| worldPoint3.getY() < n5
									|| worldPoint3.getY() > n4
									|| worldPoint3.getX() < n3
									|| worldPoint3.getX() > n2)
								break;
							currentPath.add(worldPoint3);
						}

						if ((currentPath.size() >= config.acidFreePathLength() && clicksRequired < bestClicksRequired)
								|| (clicksRequired == bestClicksRequired && currentPath.size() > bestPath.size())) {
							bestPath = currentPath;
							bestClicksRequired = clicksRequired;
						}
					}
			}
		}
		if (bestClicksRequired != 99.0D)
			acidFreePath = bestPath;
	}
}