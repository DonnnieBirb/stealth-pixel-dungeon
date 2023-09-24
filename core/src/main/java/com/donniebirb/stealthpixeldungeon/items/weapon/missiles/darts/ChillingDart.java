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

package com.donniebirb.stealthpixeldungeon.items.weapon.missiles.darts;

import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.actors.Char;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Buff;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Chill;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;

public class ChillingDart extends TippedDart {
	
	{
		image = ItemSpriteSheet.CHILLING_DART;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//when processing charged shot, only chill enemies
		if (!processingChargedShot || attacker.alignment != defender.alignment) {
			if (Dungeon.level.water[defender.pos]) {
				Buff.prolong(defender, Chill.class, Chill.DURATION);
			} else {
				Buff.prolong(defender, Chill.class, 6f);
			}
		}
		
		return super.proc(attacker, defender, damage);
	}
}