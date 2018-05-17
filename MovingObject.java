public class MovingObject {
    
    private double mass;
    private double positionX,
        positionY,
        velocityX,
        velocityY,
        accelX,
        accelY,
        direction; // in radians from straight up
        
        // some class methods - the method names are self explanatory
        double setPosX(double x){  return (positionX = x); }
        double getPosX(){ return positionX;  }
        double setPosY(double y){  return (positionY = y); }
        double getPosY(){ return positionY; }
        double setVelX(double x){  return (velocityX = x); }
        double getVelX(){ return velocityX;  }
        double setVelY(double y){  return (velocityY = y); }
        double getVelY(){ return velocityY; }
        void setAccelX(double x){  accelX = x; }
        double getAccelX(){ return accelX;  }
        void setAccelY(double y){  accelY = y; }
        double getAccelY(){ return accelY; }
        double getMass(){ return mass; }
        void setDirection(double d){  direction = d; }
        double getDirection(){ return direction; }

    
    
} // end of MovingObject class
