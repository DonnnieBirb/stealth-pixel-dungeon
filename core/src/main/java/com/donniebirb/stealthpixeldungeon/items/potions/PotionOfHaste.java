/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.donniebirb.stealthpixeldungeon.items.potions;

import com.donniebirb.stealthpixeldungeon.actors.buffs.Buff;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Haste;
import com.donniebirb.stealthpixeldungeon.actors.hero.Hero;
import com.donniebirb.stealthpixeldungeon.effects.SpellSprite;
import com.donniebirb.stealthpixeldungeon.messages.Messages;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;
import com.donniebirb.stealthpixeldungeon.utils.GLog;

public class PotionOfHaste extends Potion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_HASTE;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();
		
		GLog.w( Messages.get(this, "energetic") );
		Buff.prolong( hero, Haste.class, Haste.DURATION);
		SpellSprite.show(hero, SpellSprite.HASTE, 1, 1, 0);
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}