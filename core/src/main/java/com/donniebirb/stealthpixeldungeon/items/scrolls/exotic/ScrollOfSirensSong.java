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

package com.donniebirb.stealthpixeldungeon.items.scrolls.exotic;

import com.donniebirb.stealthpixeldungeon.Assets;
import com.donniebirb.stealthpixeldungeon.Dungeon;
import com.donniebirb.stealthpixeldungeon.actors.Actor;
import com.donniebirb.stealthpixeldungeon.actors.Char;
import com.donniebirb.stealthpixeldungeon.actors.buffs.AllyBuff;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Buff;
import com.donniebirb.stealthpixeldungeon.actors.buffs.Charm;
import com.donniebirb.stealthpixeldungeon.actors.mobs.Mob;
import com.donniebirb.stealthpixeldungeon.effects.Speck;
import com.donniebirb.stealthpixeldungeon.messages.Messages;
import com.donniebirb.stealthpixeldungeon.scenes.CellSelector;
import com.donniebirb.stealthpixeldungeon.scenes.GameScene;
import com.donniebirb.stealthpixeldungeon.sprites.CharSprite;
import com.donniebirb.stealthpixeldungeon.sprites.ItemSpriteSheet;
import com.donniebirb.stealthpixeldungeon.ui.BuffIndicator;
import com.donniebirb.stealthpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfSirensSong extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_SIREN;
	}
	
	@Override
	public void doRead() {
		if (!anonymous) curItem.collect(); //we detach it later
		GameScene.selectCell(targeter);
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null && isKnown()){
				return;
			}

			Mob target = null;
			if (cell != null){
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.alignment != Char.Alignment.ALLY && ch instanceof Mob){
					target = (Mob)ch;
				}
			}

			if (target == null && isKnown() && !anonymous){
				GLog.w(Messages.get(ScrollOfSirensSong.class, "cancel"));
				return;

			} else {

				detach(curUser.belongings.backpack);

				curUser.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
				Sample.INSTANCE.play( Assets.Sounds.CHARMS );
				Sample.INSTANCE.playDelayed( Assets.Sounds.LULLABY, 0.1f );

				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (Dungeon.level.heroFOV[mob.pos] && mob != target && mob.alignment != Char.Alignment.ALLY) {
						Buff.affect( mob, Charm.class, Charm.DURATION ).object = curUser.id();
						mob.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
					}
				}

				if (target != null){
					if (!target.isImmune(Enthralled.class)){
						AllyBuff.affectAndLoot(target, curUser, Enthralled.class);

					} else {
						Buff.affect( target, Charm.class, Charm.DURATION ).object = curUser.id();

					}
					target.sprite.centerEmitter().burst( Speck.factory( Speck.HEART ), 10 );
				} else {
					GLog.w(Messages.get(ScrollOfSirensSong.class, "no_target"));
				}

				identify();

				readAnimation();

			}
		}

		@Override
		public String prompt() {
			return Messages.get(ScrollOfSirensSong.class, "prompt");
		}

	};

	public static class Enthralled extends AllyBuff {

		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.HEARTS);
			else    target.sprite.remove(CharSprite.State.HEARTS);
		}

		@Override
		public int icon() {
			return BuffIndicator.HEART;
		}
	}
	
}
