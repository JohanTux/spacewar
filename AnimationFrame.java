import java.awt.*;
import java.text.NumberFormat;

public class AnimationFrame
    extends ApplicationFrame {
  private Label mStatusLabel;
  private NumberFormat mFormat;
  
  public AnimationFrame(AnimationComponent ac) {
    super("AnimationFrame v1.0");
    setLayout(new BorderLayout());
    add(ac, BorderLayout.CENTER);
    add(mStatusLabel = new Label(), BorderLayout.SOUTH);
    // Create a number formatter.
    mFormat = NumberFormat.getInstance();
    mFormat.setMaximumFractionDigits(1);
    // Listen for the frame rate changes.
    ac.setRateListener(this);
    // Kick off the animation.
    Thread t = new Thread(ac);
    t.start();
  }
  
  public void rateChanged(double frameRate) {
    mStatusLabel.setText(mFormat.format(frameRate) + " fps");
  }
}