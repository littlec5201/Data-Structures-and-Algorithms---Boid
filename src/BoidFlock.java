import java.awt.Graphics;
import java.util.*;
/**
 * @author Callum
 *
 */

public class BoidFlock {
	public int DETECTRADIUS;
	private List<Boid> boidList;
	private static Random rand = new Random();
	private static BoidGUI boidGUI = new BoidGUI();
	
	public BoidFlock(int numBoidsToStart){
		this.boidList = new TestDriver<Boid>();
		for(int i = 0; i < numBoidsToStart; i++){
			this.addBoidToFlock();
		}
	}
	
	public static int randInt(int min, int max){
		int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public void addBoidToFlock(){
		Boid boid = new Boid(this, randInt(0, boidGUI.getCanvasWidth()), randInt(0, boidGUI.getCanvasHeight()), randInt(0, Boid.MAX_SPEED), randInt(0, Boid.MAX_SPEED), boidGUI.getCanvasWidth(), boidGUI.getCanvasHeight());
		boidList.add(boid);
		Thread thread = new Thread(boid);
		thread.start();
	}
	
	public synchronized void removeBoidFromFlock(){
		if (boidList.size() > 0){
			int random = randInt(0, boidList.size() - 1);
			boidList.remove(random);
		}
	}
	
	public List<Boid> getBoidList() {
		return boidList;
	}
	
	public void setBoidList(List<Boid> boidList) {
		this.boidList = boidList;
	}
	
	public void stopRequest(){
		for (int i = 0; i < boidList.size(); i++){
			Boid.stopRequested = true;
		}
		
	}
	
	public void startRequest(){
		if (Boid.stopRequested == true){	
			for(Boid boid: boidList){
				Boid.stopRequested = false;
				Thread thread = new Thread(boid);
				thread.start();
			}
		}
	}
	
	public void drawAllBoids(Graphics g){
		for(int i = 0; i < boidList.size(); i++){
			boidList.get(i).drawShip(g);
		}
	}

	public int getNumberOfBoids(){
		int numberOfBoids = 0;
		for(int i = 0; i < boidList.size(); i++){
			numberOfBoids++;
		}
		return numberOfBoids;
	}

	public void destroyAllBoids(){
		boidList.clear();
	}

	public List<Boid> getNeighbours(Boid boidToTest){
		List<Boid> neighboursList = new TestDriver<Boid>();
		float x;
		float y;
		for(int i = 0; i < boidList.size(); i++){
			x = (boidToTest.getxPos()) - (boidList.get(i).getxPos());
			y = (boidToTest.getyPos()) - (boidList.get(i).getyPos());
			if(boidList.get(i) != boidToTest){
				if((x * x) + (y * y) < (DETECTRADIUS * DETECTRADIUS)){
					neighboursList.add(boidList.get(i));
				}
			}
		}
		return neighboursList;
	}
}

