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

package com.donniebirb.stealthpixeldungeon.plants;

import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.actors.Char;
import com.donniebirb.stealthpixeldungeon.actors.blobs.Blob;
import com.donniebirb.stealthpixeldungeon.actors.blobs.ToxicGas;
import com.donniebirb.stealthpixeldungeon.actors.buffs.AdrenalineSurge;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Buff;
import com.donniebirb.stealthpixeldungeon.actors.hero.Hero;
import com.donniebirb.stealthpixeldungeon.actors.hero.HeroSubClass;
import com.donniebirb.stealthpixeldungeon.effects.CellEmitter;
import com.donniebirb.stealthpixeldungeon.effects.particles.LeafParticle;
import com.donniebirb.stealthpixeldungeon.scenes.GameScene;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;

public class Rotberry extends Plant {

	{
		image = 0;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {
		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
			Buff.affect(ch, AdrenalineSurge.class).reset(1, AdrenalineSurge.DURATION);
		} else {
			GameScene.add( Blob.seed( pos, 100, ToxicGas.class ) );
		}
	}
	
	@Override
	public void wither() {
		Dungeon.level.uproot( pos );
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
		}

		//seed always drops, no lotus benefit
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
	}

	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SEED_ROTBERRY;

			plantClass = Rotberry.class;

			unique = true;
		}
		
		@Override
		public int value() {
			return 30 * quantity;
		}

		@Override
		public int energyVal() {
			return 3 * quantity;
		}
	}
}
