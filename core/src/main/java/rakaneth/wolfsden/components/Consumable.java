package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.ItemBuilder.ItemBase.ItemType;

public class Consumable implements Component
{
  public int stack;
  public float value;
  public ItemType type;
  
  public Consumable(int stack, float value, ItemType type)
  {
    this.stack = stack;
    this.value = value;
    this.type = type;
  }
}
