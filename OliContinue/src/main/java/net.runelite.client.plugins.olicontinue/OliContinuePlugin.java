//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.olicontinue;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;


@Extension
@PluginDescriptor(
		name = "OliContinue",
		description = "Continues dialog, credit to ben93riggs.",
		tags = {"oli", "oiuyo", "continue", "quest"}
)
@Slf4j
public class OliContinuePlugin extends Plugin {

	@Inject
	private Client client;

	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget widget = getDialog();

		if (widget != null && !widget.isHidden())
		{
			pressSpace();
		}

	}

	public Widget getDialog() {
		Widget widget = client.getWidget(WidgetInfo.DIALOG_NPC_CONTINUE);

		if (widget != null && !widget.isHidden()) {
			return widget;
		}

		widget = client.getWidget(WidgetInfo.DIALOG_PLAYER_CONTINUE);

		if (widget != null && !widget.isHidden()) {
			return widget;
		}

		widget = client.getWidget(WidgetInfo.DIALOG2_SPRITE_CONTINUE);

		if (widget != null && !widget.isHidden())
		{
			return widget;
		}

		widget = client.getWidget(WidgetInfo.DIALOG_NOTIFICATION_CONTINUE);

		if (widget != null && !widget.isHidden())
		{
			return widget;
		}

		widget = client.getWidget(193, 0);

		if (widget != null && !widget.isHidden())
		{
			return widget;
		}

		return null;
	}

	public void pressSpace()
	{
		KeyEvent keyPress = new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE);
		this.client.getCanvas().dispatchEvent(keyPress);
		KeyEvent keyRelease = new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE);
		this.client.getCanvas().dispatchEvent(keyRelease);
		KeyEvent keyTyped = new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE);
		this.client.getCanvas().dispatchEvent(keyTyped);
	}
	}