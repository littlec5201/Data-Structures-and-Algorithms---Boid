/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
/**
 *
 * @author sehall
 */
public class Boid implements Runnable{
   private float xPos,yPos;     //x and y position of boid
   private int boundX,boundY;   //boundaries of panel
   private float velX, velY;    //velocity of boid
   public static int BOID_LENGTH = 15; // pixel length for drawing each boid
   public static boolean stopRequested;
   public static float SEPARATION_WEIGHT = 1.5f; // separation steering force weighting 1.5
   public static float ALIGNMENT_WEIGHT = 0.05f; // alignment steering force weighting 0.05
   public static float COHESION_WEIGHT = 0.01f;  // cohesion steering force weighting ( 0.01)
   public static float EXTERNAL_WEIGHT = 5.0f;   // external steering force weighting ( 5.0)
   public static int MAX_SPEED = 10;              // upper bound for speed of one boid
   private BoidFlock flock;                      //reference back to the flock
   public static boolean enableExtForce;
   private float xExt,yExt;
   
   public Boid(){
	   
   }

   //constructor takes reference back to the boid flock. and needs the starting
   //postion and velocity of the boid as well as the boundaries for the panel  
   public Boid(BoidFlock flock, float xPos, float yPos, float velX, float velY, int boundX, int boundY){
      this.boundX = boundX - BOID_LENGTH - 1;
      this.boundY = boundY - BOID_LENGTH - 1;
      this.xPos = xPos;
      this.yPos = yPos;
      this.velX = velX;
      this.velY = velY;
      this.flock = flock;
   }
   
   //getters and setters
   public float getVelX(){    
	   return velX;
   }
   
   public float getVelY(){    
	   return velY;
   }
   
   public float getxPos(){    
	   return xPos;
   }
   
   public float getyPos(){
	   return yPos;
   }
   
   public void setVelX(float velX){
	   this.velX = velX;
   }
   
   public void setVelY(float velY){
	  this.velY = velY;
   }
   
   public void setxPos(float xPos){
	  this.xPos = xPos;
   }
   
   public void setyPos(float yPos){
	  this.yPos = yPos;
   }
   
   //draws a single boid.
   public void drawShip(Graphics g){
	  double speed = Math.sqrt((velX*velX)+(velY*velY));
      if (speed < 0.01f){
         speed = 0.01f;
      }
      // calculate direction of boid normalised to length BOID_LENGTH/2
      double vx_dir = BOID_LENGTH * velX / (2.0f * speed);
      double vy_dir = BOID_LENGTH * velY / (2.0f * speed);
      g.setColor(new Color(245,61,223));
      g.drawLine((int)(xPos - 2.0f * vx_dir), (int)(yPos - 2.0f * vy_dir), (int)xPos, (int)yPos);
      g.setColor(Color.CYAN);
      g.drawLine((int)xPos, (int)yPos, (int)(xPos - vx_dir+vy_dir), (int)(yPos - vx_dir - vy_dir));
      g.setColor(new Color(255,135,0));
      g.drawLine((int)xPos, (int)yPos, (int)(xPos - vx_dir - vy_dir), (int)(yPos + vx_dir - vy_dir));
   }
   
   //called so if an external force is applied, then we set the force up
   //at the parameter co-ordinates.
   public synchronized void setExternalForceLocation(float xExt, float yExt){
      enableExtForce = true;
      this.xExt = xExt;
      this.yExt = yExt;
   }
   
   //called to turn off the external force
   public void removeExternalForceLocation(){
      enableExtForce = false;
   }
   
   //update the position of the boid based on its velocity, should be
   //avoid shared used of position and velocity variables
   private synchronized void moveBoid(){
	  xPos += velX;
      yPos += velY;
      // check whether boid off bounds of screen and if so bounce back
      if (xPos < 0){
    	 xPos = 2 * BOID_LENGTH - xPos;
         velX = -velX;
      }
      else if (xPos >= boundX){ 
    	 xPos = 2 * boundX - xPos;
         velX = -velX;
      }
      if (yPos < BOID_LENGTH){ 
    	 yPos = 2 * BOID_LENGTH - yPos;
         velY = -velY;
      }
      else if (yPos >= boundY){
    	 yPos = 2 * boundY - yPos;
         velY = -velY;
      }
   }
   
   //run method for single boid to run this thread concurrently
   public void run(){
      stopRequested = false;
      List<Boid> neighbours = null;
      //create all posible forces
      Force steering_force = new Force();
      Force separation_force = new Force();
      Force alignment_force = new Force();
      Force cohesion_force = new Force();
      Force external_force = new Force();
      //loop until requested to stop.
      while(stopRequested == false){
    	 //neighbouring boids around this boids position.
         neighbours = flock.getNeighbours(this);
         //option to add another cohesive external force
         if(enableExtForce){
            external_Force_behaviour(external_force);
            external_force.x *= EXTERNAL_WEIGHT;
            external_force.y *= EXTERNAL_WEIGHT;
            add_available_force(steering_force, external_force);
         }     
         // find the separation force on boid and apply the weight factor
         separation_behaviour(neighbours,separation_force);
         separation_force.x *= SEPARATION_WEIGHT;
         separation_force.y *= SEPARATION_WEIGHT;
         add_available_force(steering_force,separation_force);
           // find the alignment force and apply the weight factor
         alignment_behaviour(neighbours,alignment_force);
         alignment_force.x *= ALIGNMENT_WEIGHT;
         alignment_force.y *= ALIGNMENT_WEIGHT;
         add_available_force(steering_force,alignment_force);
         // find the cohesion force and apply the weight factor
         cohesion_behaviour(neighbours,cohesion_force);
         cohesion_force.x *= COHESION_WEIGHT;
         cohesion_force.y *= COHESION_WEIGHT;
         add_available_force(steering_force,cohesion_force);
         // use the cumulative steering force to modify velocity of boid
         float new_vx = getVelX() + steering_force.x;
         float new_vy = getVelY() + steering_force.y;
         if (new_vx*new_vx+new_vy*new_vy <= MAX_SPEED*MAX_SPEED){ 
        	setVelX(new_vx);
            setVelY(new_vy);
         }
         //update the position of the boid based on its velocity, should be
         //synchronized to avoid shared used of position and velocity variables
         moveBoid();
         //clear the force objects
         steering_force.clear();
         separation_force.clear();
         cohesion_force.clear();
         alignment_force.clear();
         external_force.clear();
         //put this thread to sleep for 50mS
         try{
        	 Thread.sleep(50);
         } catch(InterruptedException e){}
      }
   }
   
   //requests this thread to stop running
   public void requestStop(){ 
	   stopRequested = true;
   }
   
   // adds the new_force to the steering force
    private void add_available_force(Force force, Force new_force){
       force.x += new_force.x;
       force.y += new_force.y;
    }

//method that is called to either repel or attract all boids to a force
   private void external_Force_behaviour(Force external_force){
      float x_separation = getxPos() - xExt;
      float y_separation = getyPos() - yExt;
      float separation_squared = (x_separation * x_separation) + (y_separation * y_separation);
      if (separation_squared < 1){
         separation_squared = 1.0f;
      }
      external_force.x += x_separation / separation_squared;
      external_force.y += y_separation / separation_squared;

   }
   
   // calculates the separation component of steering force on a boid
   // based on sum of forces each given by 1/separation of boids
   private synchronized void separation_behaviour(List<Boid> neighbours,Force steering_force){
	  // determine which boids are within range
      //Force steering_force = new Force();
      for (Boid ship:neighbours){
         float x_separation = getxPos() - ship.getxPos();
         float y_separation = getyPos() - ship.getyPos();
         float separation_squared = x_separation * x_separation + y_separation * y_separation;
         if (separation_squared < 1)
            separation_squared = 1.0f;
         // scale steering force so linearly decreases with separation
         steering_force.x += x_separation / separation_squared;
         steering_force.y += y_separation / separation_squared;
      }
      //return steering_force;
   }
   
    //aligns the boids with each of its neighbours by the alignment weight
    private synchronized void alignment_behaviour(List<Boid> neighbours,Force steering_force){
        // determine which boids are within range
       float total_vx = 0.0f;
       float total_vy = 0.0f;
       int counter = 0;
       for (Boid ship:neighbours){
          total_vx += ship.getVelX();
          total_vy += ship.getVelY();
          counter++;
       }
       if (counter > 0){
    	  steering_force.x = total_vx / counter - getVelX();
          steering_force.y = total_vy / counter - getVelY();
       }
    }
    
    //calculates the cohesion component of steering force on a boid
    //based on the average position of tagged (nearby) boids
    private synchronized void cohesion_behaviour(List<Boid> neighbours, Force steering_force){
       // determine which boids are within range
       float total_x = 0.0f;
       float total_y = 0.0f;
       int counter = 0;
       for (Boid ship:neighbours){
    	   total_x += ship.getxPos();
    	   total_y += ship.getyPos();
    	   counter++;
       }
       if (counter > 0){
    	  steering_force.x = total_x / counter - getxPos();
          steering_force.y = total_y / counter - getyPos();
       }
    }
    
   //Object that represents an applied force on a single boid
   private class Force{
	  public float x, y;

      public Force(){
    	 this(0.0f, 0.0f);
      }
      public Force(float x,float y){
    	 this.x = x;
         this.y = y;
      }
      public void clear(){
         x = 0.0f; 
         y = 0.0f;
      }
   }
}
