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

package com.donniebirb.stealthpixeldungeon.items.food;

import com.donniebirb.stealthpixeldungeon.Assets;
import com.donniebirb.stealthpixeldungeon.Badges;
import com.donniebirb.stealthpixeldungeon.Challenges;
import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.Statistics;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Buff;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Hunger;
import com.donniebirb.stealthpixeldungeon.actors.hero.Hero;
import com.donniebirb.stealthpixeldungeon.actors.hero.Talent;
import com.donniebirb.stealthpixeldungeon.effects.SpellSprite;
import com.donniebirb.stealthpixeldungeon.items.Item;
import com.donniebirb.stealthpixeldungeon.items.artifacts.Artifact;
import com.donniebirb.stealthpixeldungeon.items.artifacts.HornOfPlenty;
import com.donniebirb.stealthpixeldungeon.messages.Messages;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;
import com.donniebirb.stealthpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Food extends Item {

	public static final float TIME_TO_EAT	= 3f;
	
	public static final String AC_EAT	= "EAT";
	
	public float energy = Hunger.HUNGRY;
	
	{
		stackable = true;
		image = ItemSpriteSheet.RATION;

		defaultAction = AC_EAT;

		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_EAT );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EAT )) {
			
			detach( hero.belongings.backpack );
			
			satisfy(hero);
			GLog.i( Messages.get(this, "eat_msg") );
			
			hero.sprite.operate( hero.pos );
			hero.busy();
			SpellSprite.show( hero, SpellSprite.FOOD );
			Sample.INSTANCE.play( Assets.Sounds.EAT );
			
			hero.spend( eatingTime() );

			Talent.onFoodEaten(hero, energy, this);
			
			Statistics.foodEaten++;
			Badges.validateFoodEaten();
			
		}
	}

	protected float eatingTime(){
		if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
			|| Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
			|| Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
			|| Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
			|| Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)){
			return TIME_TO_EAT - 2;
		} else {
			return TIME_TO_EAT;
		}
	}
	
	protected void satisfy( Hero hero ){
		float foodVal = energy;
		if (Dungeon.isChallenged(Challenges.NO_FOOD)){
			foodVal /= 3f;
		}

		Artifact.ArtifactBuff buff = hero.buff( HornOfPlenty.hornRecharge.class );
		if (buff != null && buff.isCursed()){
			foodVal *= 0.67f;
			GLog.n( Messages.get(Hunger.class, "cursedhorn") );
		}

		Buff.affect(hero, Hunger.class).satisfy(foodVal);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 10 * quantity;
	}
}
