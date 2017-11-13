package rakaneth.wolfsden.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class Inventory implements Component
{
	public int capacity;
	public List<Entity> inventory;
	
	public Inventory(int capacity)
	{
		this.capacity = capacity;
		inventory = new ArrayList<>();
	}
	
	public Inventory()
	{
		inventory = new ArrayList<>();
		capacity = 10;
	}
	
	public boolean atMax()
	{
		return inventory.size() >= capacity; 
	}
	
	public boolean add(Entity e)
	{		
		if (atMax())
			return false;
		else
		{
			e.remove(Position.class);
			e.remove(Drawing.class);
			inventory.add(e);
			return true;
		}
	}
	
	public void remove(Entity e)
	{
		inventory.remove(e);
	}
	
}
