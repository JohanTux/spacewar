import java.awt.*;

public abstract class AnimationComponent
    extends Container
    implements Runnable {
  private boolean mTrucking = true;
  private long[] mPreviousTimes; // milliseconds
  private int mPreviousIndex;
  private boolean mPreviousFilled;
  private double mFrameRate; // frames per second
  private Image mImage;
  
  public AnimationComponent() {
    mPreviousTimes = new long[128];
    mPreviousTimes[0] = System.currentTimeMillis();
    mPreviousIndex = 1;
    mPreviousFilled = false;
  }
  
  public abstract void timeStep();
  
  public void run() {
    while (mTrucking) {
      render();
      timeStep();
      calculateFrameRate();
    }
  }
  
  protected void render() {
    Graphics g = getGraphics();
    if (g != null) {
      Dimension d = getSize();
      if (checkImage(d)) {
        Graphics imageGraphics = mImage.getGraphics();
        // Clear the image background.
        imageGraphics.setColor(getBackground());
        imageGraphics.fillRect(0, 0, d.width, d.height);
        imageGraphics.setColor(getForeground());
        // Draw this component offscreen.
        paint(imageGraphics);
        // Now put the offscreen image on the screen.
        g.drawImage(mImage, 0, 0, null);
        // Clean up.
        imageGraphics.dispose();
      }
      g.dispose();
    }
  }

  // Offscreen image.
  protected boolean checkImage(Dimension d) {
    if (d.width == 0 || d.height == 0) return false;
    if (mImage == null || mImage.getWidth(null) != d.width
        || mImage.getHeight(null) != d.height) {
      mImage = createImage(d.width, d.height);
    }
    return true;
  }

  protected void calculateFrameRate() {
    // Measure the frame rate
    long now = System.currentTimeMillis();
    int numberOfFrames = mPreviousTimes.length;
    double newRate;
    // Use the more stable method if a history is available.
    if (mPreviousFilled)
      newRate = (double)numberOfFrames /
          (double)(now - mPreviousTimes[mPreviousIndex]) *
          1000.0;
    else
      newRate = 1000.0 /
          (double)(now - mPreviousTimes[numberOfFrames - 1]);
    firePropertyChange("frameRate", mFrameRate, newRate);
    mFrameRate = newRate;
    // Update the history.
    mPreviousTimes[mPreviousIndex] = now;
    mPreviousIndex++;
    if (mPreviousIndex >= numberOfFrames) {
      mPreviousIndex = 0;
      mPreviousFilled = true;
    }
  }

  public double getFrameRate() { return mFrameRate; }
  
  // Property change support.
  private transient AnimationFrame mRateListener;

  public void setRateListener(AnimationFrame af) {
    mRateListener = af;
  }


  public void firePropertyChange(String name, double oldValue,
      double newValue) {
    mRateListener.rateChanged(newValue);
  }
}