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

package com.donniebirb.stealthpixeldungeon.actors.mobs;

import com.donniebirb.stealthpixeldungeon.Assets;
import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.actors.Actor;
import com.donniebirb.stealthpixeldungeon.effects.CellEmitter;
import com.donniebirb.stealthpixeldungeon.effects.Speck;
import com.donniebirb.stealthpixeldungeon.items.EquipableItem;
import com.donniebirb.stealthpixeldungeon.items.Heap;
import com.donniebirb.stealthpixeldungeon.items.Item;
import com.donniebirb.stealthpixeldungeon.items.armor.Armor;
import com.donniebirb.stealthpixeldungeon.items.artifacts.Artifact;
import com.donniebirb.stealthpixeldungeon.items.wands.Wand;
import com.donniebirb.stealthpixeldungeon.items.weapon.Weapon;
import com.donniebirb.stealthpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.donniebirb.stealthpixeldungeon.messages.Messages;
import com.donniebirb.stealthpixeldungeon.sprites.MimicSprite;
import com.donniebirb.stealthpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class GoldenMimic extends Mimic {

	{
		spriteClass = MimicSprite.Golden.class;
	}

	@Override
	public String name() {
		if (alignment == Alignment.NEUTRAL){
			return Messages.get(Heap.class, "locked_chest");
		} else {
			return super.name();
		}
	}

	@Override
	public String description() {
		if (alignment == Alignment.NEUTRAL){
			return Messages.get(Heap.class, "locked_chest_desc") + "\n\n" + Messages.get(this, "hidden_hint");
		} else {
			return super.description();
		}
	}

	public void stopHiding(){
		state = HUNTING;
		if (sprite != null) sprite.idle();
		if (Actor.chars().contains(this) && Dungeon.level.heroFOV[pos]) {
			enemy = Dungeon.hero;
			target = Dungeon.hero.pos;
			GLog.w(Messages.get(this, "reveal") );
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 0.85f);
		}
	}

	@Override
	public void setLevel(int level) {
		super.setLevel(Math.round(level*1.33f));
	}

	@Override
	protected void generatePrize() {
		super.generatePrize();
		//all existing prize items are guaranteed uncursed, and have a 50% chance to be +1 if they were +0
		for (Item i : items){
			if (i instanceof EquipableItem || i instanceof Wand){
				i.cursed = false;
				i.cursedKnown = true;
				if (i instanceof Weapon && ((Weapon) i).hasCurseEnchant()){
					((Weapon) i).enchant(null);
				}
				if (i instanceof Armor && ((Armor) i).hasCurseGlyph()){
					((Armor) i).inscribe(null);
				}
				if (!(i instanceof MissileWeapon || i instanceof Artifact) && i.level() == 0 && Random.Int(2) == 0){
					i.upgrade();
				}
			}
		}
	}
}
