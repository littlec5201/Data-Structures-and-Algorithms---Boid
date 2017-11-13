import java.util.*;

/**
 * @author Callum
 *
 */
public class TestDriver<E> extends ArrayList<E> implements RandomObtainable<E>{
	static TestDriver<String> td = new TestDriver<String>();
	private Random rand;
	private List<E> list;
	
	public TestDriver(){
		rand = new Random();
		list = new ArrayList<E>();
	}
	
	@Override
	public E getRandom() throws NoSuchElementException {
		try {	
			int randomNum = rand.nextInt(list.size());
			System.out.println("About to return element \"" + list.get(randomNum) + "\" from index " + randomNum);
			return list.get(randomNum);
		} catch (IllegalArgumentException e) {
			System.out.println("Could not return an element");
			return null;
		}
	}

	@Override
	public boolean removeRandom() throws UnsupportedOperationException {
		try {
			int randomNum = rand.nextInt(list.size());
			System.out.println("About to remove element \"" + td.list.get(randomNum) + "\" from index " + randomNum);
			list.remove(randomNum);
			System.out.println(td.list);
			return true;
		} catch (IllegalArgumentException e) {
			System.out.println("Could not remove a random element");
			System.out.println(td.list);
			return false;
		}
	}

	@Override
	public boolean insertRandom(E element) {
		try {
			int randomNum = rand.nextInt(list.size() + 1);
			System.out.println("About to insert element \"" + element + "\" at index " + randomNum);
			list.add(randomNum, element);
			System.out.println(td.list);
			return true;
		} catch (IllegalArgumentException e) {
			System.out.println("Could not insert \"" + element + "\"");
			return false;
		}
	}
	
	public static void main(String args[]){
		int x = 0;
		int userResponse = 0;
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Welcome to TestDriver");
		while (x == 0){
			System.out.println("Please select one of the following options:");
			System.out.println("1) Return a random element in the list");
			System.out.println("2) Remove a random element from the list");
			System.out.println("3) Insert an element into the list");
			System.out.println("4) Quit");
			
			userResponse = keyboard.nextInt();
			
			if (userResponse == 1){
				System.out.println(td.getRandom() + "\n");
			}
			else if (userResponse == 2){
				System.out.println(td.removeRandom() + "\n");
			}
			else if (userResponse == 3){
				String element = "";
				System.out.println("Please type the element you would like to add");
				keyboard.nextLine();
				element = keyboard.nextLine();
				System.out.println(td.insertRandom(element));
				System.out.println();
			}
			else if (userResponse == 4){
				System.out.println("Goodbye");
				break;
			}
		}
	}
}
