/**
 * Copyright (c) 2011, Chicken Zombie Bonanza Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Chicken Zombie Bonanza Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CHICKEN ZOMBIE BONANZA PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ucf.chickenzombiebonanza.game.entity;

import java.util.ArrayList;
import java.util.List;

import ucf.chickenzombiebonanza.common.GeocentricCoordinate;
import ucf.chickenzombiebonanza.common.LocalOrientation;
import ucf.chickenzombiebonanza.game.item.InventoryObject;

public class PowerUpEntity extends GameEntity {
	
	private double activationDistance;
	
	private InventoryObject item;
	
	private final List<ObjectActivationListener> activationListeners = new ArrayList<ObjectActivationListener>(1);
		
	public PowerUpEntity(InventoryObject item, double activationDistance, GeocentricCoordinate position) {
		super(position, new LocalOrientation(), GameEntityTagEnum.POWER_UP);
		this.activationDistance = activationDistance;
	}

	@Override
	public void destroyEntity() {
		super.destroyEntity();
		activationListeners.clear();
	}

	@Override
	public void interactWith(GameEntity entity) {
		if (entity.getPosition().distanceFrom(getPosition()) < activationDistance) {
			if(entity.getTag() == GameEntityTagEnum.LIFEFORM) {
				LifeformEntity lifeform = (LifeformEntity)entity;
				if(!lifeform.isEnemy()) {
	 				lifeform.addItem(item);
					for (ObjectActivationListener i : activationListeners) {
						i.objectActivated(entity);
					}
    				destroyEntity();
    			}
			}
		}
	}
	
	public void addActivationListener(ObjectActivationListener listener) {
		activationListeners.add(listener);
	}
}
