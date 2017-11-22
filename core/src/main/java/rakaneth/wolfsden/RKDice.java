package rakaneth.wolfsden;

import java.util.Arrays;

import squidpony.squidmath.Dice;

public class RKDice
{
  private static Dice dice = new Dice(WolfGame.rng);
  private int         roll;
  private int         keep;

  /**
   * Constructor for manual building. Creates a 0k0 roll.
   */
  public RKDice()
  {
    setRoll(0);
    setKeep(0);
  }

  /**
   * Constructor providing initial roll and keep values. This is the most
   * commonly-used constructor.
   * 
   * @param roll
   *          The number of dice to roll; the left side of the k.
   * @param keep
   *          The number of dice to keep; the right side of the k.
   */
  public RKDice(int roll, int keep)
  {
    setRoll(roll);
    setKeep(keep);
  }

  /**
   * Constructor taking a dice string in the form of XkY.
   * 
   * @param diceString
   *          The string to convert. Must be in XkY form.
   */
  public RKDice(String diceString)
  {
    this();
    String[] parts = diceString.split("k");
    if (parts.length == 2)
    {
      setRoll(Integer.parseInt(parts[0]));
      setKeep(Integer.parseInt(parts[1]));
    }
  }

  /**
   * Gets the result of the roll.
   * 
   * @return The result of the roll of these dice.
   */
  public int roll()
  {
    int realKeep = Math.min(roll, keep);
    if (realKeep > 0)
      return dice.bestOf(realKeep, roll, "!6");
    return 0;
  }

  /**
   * Gets the number of successes of these dice rolled against a difficulty. <br>
   * Every 5 over the difficulty is an additional success.
   * 
   * @param diff
   * @return The number of successes rolled.
   */
  public int roll(int diff)
  {
    int result = roll() - diff;
    return (result >= 0) ? (result / 5) + 1 : 0;
  }

  /**
   * Adds another RKDice instance to this one. <br>
   * The roll values are added together, then the keep values are added together.
   * <br>
   * The result cannot be less than 1k1.
   * 
   * @param rk
   * @return a new RKDice instance that is the result of adding this to rk.
   */
  public RKDice add(RKDice rk)
  {
    int newRoll = roll + rk.roll;
    int newKeep = keep + rk.keep;
    return new RKDice(newRoll, newKeep);
  }

  /**
   * Statically rolls XkY without creating an instance.
   * 
   * @param diceString
   *          The dice string to roll, in XkY format.
   * @return The result of the roll.
   */
  public static int roll(String diceString)
  {
    String[] parts = diceString.split("k");
    if (parts.length != 2)
      return 0;
    else
    {
      int roll = Integer.parseInt(parts[0]);
      int keep = Integer.parseInt(parts[1]);
      int realKeep = Math.min(roll, keep);
      if (realKeep > 0)
        return dice.bestOf(realKeep, roll, "!6");
    }
    return 0;
  }

  /**
   * Statically rolls XkY versus a difficulty of diff, counting successes as in
   * {@link #roll(int) roll}.
   * 
   * @param diceString
   *          The dice string to roll, in XkY format.
   * @param diff
   *          The difficulty of the roll.
   * @return The number of successes.
   */
  public static int roll(String diceString, int diff)
  {
    int raw = roll(diceString) - diff;
    return (raw >= 0) ? (raw / 5) + 1 : 0;
  }

  /**
   * Statically add 2 RKDIce values without requiring an instance.
   * 
   * @param d1
   *          The first addend.
   * @param d2
   *          The second addend.
   * @return The RKDice result of d1 + d2.
   */
  public static RKDice add(RKDice d1, RKDice d2)
  {
    int totalRoll = d1.roll + d2.roll;
    int totalKeep = d1.keep + d2.keep;
    return new RKDice(totalRoll, totalKeep);
  }

  /**
   * Statically add multiple RKDice values without requiring an instance.
   * 
   * @param dice
   *          The list of RKDice to add.
   * @return The RKDice sum of all arguments.
   */
  public static RKDice add(RKDice... dice)
  {
    return Arrays.stream(dice)
                 .reduce(new RKDice(), (d1, d2) -> add(d1, d2));
  }

  /**
   * Manually sets the number of dice to roll.
   * 
   * @param val
   *          The number to set roll to.
   */
  public void setRoll(int val)
  {
    roll = val;
  }

  /**
   * Manually sets the number of dice to keep.
   * 
   * @param val
   *          The number to set keep to.
   */
  public void setKeep(int val)
  {
    keep = val;
  }

  /**
   * Manually sets roll and keep as a unit.
   * 
   * @param roll
   *          The number to set roll to.
   * @param keep
   *          The number to set keep to.
   */
  public void set(int roll, int keep)
  {
    setRoll(roll);
    setKeep(keep);
  }

  @Override
  public String toString()
  {
    return String.format("%dk%d", roll, keep);
  }

  @Override
  public boolean equals(Object other)
  {
    if (other instanceof RKDice)
      return this.roll == ((RKDice) other).roll && this.keep == ((RKDice) other).keep;
    else if (other == null)
      return false;
    else
      return false;
  }

}
