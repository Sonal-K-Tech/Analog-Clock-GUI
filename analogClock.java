import java.applet.Applet;
import java.awt.*;
import java.util.Calendar;

public class analogClock extends Applet implements Runnable {
    private Thread thread;
    private Image offScreenImage;
    private Graphics offScreenGraphics;

    @Override
    public void init() {
        setSize(800, 400);
        setBackground(new Color(30, 30, 35)); // Modern dark theme background
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (thread != null) {
            repaint();
            try {
                Thread.sleep(1000); // Refresh every second
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void update(Graphics g) {
        // Implement double buffering to prevent screen flicker
        Dimension d = getSize();
        if (offScreenImage == null || offScreenImage.getWidth(this) != d.width || offScreenImage.getHeight(this) != d.height) {
            offScreenImage = createImage(d.width, d.height);
            offScreenGraphics = offScreenImage.getGraphics();
        }
        offScreenGraphics.setColor(getBackground());
        offScreenGraphics.fillRect(0, 0, d.width, d.height);
        paint(offScreenGraphics);
        g.drawImage(offScreenImage, 0, 0, this);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Enable anti-aliasing for smooth circles and lines
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fetch current system time
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);
        int ampm = time.get(Calendar.AM_PM);
        String ampmStr = (ampm == Calendar.AM) ? "AM" : "PM";

        // Clock center and sizing
        int centerX = 400;
        int centerY = 190;
        int radius = 100;

        // Outer rim shadow / bezel ring
        g2.setColor(new Color(60, 63, 65));
        g2.fillOval(centerX - radius - 10, centerY - radius - 10, (radius + 10) * 2, (radius + 10) * 2);

        // Clock face body
        g2.setColor(Color.WHITE);
        g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Draw hour tick marks around the perimeter
        g2.setColor(new Color(120, 120, 120));
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30);
            int x1 = (int) (centerX + (radius - 10) * Math.sin(angle));
            int y1 = (int) (centerY - (radius - 10) * Math.cos(angle));
            int x2 = (int) (centerX + radius * Math.sin(angle));
            int y2 = (int) (centerY - radius * Math.cos(angle));
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw cardinal numbers (12, 3, 6, 9)
        g2.setColor(new Color(50, 50, 50));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("12", centerX - 9, centerY - radius + 22);
        g2.drawString("3", centerX + radius - 18, centerY + 5);
        g2.drawString("6", centerX - 5, centerY + radius - 10);
        g2.drawString("9", centerX - radius + 10, centerY + 5);

        // Calculate accurate hand angles (subtracting 90° so 0 points straight up)
        double secAngle = Math.toRadians((second * 6) - 90);
        double minAngle = Math.toRadians(((minute + second / 60.0) * 6) - 90);
        double hrAngle = Math.toRadians((((hour == 0 ? 12 : hour) + minute / 60.0) * 30) - 90);

        // Draw Hour Hand (Thick, Dark Gray)
        g2.setColor(new Color(40, 40, 40));
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int hx = (int) (centerX + Math.cos(hrAngle) * (radius * 0.55));
        int hy = (int) (centerY + Math.sin(hrAngle) * (radius * 0.55));
        g2.drawLine(centerX, centerY, hx, hy);

        // Draw Minute Hand (Medium, Blue)
        g2.setColor(new Color(0, 102, 204));
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int mx = (int) (centerX + Math.cos(minAngle) * (radius * 0.75));
        int my = (int) (centerY + Math.sin(minAngle) * (radius * 0.75));
        g2.drawLine(centerX, centerY, mx, my);

        // Draw Second Hand (Thin, Red)
        g2.setColor(new Color(204, 0, 0));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int sx = (int) (centerX + Math.cos(secAngle) * (radius * 0.85));
        int sy = (int) (centerY + Math.sin(secAngle) * (radius * 0.85));
        g2.drawLine(centerX, centerY, sx, sy);

        // Center Pin
        g2.setColor(Color.BLACK);
        g2.fillOval(centerX - 4, centerY - 4, 8, 8);

        // Digital Time Readout String below the analog clock
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 15));
        String timeStr = String.format("%02d:%02d:%02d %s", (hour == 0 ? 12 : hour), minute, second, ampmStr);
        g2.drawString(timeStr, centerX - 50, centerY + radius + 35);
    }

    @Override
    public void destroy() {
        thread = null;
    }
}