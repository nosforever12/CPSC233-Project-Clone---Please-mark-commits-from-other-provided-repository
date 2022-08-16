package application;

import java.text.DecimalFormat;
import java.util.HashMap;

import javafx.scene.canvas.Canvas;

/**
 * Object class containing all the necessary variables for a triangle. These include doubles representing
 * the sidelengths, angle, point objects representing the coordinates of the corners of the triangles,
 * and also storage of the various user inputs and settings associated with the creation of the triangle.
 */
public class Triangle {
	private double hyp, opp, adj, ang;
	private boolean angleModeDegrees;
	private String errorDescription = "";
	private Point pointHypOpp, pointHypAdj, pointOppAdj;
	private HashMap<String, String> info = new HashMap<String, String>();
	
	/**Triangle Constructor. Runs the necessary methods to prepare the triangle and
	 * associated values for display on the GUI. This includes calculating the 
	 * missing sidelengths/angle, scaling and moving it to fit on the canvas, 
	 * and creating points that represent the corners of the triangle.
	 * @param inputHyp - value entered for hypotenuse side length
	 * @param inputOpp - value entered for opposite side length
	 * @param inputAdj - value entered for adjacent side length
	 * @param inputAng - value entered for angle theta
	 * @param angleModeDegrees - degree mode - true for degrees, false for radians
	 */
	Triangle(String inputHyp, String inputOpp, String inputAdj, String inputAng, boolean angleModeDegrees, Canvas canvasToDrawOn){
		//Validating the various user inputs before starting calculations
		//to prevent potential wrongtype or math errors later on
    	double validatedH = validateInput("Hypotenuse", inputHyp, angleModeDegrees);
    	double validatedO = validateInput("Opposite", inputOpp, angleModeDegrees);
    	double validatedA = validateInput("Adjacent", inputAdj, angleModeDegrees);
    	double validatedT = validateInput("Angle θ", inputAng, angleModeDegrees);
    	
		//computing for the missing values of the triangle using the validated user inputs
		calculateMissingValues(validatedH,validatedO,validatedA,validatedT, angleModeDegrees); 
		
		//checking if a valid triangle was calculated, setting an error description if not
		isValid();
		
		//setting the current triangle sidelength/angle information to string values in the
		//triangle before they are manipulated by further methods for display purposes
		//This method is overridden in the FormulaTriangle class in order to store 
		//algebraic formulas instead of calculated values for the sidelengths/angles.
		storeInfoInHashMap();
		
		//resizing and moving the triangle to fit and make efficient use of the canvas size
		prepareForCanvas(canvasToDrawOn);
	}
	
	/**
	 * Triangle Copy Constructor. Creates a new triangle with the same values as
	 * the triangle to copy from.
	 * @param triangleToCopy - Triangle object to copy values
	 */
	Triangle(Triangle triangleToCopy){
		//setting the various values associated with a triangle object with
		//the values from the triangle to copy.
		hyp = triangleToCopy.getHyp();
		opp = triangleToCopy.getOpp();
		adj = triangleToCopy.getAdj();
		ang = triangleToCopy.getAng();
		angleModeDegrees = triangleToCopy.getDegreeMode();
		errorDescription = triangleToCopy.getErrorDescription();
		pointHypOpp = triangleToCopy.getHypOpp();
		pointHypAdj = triangleToCopy.getHypAdj();
		pointOppAdj = triangleToCopy.getOppAdj();
		info = new HashMap<String, String>(triangleToCopy.getInfo());
	}
	
	
	
    /**
     * Validates
     * @param textField - The label of the textField that is currently being validated
     * @param text - the text within the textField that is currently being validated
     * @param degrees - whether or not the the degreesToggleButton is currently selected
     * @return double value suitable for the triangle object; text parsed as a double or 
     * 1 (for triangles of FormulaTriangle class) if the text was valid, otherwise 0.
     */
    private double validateInput(String textField, String text, boolean degrees) {
    	//storing the user input for future use
		info.put(textField+"Input", text);
		
		//setting the value to 0 is my default for a value that must be calculated for,
		//this does not cause any issues since a valid triangle will never have 0 as a 
		//value for any of it's side lengths or angles.
		if(text.isEmpty()) return 0;
		
		//setting the error description of the triangle to the return value of the error checking function.
		//checkError is overridden in the child class of Triangle since the criteria for
		//valid inputs change based on whether it is solving using values or formulas names.
    	errorDescription = checkError(textField, text, degrees);
    	
    	//if there is no error message, a non-0 value may be returned.
    	if(errorDescription.equals("")) {
    		//depending on the type of triangle being created (formula or numeric based),
    		//non-double values may be returned by the checkError method. In this case, 
    		//a 1 will be returned to signify that the user entered a valid input.
    		try {
    			return Double.parseDouble(text);
    		} catch(NumberFormatException e) {
    			return 1;
    		}
    	}
    	
    	//If the program has reached this point, the input is not valid, so the
    	//default value of 0 will be returned.
    	return 0.0;
    }	
    
    
	/**
	 * Checks if the text field contains a valid side length value. 
	 * Valid side lengths only contain digits, as well as up to one decimal and/or negative sign.
	 * 0 is not a valid side length.
	 * Valid angles are (0 < n < 90) in degrees mode, or (0 < n < π/2) in radians mode.
	 * @param textField - the label of the input text field being checked
	 * @param text - String object to validate
	 * @param degreesMode - whether the program is currently calculating in degrees or radians
	 * @return String value describing the error causing the input to be invalid (empty string if no error) 
	 * @implNote This method is overridden by the child class FormulaTriangle.
	 */
    protected String checkError(String textField, String text, boolean degreesMode) {
    	//initializing variables that keep track of un-allowed or limited-quantity-allowed characters
    	int dotCount = 0;
    	int dashCount = 0;
    	int otherCount = 0;
    	
    	//loops through the string to tally up previously initialized counters.
    	for(int i = 0; i < text.length(); i++) {
    		if(!Character.isDigit(text.charAt(i))){
        		if(text.charAt(i) == '.') dotCount++;
        		else if(text.charAt(i) == '-' && i == 0) dashCount++;
        		else otherCount++;
    		}
    	}
    	
    	//large amount of conditions that cover the many ways which a user may input an invalid value.
    	if(dotCount > 1 || dashCount > 1 || otherCount >= 1 
    			|| (textField == "Angle θ" && !degreesMode && (Double.parseDouble(text) >= Math.PI/2 || Double.parseDouble(text) < 0))
    			|| (textField == "Angle θ" && degreesMode && (Double.parseDouble(text) >= 90 || Double.parseDouble(text) < 0)) 
    			|| (textField == "Hypotenuse" && Double.parseDouble(text) < 0) 
    			|| Double.parseDouble(text) == 0) {
    		
    		//checks which above condition was violated, and sets the errorLabel message to an appropriate message.
    		if (textField == "Angle θ") errorDescription = " must be less than 90° or π/2 (~1.57)";
    		else if(dotCount > 1) errorDescription = " can only contain one decimal point.";
    		else if(dashCount > 1) errorDescription = " can only contain one negative sign.";
    		else if(otherCount >= 1) errorDescription = " can only contain digits, decimals or neg. signs.";
    		else if(textField == "Hypotenuse" && Double.parseDouble(text) < 0) errorDescription = " can not be less than 0.";
    		else if(Double.parseDouble(text) == 0) errorDescription = " can not be equal to 0.";
    		errorDescription = textField + errorDescription;
    	}
    	
    	return errorDescription;
    }

	
	/**
	 * Solves for the missing values of the triangle using trigonometry. The solved 
	 * values are assigned to the triangle object's instance variables, and the 
	 * method/formula used is also put into the triangle's hashmap under the key "solveMethod"
	 * Unknown parameters (entered as 0) will be solved for.
	 * @param hyp - hypotenuse side length of the triangle
	 * @param opp - opposite side length of the triangle
	 * @param adj - adjacent side length of the triangle
	 * @param ang - theta - angle between hypotenuse and adjacent side lengths
	 * @param angleModeDegrees - degree mode (degrees if true, radians if false) of value entered for theta
	 * @implNote This method is overridden by the child class FormulaTriangle.
	 */
	protected void calculateMissingValues(double hyp, double opp, double adj, double ang, boolean angleModeDegrees) {
		String solveMethod = "";
		
		//determining whether it is necessary to change the angle given by the user to radians
		//since the Math library trigonometric functions only works in radians
		if(angleModeDegrees) ang = Math.toRadians(ang);
		
		//if side length/angle values aren't equal to 0, they have a value given by the user,
		//and therefore should be used to calculate the values of the rest of the triangle.
		//The below code contains all the possible combinations of the two text fields that
		//the user may have chosen to enter values for.
		if(hyp!=0 && opp!=0) {
			ang = Math.asin(Math.abs(opp)/Math.abs(hyp));
			solveMethod += "θ = aSin(o/h) \nRearranged from: sinθ = o/h";
			adj = Math.sqrt(hyp*hyp - opp*opp);
		} else if(hyp!=0 && adj!=0) {
			ang = Math.acos(Math.abs(adj)/Math.abs(hyp));
			solveMethod += "θ = aCos(a/h) \nRearranged from: cosθ = a/h";
			opp = Math.sqrt(hyp*hyp - adj*adj);
		} else if(hyp!=0 && ang!=0) {
			opp = hyp*Math.sin(ang);
			adj = hyp*Math.cos(ang);
			solveMethod += "o = h*sin(θ) \nRearranged from: sinθ = o/h \n\nTrig. Formula Used: a = h*cos(θ) \nRearranged from: cosθ = a/h";
		} else if(opp!=0 && adj!=0) {
			ang = Math.atan(Math.abs(opp)/Math.abs(adj));
			solveMethod += "θ = aTan(o/a) \nRearranged from: tanθ = o/a";
			hyp = Math.sqrt(adj*adj + opp*opp);
		} else if(opp!=0 && ang!=0) {
			adj = opp/Math.tan(ang);
			solveMethod += "a = o/tan(θ) \nRearranged from: tanθ = o/a";
			hyp = Math.sqrt(adj*adj + opp*opp);
		} else if(adj!=0 && ang!=0) {
			opp = adj*Math.tan(ang);
			solveMethod += "o = a*tan(θ) \nRearranged from: tanθ = o/a";
			hyp = Math.sqrt(adj*adj + opp*opp);
		}
		
		//converting angle (back) into degrees if the user chose to calculate in degrees
		if(angleModeDegrees) ang = Math.toDegrees(ang);
		
		//assigning the newly calculated values to the triangle's instance variables
		this.hyp = hyp;
		this.opp = opp;
		this.adj = adj;
		this.ang = ang;
		this.angleModeDegrees = angleModeDegrees;
		
		//the solve method may be stored now since it does not
		//need additional formatting before being displayed to the user
		info.put("solveMethod", solveMethod);
	}
	
	
    /**
     * Checks if the created triangle has valid side lengths and angle. 0 is not a valid side length/angle. 
     * A NaN may also be calculated for the hypotenuse in cases where the user inputs can not create a valid
     * triangle, and similarly marks the triangle as invalid. Having non-empty error description also voids validity.
     * Sets the error description of the triangle to an appropriate message if found.
     * @return true if the triangle has valid side lengths/angle and no errors, otherwise false
     */
    private boolean isValid() {
    	//triangle.getH() will return NaN if it is unable to be calculated due to entering 
    	//impossible values for triangle side lengths (ie. opposite larger than hypotenuse)
    	if (Double.isNaN(hyp) || Double.isNaN(opp) || Double.isNaN(adj) 
    			|| Double.isNaN(ang) || hyp == 0 || opp == 0 || adj == 0) {
    		if(errorDescription.equals("")) {
        		//if the triangle has 0 for h/o/a values, no errorlabel message change is needed as the
    			//validateInput() method would already have set an errorlabel message, and should not be 
    			//overwritten. Therefore there is only an errorlabel message for the isNaN case of validateTriangle().
    			errorDescription = "Opp. and Adj. can't be larger than or equal to Hyp.";
    		}
    		return false;
    	}
    	return true;
    }
    
    
	/**
	 * Takes the triangle's current sidelength and angle values, formats them 
	 * into strings with 2 decimal places, and stores them in the triangle's information hashmap.
	 * These are the final values that will be displayed to the user.
	 * @implNote This method is overridden by the child class FormulaTriangle.
	 */
	protected void storeInfoInHashMap(){
		//creation of component to format doubles into 2 decimal places
		DecimalFormat dec2 = new DecimalFormat("#0.00");
		
		//converting the boolean degree mode used to calculate 
		//the triangle into text that makes sense to the user
		String angleMode = angleModeDegrees ? "°" : "rad";
		
		//storing formatted information into the hashmap of the triangle object
		info.put("hyp", dec2.format(hyp));
		info.put("opp", dec2.format(opp));
		info.put("adj", dec2.format(adj));
		info.put("ang", dec2.format(ang)+angleMode);
	}
	
    
    /**
     * Wrapper method which calls all the functions related to the operations taken to
     * adjust triangle values such that it will be displayed properly on the canvas.
     * This includes scaling size, translation to the visible quadrant, 
     * and centering the triangle on the canvas.
     * @param canvasToDrawOn
     */
    public void prepareForCanvas(Canvas canvasToDrawOn) {
		//scaling the triangle to a suitable size for the canvas
		scaleSize(canvasToDrawOn);
		
		//calculating the coordinates of each of the (scaled) triangle's three corners.
		calculatePointCoordinates();
		
		//moving each of the triangle's points to let us display triangles with points of 
		//negative coordinates on the main quadrant of the canvas (the canvas only shows +,+)
		movePointsToPositiveQuadrant();
		
		//moving the triangle's points so that they are (collectively) centered on the canvas
		centerPointsOnCanvas(canvasToDrawOn);
    }
    
    
	/**
	 * Scales up/down the size (distance between corner points) of the triangle such that it's 
	 * largest length (relative to canvas edge length) uses the entire canvas (maxW, maxH)
	 * @param canvasToDrawOn - target canvas that triangle needs to fit on
	 */
	private void scaleSize(Canvas canvasToDrawOn){
		//maximum size that triangle should take up in x and y dimensions
		double maxW = canvasToDrawOn.getWidth()*0.8;
		double maxH = canvasToDrawOn.getHeight()*0.8;
		double scale = 1;
		
		//scale triangle size to use the entire canvas without distorting x:y ratio
		//by picking the most appropriate scale to use between the x and y dimension
		//by determining the side length with length closest to it's respective window side length
		if(Math.abs(opp)/maxH > Math.abs(adj)/maxW) scale = maxH / Math.abs(opp);
		else scale = maxW / Math.abs(adj);
		opp *= scale;
		adj *= scale;
		
		//recalculating the hypotenuse length since the opposite/adjacent lengths have changed.
		hyp = Math.sqrt(opp*opp + adj*adj);
	}
	
	
	/**
	 * Uses the triangle's current adjacent and opposite side length values determine 
	 * the coordinates and create point objects for each of the triangle's three corners.
	 */
	private void calculatePointCoordinates() {
		/* opposite value for the hypotenuse is flipped to counter-act the java
		 * canvas' orientation of heading top to bottom as the y value increases. The
		 * values are divided by two since otherwise, the points would have two times
		 * the opposite/adjacent sidelength distances away from each other due to
		 * having a negative and positive value difference between them. */		
		pointHypOpp = new Point(adj/2,-opp/2);
		pointHypAdj = new Point(-adj/2,opp/2);
		pointOppAdj = new Point(adj/2,opp/2);
	}
	
	
	/**
	 *Identifies points that have negative x or y coordinates and calls the translate function
	 *with those point's coordinates such that all points will be on the (+,+) plane on the 
	 *canvas without distorting or reflecting the triangle over any axis.
	 */
	private void movePointsToPositiveQuadrant() {
		//translating triangle across x/y axis as needed to keep all 
		//triangle points on main quadrant (positive, positive)
		if(pointHypOpp.getX() < 0) translatePoints(Math.abs(pointHypOpp.getX()), 0);
		if(pointHypOpp.getY() < 0) translatePoints(0, Math.abs(pointHypOpp.getY()));
		if(pointHypAdj.getX() < 0) translatePoints(Math.abs(pointHypAdj.getX()), 0);
		if(pointHypAdj.getY() < 0) translatePoints(0, Math.abs(pointHypAdj.getY()));
		if(pointOppAdj.getX() < 0) translatePoints(Math.abs(pointOppAdj.getX()), 0);
		if(pointOppAdj.getY() < 0) translatePoints(0, Math.abs(pointOppAdj.getY()));
	}
	
	
	/**
	 * Moves all points of the triangle right by x, down by y.
	 * @param x - value of horizontal direction to move all points by
	 * @param y - value of vertical direction to move all points by
	 */
	private void translatePoints(double x, double y){
		//all points must be moved so that the triangle is never distorted 
		//from it's original opposite:adjacent ratio.
		pointHypOpp.setX(pointHypOpp.getX()+x);
		pointHypOpp.setY(pointHypOpp.getY()+y);
		pointHypAdj.setX(pointHypAdj.getX()+x);
		pointHypAdj.setY(pointHypAdj.getY()+y);
		pointOppAdj.setX(pointOppAdj.getX()+x);
		pointOppAdj.setY(pointOppAdj.getY()+y);
	}
	
	
	/**
	 * Centers the points on the canvas such that distances between the points are unchanged,
	 * but the collective location of the points are centered on the canvas.
	 * @param canvasToDrawOn - target canvas that the triangle points need to be centered on
	 */
	private void centerPointsOnCanvas(Canvas canvasToDrawOn) {
		Point largestX = null;
		Point largestY = null;
		
		//creation of array containing each triangle corner point to loop through
		Point[] trianglePoints = {pointHypOpp, pointHypAdj, pointOppAdj};
		
		//making the previously created variables reference the point that has the largest
		//x or y coordinate
		for(Point point : trianglePoints) {
			if(largestX == null || point.getX() > largestX.getX()) largestX = point;
			if(largestY == null || point.getY() > largestY.getY()) largestY = point;
		}
		
		//translating all points right by the difference between the rightmost point to the rightmost
		//edge of the canvas, down by the difference between the lowest (visually) point to the bottom
		//edge of the canvas. This centers the points, because the points are created to initially be
		//aligned with the top left corner of the canvas.
		translatePoints((canvasToDrawOn.getWidth()-largestX.getX()) / 2, 
				(canvasToDrawOn.getHeight()-largestY.getY()) / 2);
	}

	
    /**
     * Compares the display values of the triangle to the display values of the triangle being compared to.
     * @param toCompare - the triangle object to compare to
     * @return true if any values are different, otherwise false.
     */
    public boolean isDifferent(Triangle toCompare) {
    	if(getInfo("hyp").equals(toCompare.getInfo("hyp"))
    		&& getInfo("opp").equals(toCompare.getInfo("opp"))
			&& getInfo("adj").equals(toCompare.getInfo("adj"))
			&& getInfo("ang").equals(toCompare.getInfo("ang")))
    		return false;
    	return true;
    }

    
	
	/**
	 * Getter method for Hyp (hypotenuse) side length of triangle
	 * @return hypotenuse side length value of triangle object
	 */
	public double getHyp() {
		return hyp;
	}

	/**
	 * Setter method for Hyp (hypotenuse) side length of triangle
	 * @param value of hypotenuse side length to set to triangle object
	 */
	public void setHyp(double hypToSet) {
		this.hyp = hypToSet;
	}

	/**
	 * Getter method for Opp (opposite) side length of triangle
	 * @return opposite side length of triangle object
	 */
	public double getOpp() {
		return opp;
	}

	/**
	 * Setter method for Opp (opposite) side length of triangle
	 * @param opposite side length value of triangle object
	 */
	public void setOpp(double oppToSet) {
		this.opp = oppToSet;
	}

	/**
	 * Getter method for Adj (adjacent) side length of triangle
	 * @return adjacent side length of triangle object
	 */
	public double getAdj() {
		return adj;
	}

	/**
	 * Setter method for Adj (adjacent) side length of triangle
	 * @param adjacent side length value of triangle object
	 */
	public void setAdj(double adjToSet) {
		this.adj = adjToSet;
	}
	
	/**
	 * Setter method for Ang (angle) value of triangle
	 * @return angle value of triangle object
	 */
	public double getAng() {
		return ang;
	}
    
	/**
	 * Getter method for the point object representing the the triangle object's 
	 * corner between the opposite and hypotenuse side lengths.
	 * @return new instance of point object with values equal to the aforementioned object
	 */
	public Point getHypOpp() {
		Point newHypOpp = new Point(pointHypOpp.getX(), pointHypOpp.getY());
		return newHypOpp;
	}
	
	/**
	 * Getter method for the point object representing the the triangle object's 
	 * corner between the adjacent and hypotenuse side lengths.
	 * @return new instance of point object with values equal to the aforementioned object
	 */
	public Point getHypAdj() {
		Point newHypAdj = new Point(pointHypAdj.getX(), pointHypAdj.getY());
		return newHypAdj;
	}
	
	/**
	 * Getter method for the point object representing the the triangle object's 
	 * corner between the opposite and adjacent side lengths.
	 * @return new instance of point object with values equal to the aforementioned object
	 */
	public Point getOppAdj() {
		Point newOppAdj = new Point(pointOppAdj.getX(), pointOppAdj.getY());
		return newOppAdj;
	}
		
	/**
	 * Getter method for angleDegreeMode - whether the triangle was calculated in degrees mode.
	 * @return true if the triangle was calculated in degrees, otherwise false.
	 */
	public boolean getDegreeMode() {
		return angleModeDegrees;
	}
	
	/**
	 * Getter method for the errorDescription of the triangle, assigned to the 
	 * object while validating the user's inputs for it's sidelength or angle values.
	 * @return String value of the error description (empty if no errors occurred)
	 */
	public String getErrorDescription() {
		String newStr = new String(errorDescription);
		return newStr;
	}
	
	/**
	 * Getter method for the info hashmap of the triangle.
	 * @param keyToGet - key in the hashmap to retrieve the value of.
	 * @return value of the string variable stored at key location in hashmap
	 * @implNote This method is overloaded - see getInfo()
	 */
	public String getInfo(String keyToGet) {
		String newStr = new String(info.get(keyToGet));
		return newStr;
	}
	
	/**
	 * Getter method for the info hashmap of the triangle
	 * @return new instance of the HashMap containing the triangle's string information
	 * @implNote This method is overloaded - see getInfo(String key)
	 */
	public HashMap<String,String> getInfo() {
		HashMap<String, String> newInfo = new HashMap<String, String>(info);
		return newInfo;
	}
	
	
	/**
	 * Setter method for the info hashmap of the triangle
	 * @param keyToSet - key in the hashmap to set the value of.
	 * @param valueToSetAtKey - value to set the key in the hashmap to.
	 */
	public void setInfo(String keyToSet, String valueToSetAtKey) {
		HashMap<String, String> newInfo = new HashMap<String, String>(info);
		newInfo.put(keyToSet, valueToSetAtKey);
		info = newInfo;
	}
}