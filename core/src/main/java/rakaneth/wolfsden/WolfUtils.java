package rakaneth.wolfsden;

import com.badlogic.gdx.ai.GdxAI;

public final class WolfUtils
{
  /**
   * Returns the nth member of the Fibonacci sequence.
   * 
   * @param n
   *          the number of times to iterate
   * @return the nth member of the Fibonacci sequence
   */
  public static final int fibs(int n)
  {
    int current = 1, prev = 1, ante = 0;

    for (int i = 0; i < n; i++)
    {
      current = prev + ante;
      ante = prev;
      prev = current;
    }

    return current;
  }

  /**
   * Returns true if val is between min and max, inclusive.
   * 
   * @param val
   *          The value to check.
   * @param min
   *          The lower bound to check.
   * @param max
   *          The upper bound to check
   * @return True if min <= val <= max, false otherwise.
   */
  public static final <T extends Comparable<? super T>> boolean between(T val, T min, T max)
  {
    if (val.compareTo(min) < 0)
      return false;
    else if (val.compareTo(max) > 0)
      return false;
    else
      return true;
  }

  /**
   * Returns dFault if value is null; value otherwise.
   * 
   * @param value
   *          The value to check.
   * @param dFault
   *          The value to return if value is null.
   * @return dFault if value is null; value otherwise.
   */
  public static <T> T ifNull(T value, T dFault)
  {
    return (value == null ? dFault : value);
  }
  
  public static void log(String tag, String template, Object...args)
  {
    GdxAI.getLogger().info(tag, String.format(template, args));
  }
}
