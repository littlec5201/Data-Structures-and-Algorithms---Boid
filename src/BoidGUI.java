import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


/**
 * @author Callum
 *
 */
public class BoidGUI extends JPanel implements ActionListener, ChangeListener{
    Boid boid = new Boid();
	private DrawingCanvas canvas;
    private static BoidFlock boidFlock = new BoidFlock(100);
    private Timer timer, timerExternalForce;
    private JButton addBoidButton, removeBoidButton, startButton, stopButton, clearBoidsButton, quitButton;
    private JSlider radiusSlider, separationSlider, cohesionSlider, alignmentSlider, externalForceSlider;
    private JLabel radiusLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel separationLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel cohesionLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel alignmentLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel externalForceLabel = new JLabel("", SwingConstants.CENTER);
    private float radiusValue, separationValue, cohesionValue, alignmentValue, externalForceValue;
    private int radiusMultiplier = 10;
    private int separationDivider = 25;
    private int cohesionDivider = 500;
    private int alignmentDivider = 200;
    private int externalForceMultiplier = 5;
    private int canvasWidth = 950;
    private int canvasHeight = 700;

	public BoidGUI(){
		super();
		setLayout(new BorderLayout());
        try{  
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}
        canvas = new DrawingCanvas();
        timer = new Timer(10, this);
        timer.start();
        timerExternalForce = new Timer(1000, this);
        timerExternalForce.start();
        
        // Creates a JPanel in which the buttons, sliders and labels will be put in
        JPanel controlPanel = new JPanel();
        // Creates a JPanel in which the buttons will be put in. I have done this to allow better control
        // over the layout of the buttons. There are 6 buttons, so I have set 3 rows with 2 columns
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2));
        
        // JButton section
        // Sets the text to be displayed on the JButton, puts the JButon in the buttonPanel and adds an action listener
        addBoidButton = new JButton("Add Boid");
        addBoidButton.addActionListener(this);
        buttonPanel.add(addBoidButton);
        
        removeBoidButton = new JButton("Remove Boid");
        removeBoidButton.addActionListener(this);
        buttonPanel.add(removeBoidButton);
        
        startButton = new JButton("Start Boids");
        startButton.addActionListener(this);
        buttonPanel.add(startButton);
        
        stopButton = new JButton("Stop Boids");
        stopButton.addActionListener(this);
        buttonPanel.add(stopButton);
        
        clearBoidsButton = new JButton("Clear Boids");
        clearBoidsButton.addActionListener(this);
        buttonPanel.add(clearBoidsButton);
        
        quitButton = new JButton("Quit");
        quitButton.addActionListener(this);
        buttonPanel.add(quitButton);
        
        // Adds the buttonPanel to the controlPanel
        controlPanel.add(buttonPanel);
        
        // JSlider / JLabel section
        // Sets the minimum, maximum and the initial value displayed on the slider
        // The JSlider is then configured using the configureJSlider method, defined below
        radiusSlider = new JSlider(0, 100, 0);
        configureJSlider(radiusSlider);
        radiusSlider.addChangeListener(this);
        setRadiusValue((float)radiusSlider.getValue() * radiusMultiplier);
        JPanel radiusPanel = new JPanel();
        radiusPanel.add(radiusLabel);
        radiusPanel.add(radiusSlider);
        radiusPanel.setLayout(new GridLayout(2, 1));
        controlPanel.add(radiusPanel);
        
        separationSlider = new JSlider(0, 100, 10);
        configureJSlider(separationSlider);
        separationSlider.addChangeListener(this);
        setSeparationValue((float)separationSlider.getValue() / separationDivider);
        JPanel seperationPanel = new JPanel();
        seperationPanel.add(separationLabel);
        seperationPanel.add(separationSlider);
        seperationPanel.setLayout(new GridLayout(2, 1));
        controlPanel.add(seperationPanel);
        
        cohesionSlider = new JSlider(0, 100, 5);
        configureJSlider(cohesionSlider);
        cohesionSlider.addChangeListener(this);
        setCohesionValue((float)cohesionSlider.getValue() / cohesionDivider);
        JPanel cohesionPanel = new JPanel();
        cohesionPanel.add(cohesionLabel);
        cohesionPanel.add(cohesionSlider);
        cohesionPanel.setLayout(new GridLayout(2, 1));
        controlPanel.add(cohesionPanel);
        
        alignmentSlider = new JSlider(0, 100, 2);
        configureJSlider(alignmentSlider);
        alignmentSlider.addChangeListener(this);
        setAlignmentValue((float)alignmentSlider.getValue() / alignmentDivider);
        JPanel alignmentPanel = new JPanel();
        alignmentPanel.add(alignmentLabel);
        alignmentPanel.add(alignmentSlider);
        alignmentPanel.setLayout(new GridLayout(2, 1));
        controlPanel.add(alignmentPanel);   
        
        externalForceSlider = new JSlider(-50, 50, 0);
        configureJSlider(externalForceSlider);
        externalForceSlider.addChangeListener(this);
        setExternalForceValue((float)externalForceSlider.getValue() * externalForceMultiplier);
        JPanel externalForcePanel = new JPanel();
        externalForcePanel.add(externalForceLabel);
        externalForcePanel.add(externalForceSlider);
        externalForcePanel.setLayout(new GridLayout(2, 1));
        controlPanel.add(externalForcePanel); 
        
        // Listener for mouse clicks on the screen. The x and y coordinates are then passed into the
        // setExternalForceLocation method, which will either make the boids attracted to or repelled from the 
        // coordinate depending if the force is positive or negative 
        addMouseListener(new MouseAdapter() { 
        	public void mousePressed(MouseEvent e) { 
        		int x = e.getX();
        		int y = e.getY();
        		for (Boid boid : boidFlock.getBoidList()){
        			boid.setExternalForceLocation(x, y);
        		}
        		
        	} 
        }); 
        controlPanel.setLayout(new GridLayout(6, 1));
        controlPanel.setBorder(new EmptyBorder(10, 0, 0, 10));
        add(controlPanel,BorderLayout.EAST);
        add(canvas,BorderLayout.WEST);
	}
	
	public void configureJSlider(JSlider jSlider){
		jSlider.setMajorTickSpacing(10);
		jSlider.setPaintTicks(true);
		jSlider.setPaintLabels(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == timer){
			canvas.repaint();
		}
		else if(source == addBoidButton){
			boidFlock.addBoidToFlock();
		}
		else if(source == removeBoidButton){
			boidFlock.removeBoidFromFlock();
		}
		else if(source == startButton){
			boidFlock.startRequest();
		}
		else if(source == stopButton){
			boidFlock.stopRequest();
		}
		else if(source == clearBoidsButton){
			boidFlock.destroyAllBoids();
		}
		else if(source == quitButton){
			System.exit(0);
		}
		if (source == timer){
			radiusLabel.setText("Adjust Radius: " + getRadiusValue());
	    	separationLabel.setText("Adjust Separation: " + getSeparationValue());
			cohesionLabel.setText("Adjust Cohesion: " + getCohesionValue());
			alignmentLabel.setText("Adjust Alignment: " + getAlignmentValue());
			externalForceLabel.setText("Adjust External Force: " + getExternalForceValue());
		}
		if (source == timerExternalForce){
			boid.removeExternalForceLocation();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
		Object source = e.getSource();
		
		if (source == radiusSlider) {
			int value = radiusSlider.getValue();
		    if(radiusSlider.getValueIsAdjusting() == false) {
		    	boidFlock.DETECTRADIUS = value * radiusMultiplier;
		    }
		    setRadiusValue(value * radiusMultiplier);
		}
		
		else if(source == separationSlider){
			float value = separationSlider.getValue();
			if(separationSlider.getValueIsAdjusting() == false){
				for (int i = 0; i < boidFlock.getBoidList().size(); i++){
					boidFlock.getBoidList().get(i).SEPARATION_WEIGHT = value / separationDivider;
				}
			}
			setSeparationValue(value / separationDivider);
		}
		
		else if(source == cohesionSlider){
			float value = cohesionSlider.getValue();
			if(cohesionSlider.getValueIsAdjusting() == false){
				for (int i = 0; i < boidFlock.getBoidList().size(); i++){
					boidFlock.getBoidList().get(i).COHESION_WEIGHT = value / cohesionDivider;
				}
			}
			setCohesionValue(value / cohesionDivider);
		}
		
		else if(source == alignmentSlider){
			float value = alignmentSlider.getValue();
			if(alignmentSlider.getValueIsAdjusting() == false){
				for (int i = 0; i < boidFlock.getBoidList().size(); i++){
					boidFlock.getBoidList().get(i).ALIGNMENT_WEIGHT = value / alignmentDivider;
				}
			}
			setAlignmentValue(value / alignmentDivider);
		}
		
		else if(source == externalForceSlider){
			float value = externalForceSlider.getValue();
			if(externalForceSlider.getValueIsAdjusting() == false){
				for (int i = 0; i < boidFlock.getBoidList().size(); i++){
					boidFlock.getBoidList().get(i).EXTERNAL_WEIGHT = value * externalForceMultiplier;
				}
			}
			setExternalForceValue(value * externalForceMultiplier);
		}
	}

	private class DrawingCanvas extends JPanel{
        public DrawingCanvas(){
            setPreferredSize(new Dimension(getCanvasWidth(), getCanvasHeight()));
            setBackground(Color.BLACK);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            boidFlock.drawAllBoids(g);
        }
    }

    public int getCanvasWidth(){
    	return this.canvasWidth;
    }

    public int getCanvasHeight(){
    	return this.canvasHeight;
    }

	public float getRadiusValue() {
		return radiusValue;
	}

	public void setRadiusValue(float radiusValue) {
		this.radiusValue = radiusValue;
	}

	public float getSeparationValue() {
		return separationValue;
	}

	public void setSeparationValue(float separationValue) {
		this.separationValue = separationValue;
	}

	public float getCohesionValue() {
		return cohesionValue;
	}

	public void setCohesionValue(float cohesionValue) {
		this.cohesionValue = cohesionValue;
	}

	public float getAlignmentValue() {
		return alignmentValue;
	}

	public void setAlignmentValue(float alignmentValue) {
		this.alignmentValue = alignmentValue;
	}

	public int getExternalForceMultiplier() {
		return externalForceMultiplier;
	}

	public void setExternalForceMultiplier(int externalForceMultiplier) {
		this.externalForceMultiplier = externalForceMultiplier;
	}

	public float getExternalForceValue() {
		return externalForceValue;
	}

	public void setExternalForceValue(float externalForceValue) {
		this.externalForceValue = externalForceValue;
	}

	public static void main(String args[]){
		JFrame frame = new JFrame("Boid GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new BoidGUI());
        frame.pack();  
        frame.setResizable(false);
        frame.setVisible(true);
	}
}
