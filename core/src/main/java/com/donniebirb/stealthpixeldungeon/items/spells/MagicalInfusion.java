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

package com.donniebirb.stealthpixeldungeon.items.spells;

import com.donniebirb.stealthpixeldungeon.Badges;
import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.Statistics;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Degrade;
import com.donniebirb.stealthpixeldungeon.actors.hero.Talent;
import com.donniebirb.stealthpixeldungeon.items.Item;
import com.donniebirb.stealthpixeldungeon.items.armor.Armor;
import com.donniebirb.stealthpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.donniebirb.stealthpixeldungeon.items.wands.Wand;
import com.donniebirb.stealthpixeldungeon.items.weapon.Weapon;
import com.donniebirb.stealthpixeldungeon.messages.Messages;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;
import com.donniebirb.stealthpixeldungeon.utils.GLog;

public class MagicalInfusion extends InventorySpell {
	
	{
		image = ItemSpriteSheet.MAGIC_INFUSE;

		unique = true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable();
	}

	@Override
	protected void onItemSelected( Item item ) {

		ScrollOfUpgrade.upgrade(curUser);

		Degrade.detach( curUser, Degrade.class );

		if (item instanceof Weapon && ((Weapon) item).enchantment != null) {
			((Weapon) item).upgrade(true);
		} else if (item instanceof Armor && ((Armor) item).glyph != null) {
			((Armor) item).upgrade(true);
		} else {
			boolean wasCursed = item.cursed;
			boolean wasCurseInfused = item instanceof Wand && ((Wand) item).curseInfusionBonus;
			item.upgrade();
			if (wasCursed) item.cursed = true;
			if (wasCurseInfused) ((Wand) item).curseInfusionBonus = true;
		}
		
		GLog.p( Messages.get(this, "infuse") );
		Talent.onUpgradeScrollUsed( Dungeon.hero );
		Badges.validateItemLevelAquired(item);

		Statistics.upgradesUsed++;
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return (50 + 40) * quantity;
	}
	
	public static class Recipe extends com.donniebirb.stealthpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{ScrollOfUpgrade.class, ArcaneCatalyst.class};
			inQuantity = new int[]{1, 1};
			
			cost = 4;
			
			output = MagicalInfusion.class;
			outQuantity = 1;
		}
		
	}
}
