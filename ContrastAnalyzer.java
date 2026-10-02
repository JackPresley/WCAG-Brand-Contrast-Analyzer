import javax.swing.*;
import java.awt.*;

public class ContrastAnalyzer {

    static class HexColor {
        int r, g, b;

        public HexColor(String hex) {
            if (hex.startsWith("#")) hex = hex.substring(1);
            this.r = Integer.valueOf(hex.substring(0, 2), 16);
            this.g = Integer.valueOf(hex.substring(2, 4), 16);
            this.b = Integer.valueOf(hex.substring(4, 6), 16);
        }

        public double getLuminance() {
            double[] rgb = {r / 255.0, g / 255.0, b / 255.0};
            for (int i = 0; i < 3; i++) {
                rgb[i] = rgb[i] <= 0.03928 ? rgb[i] / 12.92 : Math.pow((rgb[i] + 0.055) / 1.055, 2.4);
            }
            return 0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2];
        }
    }

    public static double calculateContrast(HexColor c1, HexColor c2) {
        double lum1 = c1.getLuminance();
        double lum2 = c2.getLuminance();
        double lightest = Math.max(lum1, lum2);
        double darkest = Math.min(lum1, lum2);
        return (lightest + 0.05) / (darkest + 0.05);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("WCAG Contrast Analyzer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(350, 200);
            frame.setLayout(new GridLayout(4, 1));

            JPanel panel1 = new JPanel();
            panel1.add(new JLabel("Background (Hex):"));
            JTextField field1 = new JTextField("#1B362F", 10);
            panel1.add(field1);

            JPanel panel2 = new JPanel();
            panel2.add(new JLabel("Text Color (Hex):"));
            JTextField field2 = new JTextField("#F9F6F0", 10);
            panel2.add(field2);

            JButton calcButton = new JButton("Calculate");
            JLabel resultLabel = new JLabel("Ratio: ", SwingConstants.CENTER);
            resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            resultLabel.setOpaque(true);

            calcButton.addActionListener(e -> {
                try {
                    HexColor bg = new HexColor(field1.getText().trim());
                    HexColor fg = new HexColor(field2.getText().trim());
                    
                    double ratio = calculateContrast(bg, fg);
                    String status = ratio >= 4.5 ? "PASS (AA)" : "FAIL";
                    
                    resultLabel.setText(String.format("Ratio: %.2f:1 | %s", ratio, status));
                    resultLabel.setBackground(new Color(bg.r, bg.g, bg.b));
                    resultLabel.setForeground(new Color(fg.r, fg.g, fg.b));
                } catch (Exception ex) {
                    resultLabel.setText("Invalid Hex Code!");
                    resultLabel.setBackground(Color.WHITE);
                    resultLabel.setForeground(Color.RED);
                }
            });

            frame.add(panel1);
            frame.add(panel2);
            frame.add(calcButton);
            frame.add(resultLabel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}