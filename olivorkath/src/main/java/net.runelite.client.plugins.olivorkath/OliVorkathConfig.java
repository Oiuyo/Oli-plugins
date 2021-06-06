/*
 * Copyright (c) 2018, Andrew EP | ElPinche256 <https://github.com/ElPinche256>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.olivorkath;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("OliVorkathConfig")

public interface OliVorkathConfig extends Config
{
	@ConfigItem(
		keyName = "enablePrayer",
		name = "Re-enable quick prayers",
		description = "Re-enables quick prayers after the pink dragonfire attack.",
		position = 0
	)
	default boolean enablePrayer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "dodgeBomb",
			name = "Dodge fire bombs",
			description = "Dodges the vertical fire bomb attack.",
			position = 1
	)
	default boolean dodgeBomb()
	{
		return true;
	}

	@ConfigItem(
			keyName = "killSpawn",
			name = "Cast crumble undead on spawn",
			description = "Casts crumble undead when the Zombified spawn appears.",
			position = 2
	)
	default boolean killSpawn()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fastRetaliate",
			name = "Faster retaliate",
			description = "Attacks Vorkath after moving/unfreezing faster.",
			position = 3
	)
	default boolean fastRetaliate()
	{
		return true;
	}

/*	@ConfigItem(
			keyName = "switchBolts",
			name = "Switch bolts",
			description = "Switches bolts at the ideal health threshold.",
			position = 4
	)
	default boolean switchBolts()
	{
		return false;
	}*/
}