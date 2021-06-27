/*
 * Copyright (c) 2020 ImNoOSRS <https://github.com/ImNoOSRS>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.barbassault;

import net.runelite.client.config.Alpha;
import java.awt.Color;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.Config;

@ConfigGroup("barbassaultadditions")
public interface barbassaultConfig extends Config
{
	public static final String GROUP_NAME = "barbassaultadditions";
	@ConfigSection(name = "Attacker", description = "", position = 0, closedByDefault = true)
	public static final String attackerSection = "attackerSection";
	@ConfigSection(name = "Defender", description = "", position = 1, closedByDefault = true)
	public static final String defenderSection = "defenderSection";
	@ConfigSection(name = "Collector", description = "", position = 2, closedByDefault = true)
	public static final String collectorSection = "collectorSection";
	@ConfigSection(name = "Healer", description = "", position = 3, closedByDefault = true)
	public static final String healerSection = "healerSection";

	@ConfigItem(name = "Callouts MES", keyName = "baCalloutsMES", description = "Allows you to one-click callout", position = 4)
	default boolean shouldLeftClickCall() {
		return false;
	}

	@ConfigItem(name = "Check If Gay", keyName = "isGay", description = "Check this option if you're gay", position = 5)
	default boolean isGay() {
		return false;
	}

	@ConfigItem(name = "Hide Unused Menus (SoonTM)", keyName = "baHideUnusedMenus", description = "Big Project CBA ATM", position = 6)
	default boolean shouldHideUnusedMenus() {
		return false;
	}

	@ConfigItem(name = "Mark Called Arrows", keyName = "baMarkCalledArrows", description = "", position = 0, section = "attackerSection")
	default boolean shouldMarkArrows() {
		return false;
	}

	@ConfigItem(name = "Arrow Marker Color", keyName = "baArrowMarkerColor", description = "", position = 1, section = "attackerSection")
	default Color getArrowMarkerColor() {
		return Color.WHITE;
	}

	@ConfigItem(name = "Remove Incorrect Styles (SoonTM)", keyName = "baRemoveIncorrectStyles", description = "Hides the wrong attack style for Scythe, Dinh's, Dragon Claws and Crystal Halberd", position = 2, section = "attackerSection")
	default boolean shouldRemoveIncStyles() {
		return false;
	}

	@ConfigItem(name = "Mark Called Bait", keyName = "baMarkCalledBait", description = "Highlights bait called by your teammate", position = 0, section = "defenderSection")
	default boolean shouldMarkBait() {
		return false;
	}

	@ConfigItem(name = "Bait Marker Color", keyName = "baBaitMarkerColor", description = "Configures the color to highlight the called bait", position = 1, section = "defenderSection")
	default Color getBaitMarkerColor() {
		return Color.WHITE;
	}

	@ConfigItem(name = "Deprioritize Bait", keyName = "baDeprioritizeBait", description = "Moves 'Take' menu option for all bait below 'Walk Here'", position = 2, section = "defenderSection")
	default boolean shouldDeprioBait() {
		return false;
	}

	@ConfigItem(name = "Deprioritize Incorrect Eggs", keyName = "baDeprioritizeIncorrectEggs", description = "Shifts the 'Take' entry for each uncalled egg under 'Walk Here'", position = 0, section = "collectorSection")
	default boolean shouldDeprioIncEggs() {
		return false;
	}

	@ConfigItem(name = "Mark Collector Eggs", keyName = "baMarkCollectorEggs", description = "Marks the called eggs for collectors", position = 1, section = "collectorSection")
	default boolean shouldMarkCollectorEggs() {
		return false;
	}

	@ConfigItem(name = "Mark Called Poison", keyName = "baMarkCalledPoison", description = "Displays an overlay on the called poison", position = 0, section = "healerSection")
	default boolean shouldMarkPoison() {
		return false;
	}

	@ConfigItem(name = "Poison Marker Color", keyName = "baPoisonMarkerColor", description = "Configures the color for the overlay on marked poison", position = 1, section = "healerSection")
	@Alpha
	default Color getPoisonMarkerColor() {
		return Color.WHITE;
	}

	@ConfigItem(name = "Healer Codes (SoonTM)", keyName = "baHealerCodes", description = "Big Project CBA ATM", position = 2, section = "healerSection")
	default boolean showHealerCodes() {
		return false;
	}
}