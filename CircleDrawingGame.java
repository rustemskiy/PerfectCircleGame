import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CircleDrawingGame extends JFrame {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private boolean isDrawing;
    private Point targetCenter;
    private int outOfBounds = 100;
    private int targetRadius;
    private java.util.List<Point> drawnPoints = new java.util.ArrayList<>();
    private int circleCompleteness;
    private Timer drawTimer;
    private Timer idleTimer;
    private int length;

    public CircleDrawingGame() {
        setTitle("Идеальный круг");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel);

        targetCenter = new Point(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);

        drawPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (isInsideTargetCircle(e.getPoint()) && isOutsideSmallCircle(e.getPoint())) {
                    targetRadius = (int) Math.sqrt(Math.pow(e.getX() - targetCenter.x, 2) + Math.pow(e.getY() - targetCenter.y, 2));
                    isDrawing = true;
                    drawTimer.start();
                    idleTimer.start();
                    drawnPoints.clear();
                }
            }


            public void mouseReleased(MouseEvent e) {
                if (isDrawing) {
                    isDrawing = false;
                    drawTimer.stop();
                    idleTimer.stop();
                    analyzeDrawing();
                }
            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isDrawing && isInsideTargetCircle(e.getPoint()) && isOutsideSmallCircle(e.getPoint())) {
                    drawnPoints.add(e.getPoint());
                    drawPanel.repaint();


                } else {
                    drawnPoints.clear();
                }
            }
        });

        drawTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });

        idleTimer = new Timer(4000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isDrawing) {
                    JOptionPane.showMessageDialog(null, "Вы проиграли! У вас есть только 4 секунды.");
                    isDrawing = false;
                    drawTimer.stop();
                    idleTimer.stop();
                    circleCompleteness = 0;
                    repaint();
                }
            }
        });
    }

    private boolean isInsideTargetCircle(Point p) {
        int distanceX = p.x - targetCenter.x;
        int distanceY = p.y - targetCenter.y;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance <= outOfBounds;
    }

    private boolean isOutsideSmallCircle(Point p) {
        int distanceX = p.x - targetCenter.x;
        int distanceY = p.y - targetCenter.y;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance >= 70;
    }

    private void analyzeDrawing() {
        if (drawnPoints.isEmpty()) {
            return;
        }
        if (drawnPoints.size() >= 215){

            for (Point p : drawnPoints) {
                 length += (int) ((targetRadius - Math.abs(targetRadius - Math.sqrt(Math.pow(p.getX() - targetCenter.x, 2) + Math.pow(p.getY() - targetCenter.y, 2)))) / targetRadius *100);
            }
            String feedback = "Круг идеален на: " + length / drawnPoints.size()  + "%";
            JOptionPane.showMessageDialog(this, feedback);
            length = 0;
        }

        drawnPoints.clear();
        repaint();
    }

    private class DrawPanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawOval(targetCenter.x - outOfBounds, targetCenter.y - outOfBounds,
                    outOfBounds * 2, outOfBounds * 2);

            g.drawOval(targetCenter.x - 70, targetCenter.y - 70,
                    140, 140);
            int redValue = (int) (200 * (1 - circleCompleteness / 100.0));
            g.setColor(Color.green);
            g.fillOval(targetCenter.x - outOfBounds, targetCenter.y - outOfBounds, outOfBounds * 2, outOfBounds * 2);
            g.setColor(Color.white);
            g.fillOval(targetCenter.x - 70, targetCenter.y - 70,
                    140, 140);

            for (Point p : drawnPoints) {
                int distanceX = p.x - targetCenter.x;
                int distanceY = p.y - targetCenter.y;
                double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                int colorValue = (int) (255 * (1 - distance / outOfBounds));
                g.setColor(new Color(colorValue, 0, 0));
                g.fillOval(p.x - 5, p.y - 5, 10, 10);
            }
        }
    }
}
