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
import ucf.chickenzombiebonanza.common.sensor.OrientationPublisher;
import ucf.chickenzombiebonanza.common.sensor.PositionPublisher;
import ucf.chickenzombiebonanza.game.item.HasInventory;
import ucf.chickenzombiebonanza.game.item.HealthInventoryObject;
import ucf.chickenzombiebonanza.game.item.InventoryObject;
import ucf.chickenzombiebonanza.game.item.WeaponInventoryObject;
import ucf.chickenzombiebonanza.game.item.InventoryObject.InventoryObjectTypeEnum;

/**
 * @author Jolene
 *
 */
public class LifeformEntity extends GameEntity implements HasInventory {
	
	private int maxHealth, currentHealth;
	
	private boolean isEnemy = false;
	
	private LifeformEntityStateEnum state;
	
	private final List<InventoryObject> inventory = new ArrayList<InventoryObject>();
	
	private final List<LifeformHealthListener> healthListeners = new ArrayList<LifeformHealthListener>();
	
	public LifeformEntity(boolean isEnemy, int health, PositionPublisher positionPublisher, OrientationPublisher orientationPublisher) {
		super(positionPublisher, orientationPublisher, GameEntityTagEnum.LIFEFORM);
		this.isEnemy = isEnemy;
		this.maxHealth = health;
		this.currentHealth = health;
		this.state = LifeformEntityStateEnum.ALIVE;
	}
	
	public LifeformEntity(boolean isEnemy, int health, GeocentricCoordinate position, LocalOrientation orientation){
		super(position, orientation, GameEntityTagEnum.LIFEFORM);
		this.isEnemy = isEnemy;
		this.maxHealth = health;
		this.currentHealth = health;
		this.state = LifeformEntityStateEnum.ALIVE;
	}	
	
	LifeformEntityStateEnum getStatus(){
		return state;
	}
	
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		setHealth(currentHealth);
	}
	
	private void setHealth(int currentHealth){
		this.currentHealth = currentHealth > maxHealth ? maxHealth : currentHealth;
		this.currentHealth = this.currentHealth >= 0 ? this.currentHealth : 0;
		updateHealthListeners();
	}
	
	public int getCurrentHealth() {
		return this.currentHealth;
	}
	
	public boolean isDead() {
		return this.getCurrentHealth() <= 0;
	}
	
	void setStatus(LifeformEntityStateEnum newstatus){
		this.state = newstatus;
	}
	
	public boolean isEnemy() {
		return isEnemy;
	}

	@Override
	public void interactWith(GameEntity entity) {
		if(entity.getTag() == GameEntityTagEnum.LIFEFORM) {
			LifeformEntity lifeform = (LifeformEntity)entity;
			if(this.isEnemy() && !lifeform.isEnemy()) {
				lifeform.damageEntity(5);
			}
		}
	}

	@Override
	public boolean addItem(InventoryObject item) {
		if (!isEnemy()) {
			if (item.getType() == InventoryObjectTypeEnum.WEAPON) {
				WeaponInventoryObject addWeapon = (WeaponInventoryObject) item;
				for (WeaponInventoryObject i : getWeapons()) {
					if (item.equals(i)) {
						i.addAmmo(addWeapon.getAmmoLeft());
						return true;
					}
				}
				
			} else if (item.getType() == InventoryObjectTypeEnum.HEALTH) {
				HealthInventoryObject addHealth = (HealthInventoryObject) item;
				for (HealthInventoryObject i : getHealthPacks()) {
					if (item.equals(i)) {
						i.incrementCount(addHealth.getCount());
						return true;
					}
				}
			}
			
			synchronized (inventory) {
				inventory.add(item);
			}
			return true;
		}
		return false;
	}

	@Override
	public void destroyEntity() {
		super.destroyEntity();		
	}
	
	public void damageEntity(int damage) {
		setHealth(currentHealth - damage);
		if(currentHealth <= 0) {
			destroyEntity();
		}
	}
	
	public void healEntity() {
		setHealth(maxHealth);
	}
	
	public void healEntity(int amount) {
		setHealth(currentHealth + amount);
	}
	
	public List<WeaponInventoryObject> getWeapons() {
		List<WeaponInventoryObject> weapons = new ArrayList<WeaponInventoryObject>();
		synchronized(inventory) {
			for(InventoryObject i : inventory) {
				if(i.getType() == InventoryObjectTypeEnum.WEAPON) {
					weapons.add((WeaponInventoryObject)i);
				}
			}
		}
		return weapons;
	}
	
	public List<HealthInventoryObject> getHealthPacks() {
		List<HealthInventoryObject> healthPacks = new ArrayList<HealthInventoryObject>();
		synchronized(inventory) {
			for(InventoryObject i : inventory) {
				if(i.getType() == InventoryObjectTypeEnum.HEALTH) {
					healthPacks.add((HealthInventoryObject)i);
				}
			}
		}
		return healthPacks;
	}
	
	public void registerHealthListener(LifeformHealthListener listener) {
	    synchronized(healthListeners) {
	        if(!healthListeners.contains(listener)) {
	            healthListeners.add(listener);
	        }
        }
    }

    public void unregisterHealthListener(LifeformHealthListener listener) {
        synchronized (healthListeners) {
            healthListeners.remove(listener);
        }
    }
    
    private void updateHealthListeners() {
        synchronized (healthListeners) {
            for(LifeformHealthListener i : healthListeners) {
                i.onLifeformHealthChange(getCurrentHealth(), maxHealth);
            }
        }
    }
    
    public void updateHealthListener(LifeformHealthListener listener) {
        listener.onLifeformHealthChange(getCurrentHealth(), maxHealth);
    }

    @Override
    public boolean useItem(InventoryObject item) {
        synchronized (inventory) {
            for (InventoryObject i : inventory) {
                if (i.equals(item)) {
                    if (item.getType() == InventoryObjectTypeEnum.HEALTH) {
                        HealthInventoryObject healthObject = (HealthInventoryObject) item;
                        if (healthObject.use()) {
                            this.healEntity(healthObject.getHealingAmount());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
}
