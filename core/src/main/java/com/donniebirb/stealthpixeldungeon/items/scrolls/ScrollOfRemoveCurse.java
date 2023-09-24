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

package com.donniebirb.stealthpixeldungeon.items.scrolls;

import com.donniebirb.stealthpixeldungeon.Assets;
import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.actors.Actor;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Degrade;
import com.donniebirb.stealthpixeldungeon.actors.hero.Belongings;
import com.donniebirb.stealthpixeldungeon.actors.hero.Hero;
import com.donniebirb.stealthpixeldungeon.actors.mobs.TormentedSpirit;
import com.donniebirb.stealthpixeldungeon.effects.Flare;
import com.donniebirb.stealthpixeldungeon.effects.particles.ShadowParticle;
import com.donniebirb.stealthpixeldungeon.items.EquipableItem;
import com.donniebirb.stealthpixeldungeon.items.Item;
import com.donniebirb.stealthpixeldungeon.items.armor.Armor;
import com.donniebirb.stealthpixeldungeon.items.wands.Wand;
import com.donniebirb.stealthpixeldungeon.items.weapon.Weapon;
import com.donniebirb.stealthpixeldungeon.messages.Messages;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;
import com.donniebirb.stealthpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class ScrollOfRemoveCurse extends InventoryScroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_REMCURSE;
		preferredBag = Belongings.Backpack.class;
	}

	@Override
	public void doRead() {
		TormentedSpirit spirit = null;
		for (int i : PathFinder.NEIGHBOURS8){
			if (Actor.findChar(curUser.pos+i) instanceof TormentedSpirit){
				spirit = (TormentedSpirit) Actor.findChar(curUser.pos+i);
			}
		}
		if (spirit != null){
			identify();
			Sample.INSTANCE.play( Assets.Sounds.READ );
			readAnimation();

			new Flare( 6, 32 ).show( curUser.sprite, 2f );

			if (curUser.buff(Degrade.class) != null) {
				Degrade.detach(curUser, Degrade.class);
			}

			GLog.p(Messages.get(this, "spirit"));
			spirit.cleanse();
		} else {
			super.doRead();
		}
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return uncursable(item);
	}

	public static boolean uncursable( Item item ){
		if (item.isEquipped(Dungeon.hero) && Dungeon.hero.buff(Degrade.class) != null) {
			return true;
		} if ((item instanceof EquipableItem || item instanceof Wand) && ((!item.isIdentified() && !item.cursedKnown) || item.cursed)){
			return true;
		} else if (item instanceof Weapon){
			return ((Weapon)item).hasCurseEnchant();
		} else if (item instanceof Armor){
			return ((Armor)item).hasCurseGlyph();
		} else {
			return false;
		}
	}

	@Override
	protected void onItemSelected(Item item) {
		new Flare( 6, 32 ).show( curUser.sprite, 2f );

		boolean procced = uncurse( curUser, item );

		if (curUser.buff(Degrade.class) != null) {
			Degrade.detach(curUser, Degrade.class);
			procced = true;
		}

		if (procced) {
			GLog.p( Messages.get(this, "cleansed") );
		} else {
			GLog.i( Messages.get(this, "not_cleansed") );
		}
	}

	public static boolean uncurse( Hero hero, Item... items ) {
		
		boolean procced = false;
		for (Item item : items) {
			if (item != null) {
				item.cursedKnown = true;
				if (item.cursed) {
					procced = true;
					item.cursed = false;
				}
			}
			if (item instanceof Weapon){
				Weapon w = (Weapon) item;
				if (w.hasCurseEnchant()){
					w.enchant(null);
					procced = true;
				}
			}
			if (item instanceof Armor){
				Armor a = (Armor) item;
				if (a.hasCurseGlyph()){
					a.inscribe(null);
					procced = true;
				}
			}
			if (item instanceof Wand){
				((Wand) item).updateLevel();
			}
		}
		
		if (procced && hero != null) {
			hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
			hero.updateHT( false ); //for ring of might
			updateQuickslot();
		}
		
		return procced;
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}