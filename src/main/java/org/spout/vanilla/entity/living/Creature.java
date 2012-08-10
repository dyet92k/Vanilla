/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, VanillaDev <http://www.spout.org/>
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.entity.living;

import org.spout.api.tickable.LogicPriority;
import org.spout.api.tickable.TickPriority;

import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.entity.VanillaControllerType;
import org.spout.vanilla.entity.component.basic.CreatureHealthComponent;
import org.spout.vanilla.entity.component.basic.GrowComponent;
import org.spout.vanilla.entity.component.basic.HealthComponent;

public abstract class Creature extends Living {
	private int lineOfSight = 1;
	protected GrowComponent growComponent;

	protected Creature(VanillaControllerType type) {
		super(type);
	}

	@Override
	public void onAttached() {
		super.onAttached();

		removeComponent(HealthComponent.class);
		healthComponent = (HealthComponent) addComponent(new CreatureHealthComponent(TickPriority.HIGHEST)).getClass();
		growComponent = (GrowComponent) addComponent(new GrowComponent(TickPriority.HIGHEST).getClass());
		lineOfSight = getDataMap().get(VanillaData.LINE_OF_SIGHT);
	}

	@Override
	public void onSave() {
		super.onSave();
		getDataMap().put(VanillaData.LINE_OF_SIGHT, lineOfSight);
	}

	@Override
	public CreatureHealthComponent getHealth() {
		return (CreatureHealthComponent) healthProcess;
	}

	public GrowComponent getGrowing() {
		return growProcess;
	}

	/**
	 * Gets the line of sight this creature has.
	 * @return
	 */
	public int getLineOfSight() {
		return lineOfSight;
	}

	/**
	 * Sets the line of sight this creature has
	 * @param lineOfSight
	 */
	public void setLineOfSight(int lineOfSight) {
		this.lineOfSight = lineOfSight;
	}
}
