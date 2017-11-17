package rakaneth.wolfsden.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;

public class Factions implements Component
{ 
  public List<String> factions;
  
  public Factions()
  {
    factions = new ArrayList<>();
  }
  
  public Factions(List<String> factions)
  {
    this.factions = new ArrayList<>(factions);
  }
}
