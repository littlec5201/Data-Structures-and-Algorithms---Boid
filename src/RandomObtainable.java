import java.util.NoSuchElementException;

/**
 * @author Callum
 * 
 * Asymptotic complexity for getRandom() is O(n)
 * Asymptotic complexity for removeRandom() is O(n)
 * Asymptotic complexity for insertRandom() is O(n)
 * 
 */
public interface RandomObtainable<E>{
	public E getRandom() throws NoSuchElementException;
	public boolean removeRandom() throws UnsupportedOperationException;
	public boolean insertRandom(E element);
}
