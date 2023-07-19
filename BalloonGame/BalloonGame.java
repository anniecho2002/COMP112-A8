/* Code for COMP-102-112 - 2021T1, Assignment 8
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;

/** Program for a simple game in which the player has to blow up balloons
 *   on the screen.
 *  The game starts with a collection of randomly placed small balloons
 *    (coloured circles) on the graphics pane.
 *  The player then clicks on balloons to blow them up by a small amount
 *   (randomly increases the radius between 4 and 10 pixels).
 *  If an expanded balloon touches another balloon, then they both "burst" and go grey.
 *  The goal is to get the largest score. The score is the total of the
 *   sizes (areas) of all the active balloons, minus the total size of all
 *   the burst balloons.
 *  At each step, the current score is recalculated and displayed,
 *   along with the highest score that the player has achieved so far.
 *  At any time, the player may choose to stop and "lock in" their score.
 *
 *  The BalloonGame class has a field containing an Arraylist of Balloon objects
 *   to represent the current set of Balloons on the screen.
 *  It has a field to hold the highest score.
 *
 *  The New Game button should start a new game.
 *  The Lock Score button should finish the current game, updating the highest score
 *
 *  Clicking (ie, releasing) the mouse on the graphics pane is the main "action"
 *  of the game. The action should do the following
 *    Find out if the mouse was clicked on top of any balloon.
 *    If so,
 *      Make the balloon a bit larger and redraw it.
 *      Check whether the balloon is touching any other balloon.
 *      If so
 *         burst the two balloons (which will make them go grey)
 *         redraw the burst Balloons
 *      Recalculate and redisplay the score
 *   If all the balloons are gone, the game is over.
 *    
 *   To start a game, the program should
 *       Clear the graphics pane
 *       Initialise the score information
 *       Make a new list of Balloons at random positions
 *       Print a message 
 *
 *   If the game is over, the program should
 *      Update the highest score if the current score is better,
 *      Print a message reporting the scores,
 *     
 *   There are lots of ways of designing the program. It is not a good idea
 *   to try to put everything into one big method.
 *        
 *  Note that the Balloon class is written for you. Make sure that you know
 *   all its methods - no marks for redoing code that is given to you.
 *    
 */
public class BalloonGame {
    private static final int MAX_BALLOONS = 20;
    private ArrayList <Balloon> balloons = new ArrayList<Balloon>(); // The list of balloons
    private double highscore = 0;
    private double numBalloon = 20;
    private double score = 0;
    private double activeSize, burstSize;

    public void setupGUI(){
        UI.setWindowSize(600,600);
        UI.addButton("Restart Game", this::restartGame);
        UI.addSlider("Balloon Num", 10, MAX_BALLOONS, MAX_BALLOONS, this::setBalloon);
        UI.addButton("Lock Score", this::lockScore);
        UI.setMouseListener(this::doMouse);
        UI.setDivider(0.0);
    }   

    /** Start the game:
     *  Clear the graphics pane
     *  Initialise the score information 
     *  Make a new set of Balloons at random positions
     */
    public void restartGame(){
        UI.clearGraphics();
        endGame();
        balloons.clear();
        score = 0;
        activeSize = 0;
        burstSize = 0;
        int count = 0;
        
        // makes v1 balloons
        for (int i=0; i<numBalloon; i++){
            double x = Math.random()*500;
            double y = Math.random()*500;
            balloons.add(new Balloon(x, y));
        }
        
        // checks against every current balloon, makes sure they don't touch
        for (int i=0; i<balloons.size(); i++){
            ArrayList <Balloon> otherBalloons = new ArrayList<Balloon>(); // makes an arraylist for all balloons excl current one
            for (int j=0; j<balloons.size(); j++){
                otherBalloons.add(balloons.get(j));
            }
            otherBalloons.remove(count);
            
            // checks the current balloon against all other balloons
            // while they are touching, will make a new balloon
            for (int j=0; j<otherBalloons.size(); j++){
                while (balloons.get(count).isTouching(otherBalloons.get(j))){
                    // if it is touching then
                    balloons.set(count,(new Balloon(Math.random()*500, Math.random()*500)));
                    //balloons.add(new Balloon(Math.random()*500, Math.random()*500));
                }
            }
            count++;
        }
        // draws final balloons
        for (int i= 0; i<balloons.size(); i++){
            (balloons.get(i)).draw();
        }
    }
    
    public void setBalloon(double userBalloon){
        if (userBalloon % 2 == 1){
            this.numBalloon = userBalloon + 1;
            // makes all odd numbers even
        }
        else{
            this.numBalloon = userBalloon;
        }
        restartGame();
    }
    
    public void lockScore(){
        balloons.clear();
        calculateScore();
        endGame();
    }

    /**
     * Main game action:
     *    Find the balloon at (x,y) if any,
     *  Expand it 
     *  Check whether it is touching another balloon,
     *  If so, burst both balloons.
     *  Redraw the balloon (and the other balloon if it was touching)
     *  Calculate and Report the score. (Hint: use UI.printMessage(...) to report)
     *  If there are no active balloons left, end the game.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            calculateScore();
            int count = 0; // counts to balloons
            while (count<balloons.size()){
                beenPressed(count, x, y); // checks if the balloon(count) has been pressed, if so, expands
                touching(count); // makes a new arraylist full of all the balloons excluding the current one
                count++;
            }
            ArrayList<Boolean> allPopped = new ArrayList<Boolean>();
            for (int i = 0; i<balloons.size(); i++){
                if (balloons.get(i).isActive() == true){
                    allPopped.add(true);
                }
                else{
                    allPopped.add(false);
                }
            }
            if (areAllPopped(allPopped) == true){
                endGame();
            }
        }
    }
    
    public boolean areAllPopped(ArrayList <Boolean> truefalse){
        for (int i=0; i<truefalse.size(); i++){
            if (truefalse.get(i) == true){
                return false; // they afe not all popped
            }
        }
        return true; // they are all popped
    }

    public void calculateScore(){
        for (int scoreCounter = 0; scoreCounter < balloons.size(); scoreCounter++){
            if (balloons.get(scoreCounter).isActive()){ // cycles through the balloons and checks if active
                activeSize = activeSize + balloons.get(scoreCounter).size();
            }
            else { // otherwise if it has already been burst
                burstSize = burstSize + balloons.get(scoreCounter).size();
            }
        }
        score = activeSize - burstSize;
        UI.printMessage("Your score is: " + score);
    }
    
    public void beenPressed(int count, double x, double y){
        if ((balloons.get(count)).on(x,y) == true){
            (balloons.get(count)).expand();
            (balloons.get(count)).draw();
        }
    }

    public void touching(int count){
        ArrayList <Balloon> otherBalloons = new ArrayList<Balloon>();
        for (int j=0; j<balloons.size(); j++){
            otherBalloons.add(balloons.get(j));
        }
        otherBalloons.remove(count); // removes the value that is currently being checked against
        for (int j=0; j<otherBalloons.size(); j++){
            if (balloons.get(count).isTouching(otherBalloons.get(j))){
                balloons.get(count).burst();
            }
        }
    }

    public void endGame(){
        if (highscore < score){
            highscore = score;
        }
        UI.printMessage("Your current highscore is: " + highscore);
    }

    public static void main(String[] arguments){
        BalloonGame bg = new BalloonGame();
        bg.setupGUI();
    }
}
