import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.TextAttribute;
import java.text.*;
import java.text.AttributedString.*;
import java.util.Random;

public class SpaceWarIII extends javax.swing.JFrame  implements ActionListener{
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    int playfieldHeight =  screenSize.height - 75;
    int playfieldWidth  =  screenSize.width - 30;
    Canvas gameSpace = new Canvas();                                // create playfield canvas
    Timer paintTimer;                                               // crate a paint paintTimer
    
    boolean ship0Exists = false, ship1Exists = false;               // show ships not there.
    Ship[] player = {new Ship(), new Ship()};                       // create a pair of ships
    
    // create torpedo arrays and
    // fill them with 5 torpedos per ship
    Torpedo[] torps0 = {new Torpedo(), new Torpedo(), new Torpedo(),
    new Torpedo(),new Torpedo(),new Torpedo()};
    Torpedo[] torps1 = {new Torpedo(), new Torpedo(), new Torpedo(),
    new Torpedo(),new Torpedo(),new Torpedo()};
    
    ForegroundStar sol = new ForegroundStar();                      // create a sun
    int numberOfMinorStars = 100;                                    // set number of background stars
    
    BackgroundStar[] bgStar =                                    // create an array for background stars
    new BackgroundStar[numberOfMinorStars];
    
    
    
    // Flags for game conditons.  Indications for "true" state follow identifier.
    // Method(s) that affect changes in the flags follow in parinthesis.
    boolean pairCollision = false;                  // ships have collided (update)
    boolean ship0SunCollision = false;              // player[0] has hit the sun (update)
    boolean ship1SunCollision = false;              // player[1] has hit the sun (update)
    boolean ship1TorpCollision = false;             // player[1] has hit a torpedo (update)
    boolean ship0TorpCollision = false;             // player[0] has hit a torpedo (update)
    boolean ship0RL = false;                        // player[0] is rotating left (keyPressed, keyReleased)
    boolean ship0RR = false;                        // player[0] is rotating right (keyPressed, keyReleased)
    boolean ship1RL = false;                        // player[1] is rotating left (keyPressed, keyReleased)
    boolean ship1RR = false;                        // player[1] is rotating right (keyPressed, keyReleased)
    boolean ship0Thr = false;                       // player[0] is thrusting (keyPressed, keyReleased)
    boolean ship1Thr = false;                       // player[1] is thrusting (keyPressed, keyReleased)
    boolean ship0Torp = false;                      // player[0] has attempted to launch a torpedo (keyPressed, keyReleased)
    boolean ship1Torp = false;                      // player[1] has attempted to launch a torpedo (keyPressed, keyReleased)
    boolean ctrlKey = false;                        // Control key is pressed (keyPressed, keyReleased)
    boolean zKey = false;                           // Z key is pressed (keyPressed, keyReleased)
    boolean qKey = false;                           // Q key is pressed (keyPressed, keyReleased)
    int player1Score = 0;
    int player2Score = 0;
    float x = 1;                                    // this will be a scaling factor when implemented
    Integer Wplayer1Score = new Integer(player1Score);
    Integer Wplayer2Score = new Integer(player2Score);
    int ship0TorpCount = 20;
    int ship1TorpCount = 20;
    /** Creates new form SpaceWarIII */
    public SpaceWarIII() {
        gameControl game = new gameControl();
        this.setTitle("SpaceWar!");

        // wrapping them in an object for display purposes

        
        
        
        
        /**********************************************************************
        * setup the background star attributes
         *********************************************************************/
        for(int i = 0; i < numberOfMinorStars; i++){
            bgStar[i] = new BackgroundStar();
            bgStar[i].setPosX(Math.random() * playfieldWidth + 10);
            bgStar[i].setPosY(Math.random() * playfieldHeight + 10);
            // determine initial brightness of star
            int settingNumber = (int)(Math.random() * 255) ;
            bgStar[i].setBrightness(settingNumber);
            // diameter of star is generated from the same random number as the brightness
            bgStar[i].setDiameter( (settingNumber % 3) + 1);
        }
        
        
        /****************************************
        *   Start Timer
         ***************************************/
        paintTimer = new Timer(10, this);
        paintTimer.setInitialDelay(0);
        paintTimer.setCoalesce(true);
        sol.setMass(75000);
        
        
        initComponents();                                       // Forte-generated initialization block
        
        
        getContentPane().add(gameSpace, BorderLayout.CENTER);   // add canvas to Jframe
        gameSpace.setVisible(true);                             // make canvas visible
        gameSpace.setActiveShip(player[0], player[1]);

        
    }// end SpaceWarIII() (constructor)
    
    
    class gameControl extends Thread{
        
        public void run(){
            
             
            ship0Exists = ship1Exists = true;                       // show ships are there
            
            
            sol.setPosX( playfieldWidth / 2);                                       // place sol
            sol.setPosY( playfieldHeight / 2);
            // setup initial ship locations and velocities
            player[0].setPosX( playfieldWidth     / 4     );
            player[0].setPosY( playfieldHeight    / 2     );
            player[1].setPosX( 3 * playfieldWidth / 4     );
            player[1].setPosY( playfieldHeight    / 2     );
            player[1].setVelY(2.25);
            player[0].setVelY(-2.25);
            
            
            
            
            addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    formKeyPressed(evt);
                }// end keyPressed();
                public void keyReleased(java.awt.event.KeyEvent evt){
                    formKeyReleased(evt);
                }// end keyReleased();
                public void keyTyped(java.awt.event.KeyEvent evt){
                    formKeyTyped(evt);
                }// end keyTyped
            }); // end addKeyListener()
            
            
            
        }// end run()
        
    }// end gameControl()
    
    void update(Ship ship, Ship ship1){
        

        double xCoord = ship.getPosX();             //player 0 current X coord
        double yCoord = ship.getPosY();             //player 0 current Y coord
        double xCoord1 = ship1.getPosX();           //player 1 current X coord
        double yCoord1 = ship1.getPosY();           //player 1 current Y coord
        double direction = ship.getDirection();     //player 0 current direction in rads
        double direction1 = ship1.getDirection();   //player 1 current direction in rads
        double pairXdiff = 100;                     // difference in X position between players (init to 100 for initial pass)
        double pairYdiff = 100;                     // difference in Y position between players
        double sunXdiff0 = 100;                     // X distance between player 0 and sun
        double sunYdiff0 = 100;                     // Y distance between player 0 and sun
        double sunXdiff1 = 100;                     // X distance between player 1 and sun
        double sunYdiff1 = 100;                     // Y distance between player 1 and sun
        double TorpXdif0 = 100;                     // X distance between player 0 and any torpedo
        double TorpYdif0 = 100;                     // Y distance between player 0 and any torpedo
        double TorpXdif1 = 100;                     // X distance between player 1 and any torpedo
        double TorpYdif1 = 100;                     // Y distance between player 1 and any torpedo
        double pairDiff  = 100;                     // Total distance between players
        double sunDist0  = 100;                     // Total distance between player 0 and sun
        double sunDist1  = 100;                     // Total distance between player 1 and sun
        double torpDist0 = 100;                     // Minimum distance between player 0 and any torpedo
        double torpDist1 = 100;                     // Minimum distance between player 0 and any torpedo
          
        
        
        
        
        // The following if statements are for ship wrap-around behaviour
        if(ship.getPosX()< 15) ship.setPosX(playfieldWidth);
        if(ship.getPosY() < 15) ship.setPosY(playfieldHeight);
        if(ship.getPosX() > playfieldWidth ) ship.setPosX(16);
        if(ship.getPosY() > playfieldHeight) ship.setPosY(16);
        if(ship1.getPosX()< 15) ship1.setPosX(playfieldWidth);
        if(ship1.getPosY() < 15) ship1.setPosY(playfieldHeight);
        if(ship1.getPosX() > playfieldWidth ) ship1.setPosX(16);
        if(ship1.getPosY() > playfieldHeight) ship1.setPosY(16);
        
        /**********************************************************************
         * Set Twinkle factor for background stars
         *********************************************************************/
        for (int s = 0; s < numberOfMinorStars; s++){
            
            int multiplier = bgStar[s].getIncMult();
            int currentBrightness = bgStar[s].getBrightness();
            
            if( (int)Math.random() * 50 == 0){
                switch(currentBrightness){
                    case 255: bgStar[s].setBrightness(254);
                    bgStar[s].setIncMult(-1);
                    break;
                    case 0:   bgStar[s].setBrightness(1);
                    bgStar[s].setIncMult(1);
                    break;
                    default:  bgStar[s].setBrightness(currentBrightness + multiplier);
                }// end switch
            }//end if
            
        }
        
        /****************************************************************************************
        * Update Torpedo Positions 
        ****************************************************************************************/
        for (int p = 0; p < 5; p++){
            if (torps0[p].getTorpState()){
                // the .05 tagged onto the end of these is a time increment (to do: variableize this value)
                torps0[p].setPosX( torps0[p].getPosX() +  torps0[p].getVelX() * .55 );
                torps0[p].setPosY( torps0[p].getPosY() +  torps0[p].getVelY() * .55 );
                if(torps0[p].getPosX() > screenSize.width - 30) torps0[p].setPosX(15);
                if(torps0[p].getPosX() < 15) torps0[p].setPosX(screenSize.width - 30);
                if(torps0[p].getPosY() > screenSize.height - 75) torps0[p].setPosY(15);
                if(torps0[p].getPosY() < 15) torps0[p].setPosY(screenSize.height - 75);
                
                
                
                torps0[p].torpDelayCheck();
            }// end if (torps0[t])
            if (torps1[p].getTorpState()){
                // the .05 tagged onto the end of these is a time increment (to do: variableize this value)
                torps1[p].setPosX( torps1[p].getPosX() +  torps1[p].getVelX() * .55 );
                torps1[p].setPosY( torps1[p].getPosY() +  torps1[p].getVelY() * .55 );
                if(torps1[p].getPosX() > screenSize.width - 30) torps1[p].setPosX(15);
                if(torps1[p].getPosX() < 15) torps1[p].setPosX(screenSize.width - 30);
                if(torps1[p].getPosY() > screenSize.height - 75) torps1[p].setPosY(15);
                if(torps1[p].getPosY() < 15) torps1[p].setPosY(screenSize.height - 75);
                torps1[p].torpDelayCheck();
            }// end if (torps1[t])   
                
        } // end for (p)
        
        /*******************************************************************
         *  Set ships velocity due to thruster actions
         ******************************************************************/
        if(ship0Thr){
            ship.setVelX((ship.getVelX() + .00001 + Math.sin(direction)) );
            ship.setVelY((ship.getVelY() + .00001 - Math.cos(direction)) );
        }
        if(ship1Thr){
            ship1.setVelX((ship1.getVelX() + .00001 + Math.sin(direction1)) );
            ship1.setVelY((ship1.getVelY() + .00001 - Math.cos(direction1)) );
        }
        
        ship.setPosX(ship.getVelX() * .5 + ship.getPosX());
        ship.setPosY(ship.getVelY() * .5 + ship.getPosY());
        
        ship1.setPosX(ship1.getVelX() * .5 + ship1.getPosX());
        ship1.setPosY(ship1.getVelY() * .5 + ship1.getPosY());
        //====================================================================
        
        /**********************************************************************
         *  Update ships' attributes based on Sol's gravity well
         *********************************************************************/
        
        for(int i = 0; i < 2; i++){
            // figure ships' distance from sun
            double distanceX = player[i].getPosX() - sol.getPosX();
            double distanceY = player[i].getPosY() - sol.getPosY();
            double dxdy2 = distanceX * distanceX + distanceY * distanceY;
            double distance = (double)(Math.sqrt( dxdy2 ) );
            
            // figure acceleration
            double elapsedTime = .05;
            double totalAccel = sol.getMass() / dxdy2;
            
            // Prevent divide by zero situations and near infinite acceleration
            // when ship ends up extremely close to center of sun (this anomaly
            // might happen when ship is traveling fast and approaches center
            // of sun between updates).  If ship lies within sun's circumference
            // reduce acceleration to zero.  This should never happen without
            // a collision detection, but I'm leaving this code in for safety's
            // sake.
            if(distance > 20){
                player[i].setAccelX( totalAccel * Math.abs(distanceX / distance ));
                player[i].setAccelY( totalAccel * Math.abs(distanceY / distance ));
            } else {
                player[i].setAccelX(0);
                player[i].setAccelY(0);
            }
            
            if( distanceX >= 0 ) player[i].setAccelX(-(player[i].getAccelX()) );
            if( distanceY >= 0 ) player[i].setAccelY(-(player[i].getAccelY()) );
            
            // figure updated velocity
            player[i].setVelX( player[i].getVelX() + player[i].getAccelX() * elapsedTime);
            player[i].setVelY( player[i].getVelY() + player[i].getAccelY() * elapsedTime);
            // figure updated position
            player[i].setPosX( player[i].getPosX() + player[i].getVelX() * elapsedTime);
            player[i].setPosY( player[i].getPosY() + player[i].getVelY() * elapsedTime);
        }
        //====================================================================================

        
        /************************************************************************************
         *  Collision Detection Routines
         ************************************************************************************/
        // figure component-wise differentials
        pairXdiff = Math.abs( (player[0].getPosX() - player[1].getPosX() ) );
        pairYdiff = Math.abs( (player[0].getPosY() - player[1].getPosY() ) );
        sunXdiff0 = Math.abs( ( player[0].getPosX() - sol.getPosX() ) );
        sunYdiff0 = Math.abs( ( player[0].getPosY() - sol.getPosY() ) );
        sunXdiff1 = Math.abs( ( player[1].getPosX() - sol.getPosX() ) );
        sunYdiff1 = Math.abs( ( player[1].getPosY() - sol.getPosY() ) );
        
        
        // figure total differentials
        pairDiff  = Math.sqrt( pairXdiff * pairXdiff + pairYdiff * pairYdiff );
        sunDist0  = Math.sqrt( sunXdiff0 * sunXdiff0 + sunYdiff0 * sunYdiff0 );
        sunDist1  = Math.sqrt( sunXdiff1 * sunXdiff1 + sunYdiff1 * sunYdiff1 );
        
        // check to see if ships collide... if so, set new position & velocity
        if(pairDiff < 15){
            player[0].setPosX(playfieldWidth / 6);
            player[0].setPosY(playfieldHeight / 2);
            player[0].setVelX(.5);
            player[0].setVelY(2);
            player[1].setPosX(4 * playfieldWidth / 5);
            player[1].setPosY(playfieldHeight / 2);
            player[1].setVelX(1);
            player[1].setVelY(4);
            ship1TorpCount = 20;
            ship0TorpCount = 20;
        }
        // check to see if ships get too close to sun.. if so set new pos & vel
        if(sunDist0 <= 20){
            player[0].setPosX(75);
            player[0].setPosY(237);
            player[0].setVelX(0);
            player[0].setVelY(2);
            player1Score -= 5;
            ship0TorpCount = 20;
        }
        if(sunDist1 <= 20){
            player[1].setPosX(600);
            player[1].setPosY(237);
            player[1].setVelX(5);
            player[1].setVelY(6.75);
            player2Score -=5;
            ship1TorpCount=20;
        }
        
        /**********************************************************************
        * check for torpedo collisions
        **********************************************************************/
        
        
        
        for( int c = 0; c < 5; c++){
            
            //  These first two IF statements check to see if any torpedo has hit
            //  the sun.
            
            if( ( Math.abs( torps0[c].getPosX() - sol.getPosX() ) <= 20 )
                && ( Math.abs( torps0[c].getPosY() - sol.getPosY() ) <= 20 ) ){
                    torps0[c].setTorpState(false);
            } // end if 
            if( ( Math.abs( torps1[c].getPosX() - sol.getPosX() ) <= 20 )
                && ( Math.abs( torps1[c].getPosY() - sol.getPosY() ) <= 20 ) ){
                    torps1[c].setTorpState(false);
            } // end if 
            
            double player0toTorp0 =
                Math.sqrt( ( ( player[0].getPosX() - torps0[c].getPosX() ) * ( player[0].getPosX() - torps0[c].getPosX() ) )
                +          ( ( player[0].getPosY() - torps0[c].getPosY() ) * ( player[0].getPosY() - torps0[c].getPosY() ) ) 
                        );
            double player0toTorp1 =
                Math.sqrt( ( ( player[0].getPosX() - torps1[c].getPosX() ) * ( player[0].getPosX() - torps1[c].getPosX() ) )
                +          ( ( player[0].getPosY() - torps1[c].getPosY() ) * ( player[0].getPosY() - torps1[c].getPosY() ) ) 
                        );
            
            /********* set player 0 torp circumstances if collision occurs */
            
            if( (player0toTorp0 < 20) && (torps0[c].getTorpState() ) ){
                ship0TorpCollision = true;
                torps0[c].setTorpState(false);
                torps0[c].setPosX(100000);
                torps0[c].setPosY(100000);
                player1Score -= 5;
                ship0TorpCount = 20;
            }// end if
            if ( ( player0toTorp1 < 20)  && (torps1[c].getTorpState() ) ){
                ship0TorpCollision = true;
                torps1[c].setTorpState(false);
                torps1[c].setPosX(100000);
                torps1[c].setPosY(100000);
                player2Score += 10;
                ship0TorpCount = 20;
            }// end if 
            /***************************************************************/
            //  check the same things for player 1
            double player1toTorp0 =
                Math.sqrt( ( ( player[1].getPosX() - torps0[c].getPosX() ) * ( player[1].getPosX() - torps0[c].getPosX() ) )
                +          ( ( player[1].getPosY() - torps0[c].getPosY() ) * ( player[1].getPosY() - torps0[c].getPosY() ) ) 
                        );
            double player1toTorp1 =
                Math.sqrt( ( ( player[1].getPosX() - torps1[c].getPosX() ) * ( player[1].getPosX() - torps1[c].getPosX() ) )
                +          ( ( player[1].getPosY() - torps1[c].getPosY() ) * ( player[1].getPosY() - torps1[c].getPosY() ) ) 
                        );
            /********* set player 1 torp circumstances if collision occurs */
            if( ( player1toTorp0 < 20) && (torps0[c].getTorpState() ) ) {
                ship1TorpCollision = true;
                torps0[c].setTorpState(false);
                torps0[c].setPosX(100000);
                torps0[c].setPosY(100000);
                player1Score += 10;
                ship1TorpCount = 20;
            }// end if
            if ( (player1toTorp1 < 20)  && (torps1[c].getTorpState() ) ) {
                ship1TorpCollision = true;
                torps1[c].setTorpState(false);
                torps1[c].setPosX(100000);
                torps1[c].setPosY(100000);
                player2Score -=5;
                ship1TorpCount = 20;
            }// end if
            /****************************************************************/
            
        }// end for loop (c)
    
                
        
        if (ship0TorpCollision){
            player[0].setPosX(600 * Math.random() );
            player[0].setPosY(60);
            player[0].setVelX(-2);
            player[0].setVelY(0);
            ship0TorpCollision = false;
        }
        
        if (ship1TorpCollision){
            player[1].setPosX(600 * Math.random() );
            player[1].setPosY(screenSize.height - 60);
            player[1].setVelX(2);
            player[1].setVelY(0);
            ship1TorpCollision = false;
        }
            
        

        /*******************************************************************
         *  Ship Rotation formulae (based on key event-set flags)
         *******************************************************************/
        if(ship0RR){
            ship.setDirection(direction + Math.PI / 16);
        }
        if(ship0RL){
            ship.setDirection(direction - Math.PI / 16);
        }
        if(ship1RR){
            ship1.setDirection(direction1 + Math.PI / 16);
        }
        if(ship1RL){
            ship1.setDirection(direction1 - Math.PI / 16);
        }
        
         /*************************************************************************************
         *  Torpedo launch routines
         ************************************************************************************/
        if( ship0Torp && (ship0TorpCount > 0) ){
            for (int t = 0; t < 5; t++){
                if ( !torps0[t].getTorpState() ){
                    torps0[t].setTorpDirection( direction );
                    torps0[t].setTorpState(true);
                    torps0[t].setPosX( player[0].getPosX() + Math.sin(direction) * 20);
                    torps0[t].setPosY( player[0].getPosY() - Math.cos(direction) * 20);
                    torps0[t].setVelX( player[0].getVelX() + 
                        torps0[t].getTorpVel() * Math.sin(direction)  );
                    torps0[t].setVelY( player[0].getVelY() + 
                        torps0[t].getTorpVel() * -Math.cos(direction) );
                    //torps0[t].setTorpDirection( direction );
                    
                    ship0TorpCount--;
                    
                    break;
                }// end if(!torps0[t])
                
            }// end for (t)
            ship0Torp = false;                  // reset the fire attempt flag
        }// end if(ship0torp)
        
        if( ship1Torp && (ship1TorpCount > 0) ){
            for (int t = 0; t < 5; t++){
                if ( !torps1[t].getTorpState() ){
                    torps1[t].setTorpState(true);
                    torps1[t].setTorpDirection( direction1 );
                    torps1[t].setPosX( player[1].getPosX() + Math.sin(direction1) * 20);
                    torps1[t].setPosY( player[1].getPosY() - Math.cos(direction1) * 20);
                    torps1[t].setVelX( player[1].getVelX() + 
                        torps1[t].getTorpVel() * Math.sin(direction1)  );
                    torps1[t].setVelY( player[1].getVelY() + 
                        torps1[t].getTorpVel() * -Math.cos(direction1) );
                    //torps1[t].setTorpDirection( direction1 );
                    
                    ship1TorpCount--;
                    break;
                }// end if(!torps0[t])
                
            }// end for (t)
            ship1Torp = false;                  // reset the fire attempt flag
        }// end if(ship1torp)
        
      
      // update Score Wrappers to reflect scoring changes
        Wplayer1Score = new Integer(player1Score);
        Wplayer2Score = new Integer(player2Score);
        
    }// end update()
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        gameSpace.requestFocus();
        gameSpace.pt1 -= 1;
        gameSpace.pt2 += 1;
        gameSpace.repaint();
        update(player[0], player[1]);
        
    }// end actionPerformed for SpaceWarIII class
    
    public void startAnimation(){
        paintTimer.start();
    }
    public void stopAnimtion(){
        paintTimer.stop();
    }
    
    
 /*****************************************************************************   
  *
  * The next block of methods was originally added by FORTE... I have mucked
  * about with it such that FORTE no longer recognizes it as natively generated.
  ****************************************************************************/
    
    private void initComponents() {
        buttonStartQuit = new javax.swing.JButton();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        buttonStartQuit.setText("Start");
        buttonStartQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartQuitActionPerformed(evt);
            }
        });
        
        buttonStartQuit.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    buttonKeyPressed(evt);
                }// end keyPressed();
                public void keyReleased(java.awt.event.KeyEvent evt){
                    buttonKeyReleased(evt);
                }// end keyReleased();
               
            }); // end addKeyListener()
            
        
        
        getContentPane().add(buttonStartQuit, java.awt.BorderLayout.NORTH);
        
        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(screenSize.width, screenSize.height));
        //setLocation((screenSize.width-playfieldWidth + 10)/2,(screenSize.height-playfieldHeight - 75)/2);
    }
    
    /**********************************************
     *  END FORTE GENERATED CODE
     *********************************************/
    private void buttonKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode() == evt.VK_CONTROL) ctrlKey = true;
        if(evt.getKeyCode() == evt.VK_Z) {
            zKey = true;
            this.startAnimation();
            new gameControl().start();                                 //start the game
        }
        if(evt.getKeyCode() == evt.VK_Q){
            qKey = true;
            if( ctrlKey ) System.exit(0);
        }
        
    }// end buttonKeyPressed()
    private void buttonKeyReleased(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode() == evt.VK_CONTROL) ctrlKey = false;
        if(evt.getKeyCode() == evt.VK_Z) zKey = false;
        
        
    }// end buttonKeyReleased()
    
    
    private void formKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode() == evt.VK_A) ship0RL = true;
        if(evt.getKeyCode() == evt.VK_D) ship0RR = true;
        if(evt.getKeyCode() == evt.VK_J) ship1RL = true;
        if(evt.getKeyCode() == evt.VK_L) ship1RR = true;
        if(evt.getKeyCode() == evt.VK_W) ship0Thr = true;
        if(evt.getKeyCode() == evt.VK_I) ship1Thr = true;
        if(evt.getKeyCode() == evt.VK_S) ship0Torp = true;
        if(evt.getKeyCode() == evt.VK_K) ship1Torp = true;
        if(evt.getKeyCode() == evt.VK_PLUS) x += .1;
        if(evt.getKeyCode() == evt.VK_PLUS) x -= .1;
        if(evt.getKeyCode() == evt.VK_CONTROL) ctrlKey = true;
        if(evt.getKeyCode() == evt.VK_Z) {
            zKey = true;
            if( ctrlKey ) new gameControl().start();
        }
        if(evt.getKeyCode() == evt.VK_Q){
            qKey = true;
            if( ctrlKey ) System.exit(0);
        }
        
       
    }
    private void formKeyTyped(java.awt.event.KeyEvent evt){
    
    }
    private void formKeyReleased(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode() == evt.VK_A) ship0RL = false;
        if(evt.getKeyCode() == evt.VK_D) ship0RR = false;
        if(evt.getKeyCode() == evt.VK_J) ship1RL = false;
        if(evt.getKeyCode() == evt.VK_L) ship1RR = false;
        if(evt.getKeyCode() == evt.VK_W) ship0Thr = false;
        if(evt.getKeyCode() == evt.VK_I) ship1Thr = false;
        if(evt.getKeyCode() == evt.VK_S) ship0Torp = false;
        if(evt.getKeyCode() == evt.VK_K) ship1Torp = false;
        if(evt.getKeyCode() == evt.VK_CONTROL) ctrlKey = false;
        if(evt.getKeyCode() == evt.VK_Z) zKey = false;
        if(evt.getKeyCode() == evt.VK_Q) qKey = false;
       
        
    }
    
    private void buttonStartQuitActionPerformed(java.awt.event.ActionEvent evt) {
        if(buttonStartQuit.getText() == "Start"){
            
            this.startAnimation();
            
            new gameControl().start();                                 //start the game
            
            
        }else System.exit(0);
    }
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        System.exit(0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new SpaceWarIII().show();
        
    }// end main()
    
    
    // Variables declaration - do not modify
    private javax.swing.JButton buttonStartQuit;
    // End of variables declaration
    
    class Canvas extends JPanel {
        
        Ship shipRef0;
        Ship shipRef1;
        int pt1= 3, pt2=1;
        
        // Thanks to Neil Hall for pointing this trick out to me:  apparently
        // JPanels have issues with gaining focus unless you override the
        // following method: 
        
        public boolean isFocusTraversable(){
            return true;
        }
        
        // this method verifies that drawing will occur for each individual ship
        public void setActiveShip(Ship activeShip0, Ship activeShip1){
            shipRef0 = activeShip0;
            shipRef1 = activeShip1;
        }
        
        // overriding jPanel's paintComponent method
        public void paintComponent(Graphics g){
            if(pt1 < 0) pt1 = 100000;
            Color deepRed = new Color(180,26,26);
            Color thrustBlue = new Color(187,255,255);
            Color brtRed = new Color(255,120,120, 255);
            Color transPur = new Color(255,0,255, 255);
            Font font = new Font("Serif", Font.PLAIN, 96);
            Font font2 = new Font("Times New Roman", Font.ITALIC, 40);
            Font font3 = new Font("Serif", Font.ITALIC, 40);
            Font font4 = new Font("Times New Roman", Font.PLAIN, 40);
            Font font5 = new Font("Times New Roman", Font.PLAIN, 30);
            Font scoreFont = new Font("System", Font.BOLD, 23);
            
            Graphics2D g2 = (Graphics2D)g;
            GeneralPath exhaust = new GeneralPath();                    //path for ship0 exhaust
            GeneralPath exhaust1 = new GeneralPath();                   //path for ship1 exhaust
            
            
            
            g2.setPaintMode();
            
            //  Draw a red thin rectangle around perimiter of playspace x*
            g2.setColor(deepRed);
            g2.fillRect(0,0,10,playfieldHeight + 10);
            g2.fillRect(10,0,playfieldWidth + 10,10);
            g2.fillRect(playfieldWidth + 10,10,10,playfieldHeight + 10);
            g2.fillRect(0,playfieldHeight + 10, playfieldWidth + 10,10);
            //  Draw a big black rectangle in the center of playspace
            g2.setColor(Color.black);
            g2.fillRect(10,10, playfieldWidth, playfieldHeight);
            //AffineTransform at = new AffineTransform();
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            
            
            if(!ship0Exists){
                GradientPaint gp = new GradientPaint( pt1,20, Color.yellow, pt2,30, Color.orange, true);
                g2.setPaint(gp);
                
                g2.setFont(font);
                g2.drawString("SpaceWar!", 40, 120);
                g2.setColor(Color.red);
                g2.setFont(font2);
                g2.drawString ("Controls:",65, 200);
                g2.drawString ("Rotate Left:", 65, 280);
                g2.drawString ("Rotate Right:", 65, 350);
                g2.drawString ("Thrust:", 65, 420);
                g2.drawString ("Torpedo:", 65, 490);
                g2.setColor(Color.lightGray);
                g2.setFont(font4);
                g2.drawString("Colonial Viper            Cylon Raider", 320, 200);
                g2.setColor(Color.blue);
                g2.drawString("       <A>                            <J>"   , 320, 280);
                g2.drawString("       <D>                            <L>", 320, 350);
                g2.drawString("       <W>                            <I>", 320, 420);
                g2.drawString("       <S>                            <K>", 320, 490);
                g2.setPaint(gp);
                g2.fillRect(60, 220, 850, 8);
                
                g2.setColor(Color.gray);
                g2.drawString("<CTRL><Z> To Start or Restart Game", 65, 560);
                g2.drawString("<CTRL><Q> To Exit Game", 65, 630);
                
                
                
               
            }
                
            // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if(ship0Exists){
                // display the score
                g2.setColor(Color.white);
                g2.setFont(scoreFont);
                g2.drawString("Colonial Warrior:", x*18, x*30);
                g2.drawString("Cylon Centurion:", screenSize.width - x*400, x*30);
                g2.setColor(thrustBlue);
                g2.drawString( Wplayer1Score.toString(), x*250, x*30);
                g2.setColor(brtRed);
                g2.drawString(Wplayer2Score.toString(), screenSize.width - x*170, x*30);
                
                // Draw the minor stars
                for (int s = 0; s < numberOfMinorStars; s++){
                    Color starColor = new Color(bgStar[s].getBrightness(),bgStar[s].getBrightness(),bgStar[s].getBrightness() );
                    g2.setColor(starColor);
                    g2.fillOval((int)bgStar[s].getPosX(),(int) bgStar[s].getPosY(), 3,3);
                }
                
                
                
                
                //  Draw the sun
                Color opaqueRed = new Color(255,0,0,250);
                Color opaqueOrange = new Color(255,155,50,255);
                Color transYellow = new Color(255,255,0,255);
                Color transGray = new Color(25,25,25,255);
                Color transWhite = new Color(255,255,255,255);
                //
                RoundGradientPaint rgp4 = new RoundGradientPaint((int)sol.getPosX(),(int)sol.getPosY() , transYellow, 
                    new Point2D.Double(0,(pt1 % 3000)+1), transGray);
                g2.setPaint(rgp4);
                g2.fillArc( (int)sol.getPosX()-50, (int)sol.getPosY()-50,100,100,0, 360);
                
                RoundGradientPaint rgp3 = new RoundGradientPaint((int)sol.getPosX(),(int)sol.getPosY() , brtRed, 
                    new Point2D.Double(0,(pt1 % 200)+1), transYellow);
                g2.setPaint(rgp3);
                g2.fillArc( (int)sol.getPosX()-25, (int)sol.getPosY()-25,50,50,0, 360);
             
                RoundGradientPaint rgp = new RoundGradientPaint((int)sol.getPosX(),(int)sol.getPosY() , transYellow, 
                    new Point2D.Double(0,(pt2 % 2000)+1), opaqueOrange);
                RoundGradientPaint rgp2 = new RoundGradientPaint((int)sol.getPosX(),(int)sol.getPosY() , transWhite, 
                    new Point2D.Double(0,(pt1 % 1500)+1), opaqueOrange);
                g2.setPaint(rgp);
                g2.fillArc((int)sol.getPosX()-20, (int)sol.getPosY()-20,40,40,0, 360);
                g2.setPaint(rgp2);
                g2.fillArc((int)sol.getPosX()-18, (int)sol.getPosY()-18,36,36,0, 360);
                
                
                
                
            }
            /********************************************************************************
             * draw player[0]'s ship
             *******************************************************************************/
            if(ship0Exists){
                
                GeneralPath ship = new GeneralPath(); //path for colonial viper
                GeneralPath interior3  = new GeneralPath(); // Red Stripe
                GeneralPath interior4  = new GeneralPath(); // Engine Block
                GeneralPath interior5  = new GeneralPath(); // canopy
                
                // x* is the scaling factor.  It's 1 by default.  I haven't got this integrated with the
                // rest of the system yet.
                ship.moveTo(x* -4 + (float)shipRef0.getPosX(), x* 4 + (float)shipRef0.getPosY());
                ship.lineTo( x*-7 + (float)shipRef0.getPosX(), x* 4 + (float)shipRef0.getPosY());
                ship.lineTo( x*-5 + (float)shipRef0.getPosX(), x* 2 + (float)shipRef0.getPosY());
                ship.lineTo( x*-4 + (float)shipRef0.getPosX(), x*-5 + (float)shipRef0.getPosY());
                ship.lineTo( x*-4 + (float)shipRef0.getPosX(), x*-7 + (float)shipRef0.getPosY());
                ship.lineTo( x*-5 + (float)shipRef0.getPosX(),x* -7 + (float)shipRef0.getPosY());
                ship.lineTo( x*-5  + (float)shipRef0.getPosX(),x*-5 + (float)shipRef0.getPosY());
                ship.lineTo( x*-7 + (float)shipRef0.getPosX(),  x*1 + (float)shipRef0.getPosY());
                ship.lineTo(x*-12 + (float)shipRef0.getPosX(),  x*6 + (float)shipRef0.getPosY());
                ship.lineTo( x*-12 + (float)shipRef0.getPosX(), x* 15 + (float)shipRef0.getPosY());
                ship.lineTo( x*-4 + (float)shipRef0.getPosX(),  x*16 + (float)shipRef0.getPosY());
                ship.lineTo(x*-4 + (float)shipRef0.getPosX(),  x*18 + (float)shipRef0.getPosY());
                ship.lineTo( x*-1 + (float)shipRef0.getPosX(), x* 18 + (float)shipRef0.getPosY());
                ship.lineTo( x*-1 + (float)shipRef0.getPosX(), x* 16 + (float)shipRef0.getPosY());
                ship.lineTo( x*1 + (float)shipRef0.getPosX(), x* 16 + (float)shipRef0.getPosY());
                ship.lineTo( x*1 + (float)shipRef0.getPosX(), x* 18 + (float)shipRef0.getPosY());
                ship.lineTo(x*4 + (float)shipRef0.getPosX(),  x*18 + (float)shipRef0.getPosY());
                ship.lineTo(x* 4 + (float)shipRef0.getPosX(), x* 16 + (float)shipRef0.getPosY());
                ship.lineTo(x* 12 + (float)shipRef0.getPosX(),x*  15 + (float)shipRef0.getPosY());
                ship.lineTo(x*12 + (float)shipRef0.getPosX(),  x*6 + (float)shipRef0.getPosY());
                ship.lineTo( x*7 + (float)shipRef0.getPosX(),  x*1 + (float)shipRef0.getPosY());
                ship.lineTo( x*5  + (float)shipRef0.getPosX(),x* -5 + (float)shipRef0.getPosY());
                ship.lineTo( x*5 + (float)shipRef0.getPosX(), x*-7 + (float)shipRef0.getPosY());
                ship.lineTo( x*4 + (float)shipRef0.getPosX(), x*-7 + (float)shipRef0.getPosY());
                ship.lineTo( x*4 + (float)shipRef0.getPosX(), x*-5 + (float)shipRef0.getPosY());
                ship.lineTo( x*5 + (float)shipRef0.getPosX(), x* 2 + (float)shipRef0.getPosY());
                ship.lineTo(x* 7 + (float)shipRef0.getPosX(),x*  4 + (float)shipRef0.getPosY());
                ship.lineTo(x* 4 + (float)shipRef0.getPosX(),x*  4 + (float)shipRef0.getPosY());
                ship.lineTo(x* 2 + (float)shipRef0.getPosX(),x*  -17 + (float)shipRef0.getPosY());
                ship.lineTo(x* -2 + (float)shipRef0.getPosX(),x*  -17 + (float)shipRef0.getPosY());
                
                
                ship.closePath();
                
                interior3.moveTo( x*-3 + (float)shipRef0.getPosX(), x* 4 + (float)shipRef0.getPosY());
                interior3.lineTo(x* -1 + (float)shipRef0.getPosX(),  x*-4 + (float)shipRef0.getPosY());
                interior3.lineTo( x*1 + (float)shipRef0.getPosX(),x*  -4 + (float)shipRef0.getPosY());
                interior3.lineTo(x* 3 + (float)shipRef0.getPosX(),x* 4 + (float)shipRef0.getPosY());
                interior3.closePath();
                
                interior4.moveTo(x* -6 + (float)shipRef0.getPosX(), x* 5 + (float)shipRef0.getPosY());
                interior4.lineTo(x* 6 + (float)shipRef0.getPosX(),  x*5 + (float)shipRef0.getPosY());
                interior4.lineTo(x* 6 + (float)shipRef0.getPosX(), x* 15 + (float)shipRef0.getPosY());
                interior4.lineTo(x* -6 + (float)shipRef0.getPosX(),x* 15 + (float)shipRef0.getPosY());
                interior4.closePath();
                
                interior5.moveTo(x* -8 + (float)shipRef0.getPosX(), x* 3 + (float)shipRef0.getPosY());
                interior5.lineTo(x* -8 + (float)shipRef0.getPosX(),x*  9 + (float)shipRef0.getPosY());
                interior5.lineTo(x* -6 + (float)shipRef0.getPosX(),x*  9 + (float)shipRef0.getPosY());
                interior5.lineTo(x* -5 + (float)shipRef0.getPosX(), x*8 + (float)shipRef0.getPosY());
                interior5.lineTo(x* 5 + (float)shipRef0.getPosX(), x*8 + (float)shipRef0.getPosY());
                interior5.lineTo(x* 6 + (float)shipRef0.getPosX(), x* 9 + (float)shipRef0.getPosY());
                interior5.lineTo(x* 8 + (float)shipRef0.getPosX(),x*  9 + (float)shipRef0.getPosY());
                
                interior5.lineTo( x*8 + (float)shipRef0.getPosX(),  x*3 + (float)shipRef0.getPosY());
                interior5.lineTo( x*10 + (float)shipRef0.getPosX(), x* 5 + (float)shipRef0.getPosY());
                interior5.lineTo( x*10 + (float)shipRef0.getPosX(), x*12 + (float)shipRef0.getPosY());
                interior5.lineTo(x* 6 + (float)shipRef0.getPosX(), x*12 + (float)shipRef0.getPosY());
                interior5.lineTo( x*5 + (float)shipRef0.getPosX(),  x*11 + (float)shipRef0.getPosY());
                interior5.lineTo( x*-5 + (float)shipRef0.getPosX(),  x*11 + (float)shipRef0.getPosY());
                
                interior5.lineTo( x*-6 + (float)shipRef0.getPosX(),  x*12 + (float)shipRef0.getPosY());
                interior5.lineTo( x*-10 + (float)shipRef0.getPosX(), x* 12 + (float)shipRef0.getPosY());
                interior5.lineTo(x* -10 + (float)shipRef0.getPosX(), x*5 + (float)shipRef0.getPosY());
                interior5.closePath();
                
                if(ship0Thr){
                    
                    exhaust.moveTo(x* -1 + (float)shipRef0.getPosX(),x* 18 + (float)shipRef0.getPosY());
                    exhaust.lineTo(x* -4 + (float)shipRef0.getPosX(),x* 18 + (float)shipRef0.getPosY());
                    exhaust.lineTo(x*-5 +(float)shipRef0.getPosX(), x*22 + (float)shipRef0.getPosY());
                    exhaust.quadTo(x*0 +(float)shipRef0.getPosX(), x*35 + (float)shipRef0.getPosY(),
                      x*  5 +(float)shipRef0.getPosX(), x*22 + (float)shipRef0.getPosY() );
                    exhaust.lineTo( x*4 + (float)shipRef0.getPosX(), x*18 + (float)shipRef0.getPosY());
                    exhaust.lineTo( x*1 + (float)shipRef0.getPosX(), x*18 + (float)shipRef0.getPosY());
                    exhaust.lineTo( x*0 + (float)shipRef0.getPosX(), x*22 + (float)shipRef0.getPosY());
                    
                    exhaust.closePath();
                }
                
                g2.rotate( (shipRef0.getDirection()  ), shipRef0.getPosX(), shipRef0.getPosY());
                
                g2.setColor(Color.lightGray );
                g2.fill(ship);
                g2.setColor(Color.black);
                g2.draw(interior3);
                g2.setColor(Color.gray);
                g2.fill(interior4);
                g2.setColor(deepRed);
                g2.fill(interior5);
                if(ship0Thr){
                    g2.setColor(thrustBlue);
                    g2.fill(exhaust);
                }
                // reorient the g2 object
                g2.rotate( -(shipRef0.getDirection()  ), shipRef0.getPosX(), shipRef0.getPosY());
            }// end if
           
            
             /********************************************************************************
             * draw player[1]'s ship
             *******************************************************************************/
            if (ship1Exists){
                GeneralPath ship1 = new GeneralPath(); // path for cylon raider
                
                if(ship1Thr){
                    
                    exhaust1.moveTo( -1 + (float)shipRef1.getPosX(), 18 + (float)shipRef1.getPosY());
                    exhaust1.lineTo( -4 + (float)shipRef1.getPosX(), 18 + (float)shipRef1.getPosY());
                    exhaust1.lineTo(-5 +(float)shipRef1.getPosX(), 22 + (float)shipRef1.getPosY());
                    exhaust1.quadTo(0 +(float)shipRef1.getPosX(), 28 + (float)shipRef1.getPosY(),
                        5 +(float)shipRef1.getPosX(), 22 + (float)shipRef1.getPosY() );
                    exhaust1.lineTo( 4 + (float)shipRef1.getPosX(), 18 + (float)shipRef1.getPosY());
                    exhaust1.lineTo( 1 + (float)shipRef1.getPosX(), 18 + (float)shipRef1.getPosY());
                    exhaust1.lineTo( 0 + (float)shipRef1.getPosX(), 22 + (float)shipRef1.getPosY());
                    
                    exhaust1.closePath();
                }
                
                ship1.moveTo( -4 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                ship1.lineTo( -4 + (float)shipRef1.getPosX(), 9 + (float)shipRef1.getPosY());
                ship1.lineTo(-9 +(float)shipRef1.getPosX(), 9 + (float)shipRef1.getPosY());
                ship1.lineTo( -9 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                ship1.lineTo(-12 +(float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                ship1.lineTo( -12 + (float)shipRef1.getPosX(), 1 + (float)shipRef1.getPosY());
                ship1.lineTo(-13 +(float)shipRef1.getPosX(), 1 + (float)shipRef1.getPosY());
                ship1.lineTo( -13 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                ship1.quadTo(-24 + (float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY(),
                    -13 + (float)shipRef1.getPosX(), -7 + (float)shipRef1.getPosY() );
                ship1.lineTo(-13 +(float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY());
                ship1.lineTo(-12 +(float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY());
                ship1.lineTo(-12 +(float)shipRef1.getPosX(), -5 + (float)shipRef1.getPosY());
                ship1.lineTo(-10 +(float)shipRef1.getPosX(), -5 + (float)shipRef1.getPosY());
                ship1.lineTo(-10 +(float)shipRef1.getPosX(), -8 + (float)shipRef1.getPosY());
                ship1.lineTo(-6 +(float)shipRef1.getPosX(), -9 + (float)shipRef1.getPosY());
                ship1.lineTo(-3 +(float)shipRef1.getPosX(), -11 + (float)shipRef1.getPosY());
                ship1.lineTo(3 +(float)shipRef1.getPosX(), -11 + (float)shipRef1.getPosY());
                ship1.lineTo(6 +(float)shipRef1.getPosX(), -9 + (float)shipRef1.getPosY());
                 ship1.lineTo(10 +(float)shipRef1.getPosX(), -8 + (float)shipRef1.getPosY());
                 ship1.lineTo(10 +(float)shipRef1.getPosX(), -5 + (float)shipRef1.getPosY());
                 ship1.lineTo(12 +(float)shipRef1.getPosX(), -5 + (float)shipRef1.getPosY());
                 ship1.lineTo(12 +(float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY());
                 ship1.lineTo(13 +(float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY());
                 ship1.lineTo( 13 + (float)shipRef1.getPosX(), -7 + (float)shipRef1.getPosY());
                  ship1.quadTo(24 + (float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY(),
                    13 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY() );
                 ship1.lineTo( 13 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                 ship1.lineTo(13 +(float)shipRef1.getPosX(), 1 + (float)shipRef1.getPosY());
                 ship1.lineTo( 12 + (float)shipRef1.getPosX(), 1 + (float)shipRef1.getPosY());
                 ship1.lineTo(12 +(float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                 ship1.lineTo( 9 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());
                 ship1.lineTo( 9 + (float)shipRef1.getPosX(), 9 + (float)shipRef1.getPosY());
                 ship1.lineTo( 4 + (float)shipRef1.getPosX(), 9 + (float)shipRef1.getPosY());
                 ship1.lineTo( 4 + (float)shipRef1.getPosX(), 7 + (float)shipRef1.getPosY());

                
                ship1.closePath();
                
                GeneralPath interior1 = new GeneralPath(); //cockpit
                GeneralPath interior2 = new GeneralPath(); // little red triangle
                

                
                interior1.moveTo( -4 + (float)shipRef1.getPosX(), -9 + (float)shipRef1.getPosY());
                interior1.lineTo(   4 + (float)shipRef1.getPosX(), -9 + (float)shipRef1.getPosY());
                interior1.lineTo(   1 + (float)shipRef1.getPosX(), -3 + (float)shipRef1.getPosY());
                interior1.lineTo(   -1 + (float)shipRef1.getPosX(), -3 + (float)shipRef1.getPosY());
                interior1.closePath();
                
                interior2.moveTo( -9 + (float)shipRef1.getPosX(), 2 + (float)shipRef1.getPosY());
                interior2.lineTo( -9 + (float)shipRef1.getPosX(), -2 + (float)shipRef1.getPosY());
                interior2.lineTo( -6 + (float)shipRef1.getPosX(), 0 + (float)shipRef1.getPosY());
                interior2.closePath();
                
                
                g2.rotate( (shipRef1.getDirection()  ), shipRef1.getPosX(), shipRef1.getPosY());
                
                g2.setColor(Color.lightGray);
                g2.fill(ship1);
                g2.setColor(Color.black);
                g2.fill(interior1);
                g2.setColor(deepRed);
                g2.draw(interior2);
                if(ship1Thr){
                    g2.setColor(Color.orange);
                    g2.fill(exhaust1);
                }
                 // reorient the g2 object
                g2.rotate( -(shipRef1.getDirection()  ), shipRef1.getPosX(), shipRef1.getPosY());
            }// end if
            
            /*****************************************************************
             *  Draw torpedos
             *****************************************************************/
            for (int t = 0; t < 5; t++){
                if (torps0[t].getTorpState() ){
                    g2.setColor(Color.orange);
                    g2.fillOval( (int)torps0[t].getPosX(), (int)torps0[t].getPosY(), (pt2 % 5)+5, (pt2 % 7) + 3);
                }// end if (torps0[t])
                if (torps1[t].getTorpState() ){
                    g2.setColor(Color.red);
                    g2.fillOval( (int)torps1[t].getPosX(), (int)torps1[t].getPosY(), (pt2 % 5) + 5, (pt2 % 7) + 3);
                
                }// end if (torps1[t])
            }// end for (t)
            
            
            
            
        }// end Canvas.paintComponent()
        
        
        
    }// end Canvas
}// end SpaceWarIII

class ForegroundStar extends Star {
    /*  the term mass is actually a product of G and acutal stallar mass
     *      for these purposes G*m = Stellar Mass Units or SMU's
     *      the reference for mass is 1.0 SMU (I'm making up values in all
     *      cases to insure a playable game
     */
    double mass = 5000;
    
    public void setMass( double d ){
        mass = d;
    }
    public double getMass(){
        
        return mass;
    }
} // end of ForegroundStar class

class BackgroundStar extends Star{
    private double mass = 0;
    private int brightness = 0;
    private int diameter = 2;
    private int incMult = 1;
    
    public void setBrightness(int b){ brightness = b; }
    public int getBrightness(){ return brightness; }
    public void setDiameter(int d){ diameter = d; }
    public int getDiameter(){ return diameter; }
    public void setIncMult(int i){ incMult = i; }
    public int getIncMult(){ return incMult; }
    
}// end of BackgroundStar class



class Ship extends MovingObject{
    
    double mass = 1;
    double direction = 0;
    
} // end of Ship class

class Torpedo extends MovingObject{
    
    boolean exists = false;
    private double mass = 0;
    // torpedo velocity should be in addition to firing ship's velocity
    private double torpVelocity = 40;
    private double torpDirection = 0;
    private int torpDelayClicker = 0;       // torpedo expiration timer
    /*
    public Torpedo(){
        exists = false;
    }
    public Torpedo(Ship ship){
    }
    */

    public void setTorpDirection( double d ){ torpDirection = d; }
    public double getTorpDirection(){ return torpDirection; }
    public double getTorpVel(){ return torpVelocity; }
    public boolean getTorpState(){ return exists; }
    public void setTorpState(boolean newState){ exists = newState;}
    
    // sets time that torpedo exists
    public void torpDelayCheck(){
        
        if (torpDelayClicker < 50) torpDelayClicker++;
        else {
            torpDelayClicker = 0;
            exists = false;
        }
    }// end torpDelayCheck

    
    
} // end of Torpedo class


