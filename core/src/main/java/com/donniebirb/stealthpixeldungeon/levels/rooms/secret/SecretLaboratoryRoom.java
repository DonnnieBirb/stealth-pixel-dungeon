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

package com.donniebirb.stealthpixeldungeon.levels.rooms.secret;

import com.donniebirb.stealthpixeldungeon.actors.blobs.Alchemy;
import com.donniebirb.stealthpixeldungeon.actors.blobs.Blob;
import com.donniebirb.stealthpixeldungeon.items.EnergyCrystal;
import com.donniebirb.stealthpixeldungeon.items.potions.Potion;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfExperience;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfFrost;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfHaste;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfHealing;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfInvisibility;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfLevitation;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfMindVision;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfParalyticGas;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfPurity;
import com.donniebirb.stealthpixeldungeon.items.potions.PotionOfToxicGas;
import com.donniebirb.stealthpixeldungeon.levels.Level;
import com.donniebirb.stealthpixeldungeon.levels.Terrain;
import com.donniebirb.stealthpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashMap;

public class SecretLaboratoryRoom extends SecretRoom {
	
	private static HashMap<Class<? extends Potion>, Float> potionChances = new HashMap<>();
	static{
		potionChances.put(PotionOfHealing.class,        1f);
		potionChances.put(PotionOfMindVision.class,     2f);
		potionChances.put(PotionOfFrost.class,          3f);
		potionChances.put(PotionOfLiquidFlame.class,    3f);
		potionChances.put(PotionOfToxicGas.class,       3f);
		potionChances.put(PotionOfHaste.class,          4f);
		potionChances.put(PotionOfInvisibility.class,   4f);
		potionChances.put(PotionOfLevitation.class,     4f);
		potionChances.put(PotionOfParalyticGas.class,   4f);
		potionChances.put(PotionOfPurity.class,         4f);
		potionChances.put(PotionOfExperience.class,     6f);
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );
		
		entrance().set( Door.Type.HIDDEN );
		
		Point pot = center();
		Painter.set( level, pot, Terrain.ALCHEMY );

		Blob.seed( pot.x + level.width() * pot.y, 1, Alchemy.class, level );

		int pos;
		do {
			pos = level.pointToCell(random());
		} while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
		level.drop( new EnergyCrystal().random(), pos );

		do {
			pos = level.pointToCell(random());
		} while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
		level.drop( new EnergyCrystal().random(), pos );

		int n = Random.IntRange( 2, 3 );
		HashMap<Class<? extends Potion>, Float> chances = new HashMap<>(potionChances);
		for (int i=0; i < n; i++) {
			do {
				pos = level.pointToCell(random());
			} while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
			
			Class<?extends Potion> potionCls = Random.chances(chances);
			chances.put(potionCls, 0f);
			level.drop( Reflection.newInstance(potionCls), pos );
		}
		
	}
	
}
