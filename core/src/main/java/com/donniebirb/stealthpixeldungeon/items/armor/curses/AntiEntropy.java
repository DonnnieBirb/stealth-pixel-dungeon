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

package com.donniebirb.stealthpixeldungeon.items.armor.curses;

import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.actors.Char;
import com.donniebirb.stealthpixeldungeon.actors.blobs.Freezing;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Buff;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Burning;
import com.donniebirb.stealthpixeldungeon.effects.particles.FlameParticle;
import com.donniebirb.stealthpixeldungeon.items.armor.Armor;
import com.donniebirb.stealthpixeldungeon.items.armor.Armor.Glyph;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSprite;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class AntiEntropy extends Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/8f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {

			for (int i : PathFinder.NEIGHBOURS8){
				Freezing.affect(defender.pos+i);
			}

			if (!Dungeon.level.water[defender.pos]) {
				Buff.affect(defender, Burning.class).reignite(defender, 4);
			}
			defender.sprite.emitter().burst( FlameParticle.FACTORY, 5 );

		}
		
		return damage;
	}

	@Override
	public Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
