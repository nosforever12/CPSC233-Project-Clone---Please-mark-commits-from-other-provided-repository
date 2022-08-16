package application;

import java.text.DecimalFormat;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class containing functions and associated helper functions that are called in 
 * response to user actions on the GUI.
 */
public class GUIController {
	//stored reference to stage of program (set in Main)
	public Stage applicationStage;
	
	//stored reference to the main scene when swapping to other scenes (set in Main)
	public Scene mainScene;
	
	/**Index of the currently highlighted canvas panel in the list of triangles*/
	private int highlightedPanelIndex = 0;
	
    /**Reference to a Triangle object that the controller is currently working with*/
	private Triangle triangle;
    
    /**Instance of TriangleCatalog object which contains a list of triangles and specialized 
   	methods to validate, manage, and perform operations on that list.*/
	private TriangleCatalog triangleCatalog = new TriangleCatalog();
	
	//fxml file nodes
    @FXML private TextField hypotenuseTextField, adjacentTextField, oppositeTextField, angleThetaTextField;
    @FXML private ToggleButton degreesToggleButton, radiansToggleButton, valueToggleButton, formulaToggleButton;
	@FXML private Button informationButton, nextButton, previousButton;
    @FXML private Canvas mainCanvas = new Canvas();
    @FXML private Text infoAreaText;
    @FXML private Label errorLabel, instructionLabel; 
	@FXML private VBox guiCatalogVBox;
	
	/**
	 * Setting the colors of the canvas, emptying the inputs for the input text fields, 
	 * and putting the instruction text in the text area underneath the canvas.
	 * @implNote This function is called automatically after the root element has been completely processed, 
	 * as part of javafx's control object implementation.
	 */
	@FXML
	void initialize() {
    	//creating graphics object attached to the main canvas
		GraphicsContext graphics = mainCanvas.getGraphicsContext2D();
		
		//A black outline indicating the edges of the canvas is achieved by filling the
		//entire canvas with black, then a very slightly smaller area in white.
		graphics.setFill(Color.BLACK);
		graphics.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
		graphics.setFill(Color.WHITE);
		graphics.fillRect(1, 1, mainCanvas.getWidth()-2, mainCanvas.getHeight()-2);
		
		//clearing the input text fields. This is already implemented in a modular function, 
		//so it is used instead of manually setting each text field here.
		refreshTextFields();
		
		
		//setting the text area underneath the canvas to basic instructions about using the program.
		graphics.setFill(Color.BLACK);
		infoAreaText.setText("Pick two components of a right triangle"
						+ "\n(side lengths, angle) and enter values "
						+ "\nfor them to see detailed information"
						+ "\nabout the triangle here. "
						+ "\n\nClick the Information button at the top "
						+ "\nright of the window to see all the"
						+ "\navailable features of this program.");
	}
	
	
	/**
     * Creates the correct type of triangle (Formula-based or numeric solutions) based on
     * the settings chosen by the user in the GUI, and displays the triangle to the user.
     * Includes calls to validation functions related to the use of the GUI. (not creating
     * the same triangle twice, entering the correct number of inputs, etc.)
     * @implNote triggered by the "Calculate" button
     */
    @FXML
    void calculate() {
    	//emptying the error label as errors related to previously created triangles have no effect
    	//on a new triangle being created.
    	errorLabel.setText("");
    	
    	//checking that the user did enter exactly two values as instructed by the GUI.
    	if(checkTwoTotalInputs()) {
    		//storing the previous triangle (if applicable) so that it is possible to 
    		//check later if the new triangle being created has the exact same measurements
    	    Triangle oldTriangle;
    		if(triangle != null) {
    			oldTriangle = new Triangle(triangle);
    		} else {
    			oldTriangle = triangle;
    		}
    		
        	//creating a new Triangle or FormulaTriangle object (child of Triangle), giving it the contents of the GUI.
        	//Triangle calculates it's values for numeric solutions, whereas FormulaTriangle (child) overrides certain 
    		//methods in order to handle string entries to calculate for an algebraic formula incorporating the 
    		//user's inputs instead.
        	if(formulaToggleButton.isSelected()) {
        		triangle = new FormulaTriangle(hypotenuseTextField.getText(),oppositeTextField.getText(),
        										adjacentTextField.getText(),angleThetaTextField.getText(), 
        										degreesToggleButton.isSelected(), mainCanvas);
        	} else {
        		triangle = new Triangle(hypotenuseTextField.getText(),oppositeTextField.getText(),
        								adjacentTextField.getText(),angleThetaTextField.getText(), 
        								degreesToggleButton.isSelected(), mainCanvas);
        	}
        
        	//checking that the newly created triangle is different than the previous one (multiple triangles with the 
        	//exact same measurements serves no purpose to the user and only bloats the visible list of created triangles)
        	//as well as not having any errors. 
        	if((oldTriangle == null || oldTriangle.isDifferent(triangle)) && triangle.getErrorDescription().equals("")) {
    			//calling the wrapper method which draws the triangle, it's labels, and the information associated 
    			//with the triangle on the desired canvas.
    			drawAllTriangleComponents(mainCanvas);
    			
    			//adding the newly created triangle to the list of triangles.
    			triangleCatalog.addTriangle(triangle);
    			
    			//creating new components on the GUI for a visual representation of the newly created triangle
    			//in the area showing the current list of triangles that have been created.
    			addCanvasPanel(triangle);
    		} else {
            	//setting the errorLabel to notify user of any potential errors regarding inputs not falling within
            	//acceptable values as validated by the triangle object (validation is different based on the type
            	//of triangle being created) No error needs to be displayed if the triangle is unchanged from the
    			//previously created one, as the triangle the user intends to create would be already shown on screen.
            	errorLabel.setText(triangle.getErrorDescription());
    		}
    	}
    }
    
    
    /**
     * Checks if exactly two values were input by the user within the text fields.
     * @return true if exactly two values were entered, otherwise false
     */
    boolean checkTwoTotalInputs() {
    	int totalInputs = 0;
    	
    	//checks each text field and increments the counter for each text field that isn't empty
    	if (!hypotenuseTextField.getText().isEmpty()) totalInputs++;
    	if (!oppositeTextField.getText().isEmpty()) totalInputs++;
    	if (!adjacentTextField.getText().isEmpty()) totalInputs++;
    	if (!angleThetaTextField.getText().isEmpty()) totalInputs++;
    	
    	//checks the counter to determine whether or not exactly two values were input. Set the
    	//error label to prompt the user to enter two if not.
    	if (totalInputs != 2) {
    		errorLabel.setText("Enter values for two components.");
    		return false;
    	}
    	return true;
    }
   
    
    /**
     * Wrapper method which calls the all drawing, labeling, and text setting functions that are necessary when a new
     * triangle needs to be displayed on the main canvas.
     * @param canvasToDrawOn - The canvas which the triangle should be resized to fit, drawn, and labeled on.
     */
    public void drawAllTriangleComponents(Canvas canvasToDrawOn) {
		//drawing the outline of the triangle
    	drawTriangle(canvasToDrawOn);
    	
    	//moving and setting labels according to the values of the triangle
    	setTriangleLabels(canvasToDrawOn);
    	
    	//putting the solve method in the text area of the GUI.
    	setInfoText();
    }
    
    
	/**
	 * Requests the triangle object to resize itself to the target canvas,
	 * then draws the triangle by stroking between the triangle's corner points.
	 * @param canvasToDrawOn - the target canvas which the triangle should be drawn on.
	 */
	public void drawTriangle(Canvas canvasToDrawOn) {
		//resizing the triangle to fit on the canvas
    	triangle.prepareForCanvas(canvasToDrawOn);
    	
    	//creating graphics object attached to the target canvas
		GraphicsContext graphics = canvasToDrawOn.getGraphicsContext2D();
		
		//clears the canvas so that any previously drawn triangle is no longer visible.
		//A black outline indicating the edges of the canvas is achieved by filling the
		//entire canvas with black, then a very slightly smaller area in white.
		graphics.setFill(Color.BLACK);
		graphics.fillRect(0, 0, canvasToDrawOn.getWidth(), canvasToDrawOn.getHeight());
		graphics.setFill(Color.WHITE);
		graphics.fillRect(1, 1, canvasToDrawOn.getWidth()-2, canvasToDrawOn.getHeight()-2);
		
		//setting the triangle's point coordinates to individual variables to make
		//the following code more readable.
		double haX = triangle.getHypAdj().getX();
		double haY = triangle.getHypAdj().getY();
		double hoX = triangle.getHypOpp().getX();
		double hoY = triangle.getHypOpp().getY();
		double oaX = triangle.getOppAdj().getX();
		double oaY = triangle.getOppAdj().getY();
		
		//strokes between each point of the triangle to create an outline of the triangle.
		graphics.setFill(Color.BLACK);
		graphics.strokeLine(haX, haY, hoX, hoY);
		graphics.strokeLine(hoX, hoY, oaX, oaY);
		graphics.strokeLine(oaX, oaY, haX, haY);
	}
	
	
	/**
	 * Writes text on the target canvas acting as labels for each of the triangle's side lengths/angle. If the target canvas
	 * is the main canvas, these labels will be located near the value on the triangle that they are representing.
	 * @param canvasToDrawOn - the target canvas which the labels should be drawn on.
	 */
	public void setTriangleLabels(Canvas canvasToDrawOn) {
		//creating graphics object attached to the target canvas.
		GraphicsContext graphics = canvasToDrawOn.getGraphicsContext2D();
		
		if(canvasToDrawOn == mainCanvas) {
			//setting minimum bounds for labels (so they are always fully visible)
			int xBound = (int) (mainCanvas.getWidth()-35);
			int yBound = 10;

			
			//setting values for overlap detection between labels (so they don't overlap causing readability issues)
			int overlapMinX = 100;
			int overlapMinY = 15;
			
			//creating a new point for each label to be drawn at, using the triangle's corners
			//to determine the center point of the sidelength/angle that is being represented.
			Point h = calculateMidpoint(triangle.getHypAdj(), triangle.getHypOpp(), xBound, yBound);
			Point o = calculateMidpoint(triangle.getHypOpp(), triangle.getOppAdj(), xBound, yBound);
			Point a = calculateMidpoint(triangle.getHypAdj(), triangle.getOppAdj(), xBound, yBound);
			Point t = calculateMidpoint(triangle.getHypAdj(), triangle.getHypAdj(), xBound, yBound);
			
			//move the newly created points such that they do not overlap with each other to keep labels readable
			moveOverlappingPoints(h, o, a, t, overlapMinX, overlapMinY);
			
			//writes the label information at their previously calculated locations
			graphics.setFill(Color.RED);
			graphics.fillText("H: " + triangle.getInfo("hyp"), h.getX(), h.getY());
			graphics.fillText("O: " + triangle.getInfo("opp"), o.getX(), o.getY());
			graphics.fillText("A: " + triangle.getInfo("adj"), a.getX(), a.getY());
			graphics.fillText("θ: " + triangle.getInfo("ang"), t.getX(), t.getY());
		} else {
			//write the labels on the top right of the canvas, with an increasing Y coordinate value to prevent overlap.
			graphics.setFill(Color.RED);
			graphics.fillText("H: " + reduceLengthTo5Char(triangle.getInfo("hyp")), 0, 10);
			graphics.fillText("O: " + reduceLengthTo5Char(triangle.getInfo("opp")), 0, 20);
			graphics.fillText("A: " + reduceLengthTo5Char(triangle.getInfo("adj")), 0, 30);
			graphics.fillText("θ: " + reduceLengthTo5Char(triangle.getInfo("ang")), 0, 40);
		}
	}
	
	
	/**
	 * Uses the averaged x and y coordinate from the two input points to create a new point between them.
	 * Sets coordinates to input bounds if they do not fall within those values (causing them to not appear in the canvas)
	 * @param p1 - first point object to take coordinates from
	 * @param p2 - second point object to take coordinates from
	 * @param xBound - maximum x coordinate of new point
	 * @param yBound - minimum y coordinate of new point
	 * @return new point object with coordinates averaged from the two points
	 */
	Point calculateMidpoint(Point p1, Point p2, int xBound, int yBound) {
		//get the average X and Y coordinates between the two points
		double x = (p1.getX()+p2.getX())/2;
		double y = (p1.getY()+p2.getY())/2;
		
		//set coordinates to max/min bound if outside of bound
		//a maximum Y or minimum X is not needed, because the triangle is always centered on the canvas;
		//any changes to the label points which would cause it to fall outside these bounds are only
		//in the -Y and +X direction.
		x = (x > xBound) ? xBound : x;
		y = (y < yBound) ? yBound : y;
		
		Point point = new Point(x,y);
		return point;
	}
	
	
	/**
	 * Checks for points that are too close to each other and moves them to prevent overlapping text. 
	 * @param h - hypotenuse label point object
	 * @param o - opposite label point object
	 * @param a - adjacent label point object
	 * @param t - theta - angle label point object
	 * @param minX - required minimum distance between point objects horizontally, 
	 * if they are also within minY distance of each other
	 * @param minY - required minimum distance between point objects vertically, 
	 * if they are also within minX distance of each other
	 */
	void moveOverlappingPoints(Point h, Point o, Point a, Point t, int minX, int minY) {
		//creation of point object array to loop through
		Point[] p = {h,o,a,t};
		
		//2d for loop such that all points are compared to each other at least once
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				//skipping cases where the same point is being compared to itself
				if(j==i) continue;
				
				//retrieving and storing the x and y coordinates of each point in individual variables 
				//to increase readability in the next action as it is quite complicated
				double p1X = p[i].getX();
				double p1Y = p[i].getY();
				double p2X = p[j].getX();
				double p2Y = p[j].getY();
				
				//if the two points are within both the X and Y minimum distances (they are too close)
				if(Math.abs(p1Y - p2Y) < minY && Math.abs(p1X - p2X) < minX) {
					//move the point with the smaller Y value to the Y minimum distance from the other point 
					if(p1Y <= p2Y) {
						p[j].setY(p2Y + minY);
					} else {
						p[i].setY(p1Y + minY);
					}
					//they are moved in the vertical dimension because while text may be very long (wide), 
					//it is always the same height (barring font size changes and wrapping, which are not present
					//in the main canvas)
				}
			}
		}
	}
	
	/**
	 * Customized helper method which returns the input string as a string of maximum 5 
	 * characters, plus ellipsis if the input string had it's length reduced.
	 * @param stringToReduce - the string that needs to be reduced to 5 characters
	 * @return the input string unchanged if it had length equal or below 5 characters,
	 * otherwise reduced to 5 characters with ellipsis added.
	 */
	String reduceLengthTo5Char(String stringToReduce) {
		String reducedString = "";
		//only reconstruct the string if it is longer than 5 characters
		if(stringToReduce.length() > 5) {
			//set the new string to the first 5 characters of the input string
			for(int i = 0; i < 5; i++) {
				reducedString += stringToReduce.charAt(i);
			}
			//add on ellipsis signifying that the input string was cut short
			reducedString += "...";
		} else {
			reducedString = stringToReduce;
		}
		return reducedString;
	}
	
	
	/**
	 * Sets the text area underneath the canvas to show the values of each of the
	 * triangle's sidelengths/angle, as well as the solve method information stored 
	 * in the triangle's hashmap containing any trigonometric formulas that were used
	 * to calculate for any of the missing values.
	 */
	public void setInfoText() {
    	infoAreaText.setText("Hypotenuse: " + triangle.getInfo("hyp") + 
    			"\nOpposite: " + triangle.getInfo("opp") + 
    			"\nAdjacent: " + triangle.getInfo("adj") + 
    			"\nAngle θ: " + triangle.getInfo("ang")  + 
    			"\n\nTrig. Formula Used: " + triangle.getInfo("solveMethod"));
	}
    
    
	/**
	 * Creates the necessary GUI nodes for an individual triangle's panel in the catalog. These nodes
	 * are linked to functions which may need to reference the triangle that this panel is being created
	 * (for example, an x button to delete both the panel and the triangle it is representing in the list)
	 * @param triangleToAddToCanvasPanel - the triangle object that is represented by this panel in the GUI
	 */
	void addCanvasPanel(Triangle triangleToAddToCanvasPanel) {
		//creating a new canvas to draw a small version of the triangle on
		Canvas canvas = new Canvas(80,80);
		
		//creating a button to act as a delete button for the panel and triangle
		Button removeButton = new Button("x");
		removeButton.setTranslateX(28);
		removeButton.setTranslateY(-27);
		removeButton.setOpacity(0.8);
		
		//creating a StackPane panel and adding the canvas and the button to it
		StackPane panel = new StackPane();
		panel.getChildren().addAll(canvas, removeButton);
		
		//setting the functions associated with clicking the delete button or the panel.
		removeButton.setOnAction(touchEvent -> removeTriangle(triangleToAddToCanvasPanel, panel));
		panel.setOnMousePressed(touchEvent -> selectAndDrawPanel(panel));
		
		//adding the panel to the VBox in the fxml intended to hold many of these panels.
		guiCatalogVBox.getChildren().add(panel);
		
		//moving the delete button to the front and positioning it in the top right
		removeButton.toFront();
		removeButton.setLayoutX(10);
		
		//resizing the triangle to fit on this smaller (compared to the main) canvas
		triangleToAddToCanvasPanel.prepareForCanvas(canvas);
		
		//drawing the triangle on the newly created canvas
		drawTriangle(canvas);
		
		//setting the labels for the triangle on the newly created canvas
		setTriangleLabels(canvas);
		
		//updating the currently selected panel in the catalog to this newly created panel.
		updateHighlightedPanel(panel);
	}
	
	
	/**
	 * Updates the catalog highlight, sets the triangle reference to the triangle 
	 * represented by the panel clicked, and draws the triangle on the canvas.
	 * This function is called by a setOnMousePress action that is given to every
	 * panel created for the catalog.
	 * @param panelClicked - the panel in the GUI triangle catalog that was clicked
	 */
	void selectAndDrawPanel(StackPane panelClicked) {
		//updating catalog highlight
		updateHighlightedPanel(panelClicked);
		//setting triangle reference to the triangle represented by the newly highlighted panel
		triangle = triangleCatalog.getTriangle(highlightedPanelIndex);
		//redrawing the triangle now that the triangle reference has changed
		drawAllTriangleComponents(mainCanvas);
	}
    
	
	/**
	 * sets the current triangle variable's reference to the previous or next (depending on button clicked) 
	 * triangle in the list of triangles in the catalog, then draws the triangle on the canvas. Also changes
	 * the highlighted (currently selected) triangle on the catalog to the newly selected one.
	 ** @param trigger - the "Next" or "Previous" togglebutton on the GUI that is clicked by the user
	 */
	@FXML
	void drawAdjacentTriangle(ActionEvent trigger) {
		//checking that the list contains triangles before proceeding
		if(triangleCatalog.getListSize() != 0) {
			//determining whether to retrieve the previous or next triangle in the list based on
			//which button was pressed to trigger this function
			if(trigger.getSource() == nextButton) {
				//checking if there is a triangle ahead of the currently selected one 
				if(highlightedPanelIndex < (guiCatalogVBox.getChildren().size()-1)) {
					//setting the triangle reference to the next triangle in the list
					triangle = triangleCatalog.getNextTriangle(triangle);
					//updating the highlight in the catalog to reflect changes that are about
					//to be made to the displayed triangle
					updateHighlightedPanel(highlightedPanelIndex+1);
				}	
			} else {
				if(highlightedPanelIndex > 0) {
					//setting the triangle reference to the previous triangle in the list
					triangle = triangleCatalog.getPreviousTriangle(triangle);
					//updating the highlight in the catalog to reflect changes that are about
					//to be made to the displayed triangle
					updateHighlightedPanel(highlightedPanelIndex-1);	
				}
			}
		}
		//redrawing the triangle now that the triangle reference has changed
		drawAllTriangleComponents(mainCanvas);
	}
	
	
	/**
	 * deletes a triangle from the list of triangles in the catalog, 
	 * as well as the GUI panel representing it from the GUI catalog.
	 * @param triangleToRemove - reference to the triangle object that needs to be deleted
	 * @param panelToRemove - reference to the stackpane panel node that needs to be deleted
	 */
	void removeTriangle(Triangle triangleToRemove, StackPane panelToRemove) {
		//if the currently selected triangle (not the triangle to be deleted) is below the triangle to remove,
		//and the currently selected triangle is not the first triangle in the list, the highlight should move up
		//one panel to remain on the same triangle after removing the triangle to be deleted from the list.
		if(highlightedPanelIndex >= triangleCatalog.getIndexInList(triangleToRemove) && highlightedPanelIndex > 0) {
				highlightedPanelIndex--;
		}
		
		//removing the triangle to be deleted from the list
		triangleCatalog.removeTriangle(triangleToRemove);
		//removing the panel representing the triangle to be deleted from the GUI catalog
		guiCatalogVBox.getChildren().remove(panelToRemove);
		
		//updating the highlight on the GUI catalog to reflect any potential changes, for example
		//if the currently selected triangle is deleted, the previous triangle would be selected
		updateHighlightedPanel(highlightedPanelIndex);
		
		if(triangleCatalog.getListSize() == 0) {
			//returning the program to it's initial states (clean canvas, instructions on the text area)
			//if there are no more triangles
			initialize();
			triangle = null;
		} else {
			//drawing the newly selected triangle on the main canvas otherwise
			triangle = triangleCatalog.getTriangle(highlightedPanelIndex);
			drawAllTriangleComponents(mainCanvas);
		}
	}
	
	/**
	 * Deletes all triangles and their representing panels in the catalog.
	 * @implNote triggered by the "Delete All" button in the GUI triangle catalog
	 */
	@FXML
	void clearTriangleList() {
		//removing triangles until there are none left
		while(triangleCatalog.getListSize() > 0) {
			removeTriangle(triangleCatalog.getTriangle(0), (StackPane) guiCatalogVBox.getChildren().get(0));
		}
	}
	
	
	/**
	 * Sets random valid inputs within the GUI, and creates a new triangle with
	 * those inputs through the use of the calculate() function.
	 * @implNote triggered by the "Add Random Triangle" button in the GUI triangle catalog
	 */
	@FXML
	void addRandomTriangle() {
		//clearing the textfields to prepare for new inputs
		hypotenuseTextField.setText("");
		oppositeTextField.setText("");
		adjacentTextField.setText("");
		angleThetaTextField.setText("");
		
		//creating new textfield objects which will reference
		//two of the currently four existing textfields on the GUI
		TextField pickedTextField = randomTextField();
		TextField pickedTextField2 = pickedTextField;

		//repeatedly selecting a random text field until the second text field
		//is different from the first
		while(pickedTextField2 == pickedTextField) {
			pickedTextField2 = randomTextField();
		}
		
		//calling custom helper method to insert valid inputs with different
		//ranges and requirements depending on the text field that was picked
		randomizeTextField(pickedTextField);
		randomizeTextField(pickedTextField2);
		
		//creating the triangle and additional necessary functions associated with doing so (adding to catalog, etc.)
		calculate();
	}
	
	
	/**
	 * Sets the text within the textField passed to this function with a randomized
	 * value within the entire range of the specific textField's valid inputs.
	 * @param textFieldToSet - textField that the random value should be generated for
	 */
	void randomizeTextField(TextField textFieldToSet){
		//creating formatting object for later use in reducing the precision of the generated double
		DecimalFormat dec2 = new DecimalFormat("#0.00");
		//creating random object to generate values
		Random rand = new Random();
		
		//applying different multipliers to the value returned by the random object
		//to achieve the desired range of values based on the textfield intended to be set.
		if(textFieldToSet == oppositeTextField || textFieldToSet == adjacentTextField) {
			//nextDouble() returns a value (0 <= n < 1), so subtracting 0.5 allows for negative numbers to appear half the time.
			//the intended range of inputs for these fields are (-10 <≈ n <≈ 10), so multiplying the previous result 
			//(-0.5 <= n < 0.5) by 20 will achieve this.
			textFieldToSet.setText("" + dec2.format((rand.nextDouble()-0.5)*20));
		} else {
			if(degreesToggleButton.isSelected()) {
				//the intended range of inputs for the only other available field (angle) in degrees mode is (0 < n < 90)
				//so multiplying the initial return (0 =< n < 1) by 90 will achieve (0 <= n < 90)
				//Use a while loop to randomize the number again while (n*90 <= 0.01) as that will result in a 0.00 being set.
				String angleToSet = dec2.format(rand.nextDouble()*90);
				while(angleToSet.equals("0.00")) {
					angleToSet = dec2.format(rand.nextDouble()*90);
				}
				textFieldToSet.setText(angleToSet);
			} else {
				//in radians mode, the range of intended range of inputs for the angle field is (0 < n < π/2)
				//Same method as above to remove cases where a value of (n*Math.PI/2 <= 0.01) is given by nextDouble().
				String angleToSet = dec2.format((rand.nextDouble()*Math.PI)/2);
				while(angleToSet.equals("0.00")) {
					angleToSet = dec2.format((rand.nextDouble()*Math.PI)/2);
				}
				textFieldToSet.setText(angleToSet);
			}
		}
	}
	
	/**
	 * randomly chooses one of the following text fields in the GUI to return: opposite, adjacent, angle.
	 * The hypotenuse text field is not given as an option, because it can cause conflicts with attempting
	 * to create triangles with a larger side length than hypotenuse. It is also unnecessary, because any 
	 * manner of right triangle can be created by using just one side length and any other component.
	 * @return references to either the opposite, adjacent, or angle text field in the GUI.
	 */
	TextField randomTextField() {
		//creating random object to generate values
		Random rand = new Random();
		//a range of 3 allows for 3 results to choose from
		int intPicked = rand.nextInt(3);
		
		//checking the generated value and returning one of the three text fields based on the result
		if (intPicked == 0) {
			return oppositeTextField;
		} else if (intPicked == 1) {
			return adjacentTextField;
		}
		return angleThetaTextField;
	}
	
	
	/**
	 * Sets the opacity of all panels in the GUI catalog to either 0.5 or 1 depending on 
	 * whether or not they are to be highlighted (highlighted = opaque)
	 * @param panelToHighlight - the StackPane node representing that should be opaque
	 * @implNote Overloaded method - see updateHighlightedPanel(int canvasIndexToHighlight) 
	 */
	void updateHighlightedPanel(StackPane panelToHighlight) {
		//looping through every node in the GUI catalog
		for(int i = 0; i < guiCatalogVBox.getChildren().size(); i++) {
			if(guiCatalogVBox.getChildren().get(i) != panelToHighlight) {
				//set opacity of the node in the VBox to 0.5 if they are not the panel to be highlighted
				guiCatalogVBox.getChildren().get(i).setOpacity(0.5);
			} else {
				//set opacity of the node in the VBox to 1 if they are the panel to be highlighted
				guiCatalogVBox.getChildren().get(i).setOpacity(1);
				//updating the variable tracking the index of the currently highlighted triangle
				highlightedPanelIndex = i;
			}
		}
		//updating the input text fields to the inputs used to create the currently selected triangle
		refreshTextFields();
	}
	
	
	/**
	 * Sets the opacity of all panels in the GUI catalog to either 0.5 or 1 depending on 
	 * whether or not they are to be highlighted (highlighted = opaque)
	 * @param panelIndexToHighlight - integer representing the index of the panel in the list to be highlighted
	 * @implNote Overloaded method - see updateHighlightedPanel(StackPane panelToHighlight)
	 */
	void updateHighlightedPanel(int panelIndexToHighlight) {
		//looping through every node in the GUI catalog
		for(int i = 0; i < guiCatalogVBox.getChildren().size(); i++) {
			if(guiCatalogVBox.getChildren().get(i) != guiCatalogVBox.getChildren().get(panelIndexToHighlight)) {
				//set opacity of the node in the VBox to 0.5 if it is not at the index specified through the function arguments
				guiCatalogVBox.getChildren().get(i).setOpacity(0.5);
			} else {
				//set opacity of the node in the VBox to 1 if it is at the index specified through the function arguments
				guiCatalogVBox.getChildren().get(i).setOpacity(1);
				//updating the variable tracking the index of the currently highlighted triangle
				highlightedPanelIndex = i;
			}
		}
		//updating the input text fields to the inputs used to create the currently selected triangle
		refreshTextFields();
	}
	
	
	/**
	 * sets the GUI text field inputs to the ones used to create the currently selected triangle. 
	 * In the case that there is no selected triangle, empties all the text fields.
	 */
	void refreshTextFields() {
		//checking that there are triangles in catalog before proceeding
		if(triangleCatalog.getListSize() > 0) {
			//creating variables to store the currently selected triangle's creation inputs to increase readability (it's too long)
			String hypotenuseInput = triangleCatalog.getTriangle(highlightedPanelIndex).getInfo("HypotenuseInput");
			String oppositeInput = triangleCatalog.getTriangle(highlightedPanelIndex).getInfo("OppositeInput");
			String adjacentInput = triangleCatalog.getTriangle(highlightedPanelIndex).getInfo("AdjacentInput");
			String angleThetaInput = triangleCatalog.getTriangle(highlightedPanelIndex).getInfo("Angle θInput");

			//setting the GUI text field to the associated creation input value stored in the triangle if there is 
			//one, otherwise setting it to an empty string.
			if(hypotenuseInput != null) hypotenuseTextField.setText(hypotenuseInput);
			else hypotenuseTextField.setText("");
			if(oppositeInput != null) oppositeTextField.setText(oppositeInput);
			else oppositeTextField.setText("");
			if(adjacentInput != null) adjacentTextField.setText(adjacentInput);
			else adjacentTextField.setText("");
			if(angleThetaInput != null) angleThetaTextField.setText(angleThetaInput);
			else angleThetaTextField.setText("");
			
		} else {
			//setting all the text field's inputs to an empty string if there are no triangles in the catalog.
			hypotenuseTextField.setText("");
			oppositeTextField.setText("");
			adjacentTextField.setText("");
			angleThetaTextField.setText("");
		}
	}
	
	
	/**
	 * Creates a scene containing general information about the program and switches to it.
	 * @implNote triggered by the "Information" button
	 */
	@FXML
	void switchToAboutScene(){
		//Creating a button that triggers a method which switches the scene back to the main scene
		Button backButton = new Button("Back to Calculator");
		backButton.setOnAction(doneEvent -> applicationStage.setScene(mainScene));
		
		//creating a button that triggers a method which switches the scene to the validation information scene
		Button validationInfoButton = new Button("Validation Info.");
		validationInfoButton.setOnAction(doneEvent -> switchToValidationScene());
		
		//creating a VBox and adding the above two buttons to it
		VBox buttonBox = new VBox();
		buttonBox.setAlignment(Pos.TOP_RIGHT);
		buttonBox.getChildren().addAll(validationInfoButton, backButton);
		HBox.setMargin(buttonBox, new Insets(10,10,10,10));
		
		//creating a label with large font size to act as a header
		Label headerLabel = new Label("About");
		headerLabel.setFont(new Font("Verdana", 30));
		
		//creating a label with the bulk of the information intended to be conveyed on this scene
		Label infoLabel = new Label("This program creates a right triangle based off two entered values "
				+ "(sidelengths or angle) and outputs the values of all sidelengths and the inner angle. "
				+ "\n\nDegree / Radians: \nChanges whether the value entered, as well as the final value "
				+ "is read and shown as Degrees or Radians. \n\nValue: \nSolves for numeric values. Negative "
				+ "values may be entered for the opposite and adjacent side lengths. "
				+ "\n\nFormula: \nSolves for transformative algebraic formulas. Useful for figuring out what "
				+ "formulas to use in your programming projects. For example, entering distance(a,b) and "
				+ "a.getX()-b.getX() for the hypotenuse and adjacent respectively to find an algebraic formula "
				+ "for getting the angle between the two objects. \n\nA catalog of triangles you create will "
				+ "be available for you to easily switch between multiple right triangles. You may navigate "
				+ "between the created triangles using the Next and Previous buttons, as well as by "
				+ "clicking on the panels in the catalog. Triangles can also be deleted by clicking the X in "
				+ "the top right of the triangle panel in the catalog. \n\nAdd Random Triangle: \nCreates a "
				+ "triangle using randomize values.\n\nDelete All: Clears the catalog of all triangles.");
		infoLabel.setWrapText(true);
		infoLabel.setFont(new Font(13));
		
		//creating a VBox and adding the above two labels to it
		VBox labelBox = new VBox();
		labelBox.setMaxWidth(365);
		labelBox.getChildren().addAll(headerLabel, infoLabel);
		HBox.setMargin(labelBox, new Insets(10,10,10,10));
		
		//creating a HBox and adding the two VBoxes to it
		HBox mainBox = new HBox();
		mainBox.getChildren().addAll(labelBox,buttonBox);
		
		//adding the HBox to a new scene with the height and width of the main scene
		applicationStage.setScene(new Scene(mainBox, mainScene.getWidth(), mainScene.getHeight()));
		
		//making one of the buttons the same width as the other for a cleaner display
		validationInfoButton.setPrefWidth(backButton.getBoundsInParent().getWidth());
	}
	
	
	/**
	 * Creates a scene containing information about the specifics of the program's validation of user inputs and switches to it.
	 * @implNote triggered by the "Validation Info." button in the Information/About scene
	 */
	@FXML
	void switchToValidationScene() {
		//Creating a button that triggers a method which switches the scene back to the about scene
		Button infoButton = new Button("Back to Info.");
		infoButton.setOnAction(doneEvent -> switchToAboutScene());
		
		//creating a button that triggers a method which switches the scene back to the main scene
		Button backButton = new Button("Back to Calculator");
		backButton.setOnAction(doneEvent -> applicationStage.setScene(mainScene));
		
		//creating a VBox and adding the above two buttons to it
		VBox buttonBox = new VBox();
		buttonBox.setAlignment(Pos.TOP_RIGHT);
		buttonBox.getChildren().addAll(infoButton, backButton);
		HBox.setMargin(buttonBox, new Insets(10,10,10,10));
		
		//creating a label with large font size to act as a header
		Label headerLabel = new Label("Validation");
		headerLabel.setFont(new Font("Verdana", 30));
		
		//creating a label with the bulk of the information intended to be conveyed on this scene
		Label infoLabel = new Label("Entered values which do not meet these requirements will be "
				+ "accompanied with an error message. Click Back to Info. to see more about Value mode and "
				+ "Formula mode. \n\nValue Mode:\nAll values may include a decimal "
				+ "for increased precision. \n\nHypotenuse: Positive values only. \n\nOpposite: Positive or "
				+ "negative values. \n\nAdjacent: Positive or negative values. \n\nAngle θ: Positive values "
				+ "only. Must be under 90° in Degrees mode, or π/2 in Radians mode.\n\n\n\nFormula Mode: "
				+ "\nAll entries are valid." );
		infoLabel.setWrapText(true);
		infoLabel.setFont(new Font(17));
		
		//creating a VBox and adding the above two labels to it
		VBox labelBox = new VBox();
		labelBox.setMaxWidth(365);
		labelBox.getChildren().addAll(headerLabel, infoLabel);
		HBox.setMargin(labelBox, new Insets(10,10,10,10));
		
		//creating a HBox and adding the two VBoxes to it
		HBox mainBox = new HBox();
		mainBox.getChildren().addAll(labelBox,buttonBox);
		
		//adding the HBox to a new scene with the height and width of the main scene
		applicationStage.setScene(new Scene(mainBox, mainScene.getWidth(), mainScene.getHeight()));
		
		//making one of the buttons the same width as the other for a cleaner display
		infoButton.setPrefWidth(backButton.getBoundsInParent().getWidth());
	}
	
	
    /**
     * Toggles the degrees and radians button in the GUI such that one and only one is always active.
     * @param trigger - "Radians" or "Degrees" togglebutton that is clicked by the user
     */
    @FXML
    void toggleAngleMode(ActionEvent trigger) {
    	//determines which button was clicked, and sets it to true, while also setting the other button false.
    	if(trigger.getSource().equals(degreesToggleButton)) {
    		radiansToggleButton.setSelected(false);
    		degreesToggleButton.setSelected(true);
    	} else {
    		degreesToggleButton.setSelected(false);
    		radiansToggleButton.setSelected(true);
    	}
    }
    
    
    /**
     * Toggles the value and formula button in the GUI such that one and only one is always active.
     * Sets the label at the top of GUI to prompt the user to input the correct value type when
     * entering the sidelengths/angle as determined by the calculation mode represented by the state
     * of these buttons.
     * @param trigger - "Value" or "Formula" togglebutton that is clicked by the user
     */
    @FXML
    void toggleValueMode(ActionEvent trigger) {
    	if(trigger.getSource().equals(valueToggleButton)) {
    		//set formula button to inactive and value button to active
    		formulaToggleButton.setSelected(false);
    		valueToggleButton.setSelected(true);
    		//set appropriate main label text for currently selected mode
    		instructionLabel.setText("Enter Two Values for a Right Triangle (numeric): ");
    	} else {
    		//set value button to inactive and formula button to active
    		valueToggleButton.setSelected(false);
    		formulaToggleButton.setSelected(true);
    		//set appropriate main label text for currently selected mode
    		instructionLabel.setText("Enter Two Variable Names (see Information): ");
    	}
    }
}


