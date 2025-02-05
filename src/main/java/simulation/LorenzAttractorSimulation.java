package simulation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulates a Lorenz attractor and displays it in a JFrame.
 * The visualization is centered and adjusted by zooming out
 * so that the entire image is visible with sufficient margins.
 */
class LorenzAttractorSimulation extends JFrame {

    public LorenzAttractorSimulation() {
        initUI();
    }

    private void initUI() {
        setTitle("Lorenz-Attraktor Simulation");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        SimulationPanel simulationPanel = new SimulationPanel();
        add(simulationPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LorenzAttractorSimulation::new);
    }

    static class SimulationPanel extends JPanel implements ActionListener, MouseListener {

        // Parameters
        private static final double SIGMA = 10.0;
        private static final double R = 28.0;
        private static final double B = 8.0 / 3.0;
        private static final double TIME_STEP = 0.01;
        private static final int MAX_POINTS = 10000;
        private final double[] initialState = {0.1, 0.0, 0.0};
        private final double[] state = new double[3];
        private final List<double[]> points = new ArrayList<>();
        private static final double SCALE = 15.0;
        private static final int MARGIN = 50;

        public SimulationPanel() {
            // Panelsettings
            setPreferredSize(new Dimension(1000, 800));
            setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
            resetSimulation();
            Timer timer = new Timer(16, this); // ca. 60 FPS
            timer.start();
            addMouseListener(this);
        }

        private void resetSimulation() {
            System.arraycopy(initialState, 0, state, 0, initialState.length);
            points.clear();
            points.add(new double[]{state[0], state[1], state[2]});
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets insets = getInsets();
            int availableWidth = getWidth() - insets.left - insets.right;
            int availableHeight = getHeight() - insets.top - insets.bottom;

            // Center simulation
            int offsetX = insets.left + availableWidth / 2;
            int offsetY = insets.top + availableHeight / 2 + 350;

            // Trajektorie
            for (int i = 1; i < points.size(); i++) {
                double[] prev = points.get(i - 1);
                double[] curr = points.get(i);

                int x1 = offsetX + (int) (prev[0] * SCALE);
                int y1 = offsetY - (int) (prev[2] * SCALE);
                int x2 = offsetX + (int) (curr[0] * SCALE);
                int y2 = offsetY - (int) (curr[2] * SCALE);

                float ratio = (float) i / points.size();
                Color lineColor = new Color(ratio, 0.5f, 1.0f - ratio);
                g2d.setColor(lineColor);
                g2d.drawLine(x1, y1, x2, y2);
            }

            g2d.setColor(Color.WHITE);
            g2d.drawString("Lorenz-Attractor Simulation – Click for Reset", 10, 20);
            g2d.dispose();
        }

        private void updateSimulation() {
            double[] k1 = computeLorenz(state);

            double[] s2 = new double[3];
            for (int i = 0; i < 3; i++) {
                s2[i] = state[i] + TIME_STEP * k1[i] / 2;
            }
            double[] k2 = computeLorenz(s2);

            double[] s3 = new double[3];
            for (int i = 0; i < 3; i++) {
                s3[i] = state[i] + TIME_STEP * k2[i] / 2;
            }
            double[] k3 = computeLorenz(s3);

            double[] s4 = new double[3];
            for (int i = 0; i < 3; i++) {
                s4[i] = state[i] + TIME_STEP * k3[i];
            }
            double[] k4 = computeLorenz(s4);

            for (int i = 0; i < 3; i++) {
                state[i] += TIME_STEP * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]) / 6;
            }

            points.add(new double[]{state[0], state[1], state[2]});
            if (points.size() > MAX_POINTS) {
                points.removeFirst();
            }
        }

        private double[] computeLorenz(double[] s) {
            double dx = SIGMA * (s[1] - s[0]);
            double dy = s[0] * (R - s[2]) - s[1];
            double dz = s[0] * s[1] - B * s[2];
            return new double[]{dx, dy, dz};
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < 5; i++) {
                updateSimulation();
            }
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            resetSimulation();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //Unused
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //Unused
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //Unused
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //Unused
        }
    }
}
