import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class NeumorphismUITool {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Neumorphism Tool");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2));
        frame.getContentPane().setBackground(new Color(85 , 85, 85));
        JButton meanMedianModeButton = createNeumorphicButton("Mean, Median, Mode Calculator");
        JButton importCSVButton = createNeumorphicButton("Import CSV File");
        JButton boxWhiskerButton = createNeumorphicButton("Box and Whisker Plot Maker");
        JButton scatterPlotButton = createNeumorphicButton("Scatter Plot");

        frame.add(meanMedianModeButton);
        frame.add(importCSVButton);
        frame.add(boxWhiskerButton);
        frame.add(scatterPlotButton);
        meanMedianModeButton.addActionListener(e -> showMeanMedianModeDialog());
        importCSVButton.addActionListener(e -> handleCSVImport());
        boxWhiskerButton.addActionListener(e -> showBoxAndWhiskerDialog());
        scatterPlotButton.addActionListener(e -> showScatterPlotDialog());

        frame.setVisible(true);
    }

    private static JButton createNeumorphicButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setBackground(new Color(255, 255, 255));
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 100));
        return button;
    }

    private static void showMeanMedianModeDialog() {
        JFrame dialog = new JFrame("Mean, Median, Mode Calculator");
        dialog.setSize(400, 300);
        dialog.setLayout(new FlowLayout());

        JLabel instructionLabel = new JLabel("Enter numbers with commas (e.g., 1,2,3):");
        JTextField inputField = new JTextField(20);
        JButton calculateButton = new JButton("Calculate");

        JLabel resultLabel = new JLabel();

        calculateButton.addActionListener(e -> {
            String input = inputField.getText();
            String[] numbers = input.split(",");
            try {
                List<Double> data = Arrays.stream(numbers)
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                double mean = calculateMean(data);
                double median = calculateMedian(data);
                String mode = calculateMode(data);

                resultLabel.setText(String.format("Mean: %.2f, Median: %.2f, Mode: %s", mean, median, mode));
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid input. Please enter numbers correctly.");
            }
        });

        dialog.add(instructionLabel);
        dialog.add(inputField);
        dialog.add(calculateButton);
        dialog.add(resultLabel);

        dialog.setVisible(true);
    }

    private static void handleCSVImport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<Double> data = reader.lines()
                        .flatMap(line -> Arrays.stream(line.split(",")))
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());

                double mean = calculateMean(data);
                double median = calculateMedian(data);
                String mode = calculateMode(data);

                JOptionPane.showMessageDialog(null, String.format("Mean: %.2f\nMedian: %.2f\nMode: %s", mean, median, mode));

                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setSelectedFile(new File("output.csv"));
                int saveResult = saveChooser.showSaveDialog(null);
                if (saveResult == JFileChooser.APPROVE_OPTION) {
                    File saveFile = saveChooser.getSelectedFile();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                        writer.write("Mean,Median,Mode\n");
                        writer.write(String.format("%.2f,%.2f,%s\n", mean, median, mode));
                    }
                }

            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Error processing file: " + e.getMessage());
            }
        }
    }

    private static void showBoxAndWhiskerDialog() {
        JFrame dialog = new JFrame("Box and Whisker Plot Maker");
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel instructionLabel = new JLabel("Enter numbers with commas (e.g., 1,2,3):");
        JTextField inputField = new JTextField(20);
        JButton generateButton = new JButton("Generate");

        inputPanel.add(instructionLabel);
        inputPanel.add(inputField);
        inputPanel.add(generateButton);

        BoxPlotPanel plotPanel = new BoxPlotPanel();

        generateButton.addActionListener(e -> {
            String input = inputField.getText();
            try {
                List<Double> data = Arrays.stream(input.split(","))
                        .map(Double::parseDouble)
                        .sorted()
                        .collect(Collectors.toList());
                plotPanel.setData(data);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input.");
            }
        });

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(plotPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private static void showScatterPlotDialog() {
        JFrame dialog = new JFrame("Scatter Plot Maker");
        dialog.setSize(600, 800);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel instructionLabel = new JLabel("Enter number of points:");
        JTextField pointsField = new JTextField(5);
        JButton generateButton = new JButton("Generate");

        inputPanel.add(instructionLabel);
        inputPanel.add(pointsField);
        inputPanel.add(generateButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        ScatterPlotPanel plotPanel = new ScatterPlotPanel();
        plotPanel.setSize(100,200);
        generateButton.addActionListener(e -> {
            try {
                int points = Integer.parseInt(pointsField.getText());
                String[] columnNames = {"X", "Y"};
                Object[][] data = new Object[points][2];

                JTable table = new JTable(data, columnNames);
                tablePanel.removeAll();
                tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

                JButton plotButton = new JButton("Plot");
                tablePanel.add(plotButton, BorderLayout.SOUTH);

                plotButton.addActionListener(event -> {
                    List<Point> pointList = new ArrayList<>();
                    for (int i = 0; i < points; i++) {
                        try {
                            int x = Integer.parseInt(table.getValueAt(i, 0).toString());
                            int y = Integer.parseInt(table.getValueAt(i, 1).toString());
                            pointList.add(new Point(x, y));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, "Invalid data.");
                            return;
                        }
                    }
                    plotPanel.setPoints(pointList);
                });

                dialog.revalidate();
                dialog.repaint();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number.");
            }
        });

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(tablePanel, BorderLayout.CENTER);
        dialog.add(plotPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private static double calculateMean(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private static double calculateMedian(List<Double> data) {
        int size = data.size();
        if (size % 2 == 0) {
            return (data.get(size / 2 - 1) + data.get(size / 2)) / 2;
        } else {
            return data.get(size / 2);
        }
    }

    private static String calculateMode(List<Double> data) {
        Map<Double, Long> frequency = data.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        long maxCount = frequency.values().stream().mapToLong(Long::longValue).max().orElse(0);
        return frequency.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private static double calculatePercentile(List<Double> data, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * data.size()) - 1;
        return data.get(index);
    }
}

class BoxPlotPanel extends JPanel {
    private List<Double> data = new ArrayList<>();

    public void setData(List<Double> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data.isEmpty()) return;
        Collections.sort(data);
        double q1 = calculatePercentile(data, 25);
        double q3 = calculatePercentile(data, 75);
        double median = calculateMedian(data);
        double min = data.get(0);
        double max = data.get(data.size() - 1);
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int boxHeight = 30;
        int boxWidth = width - 2 * padding;
        int boxX = padding;
        int boxY = height / 2 - boxHeight / 2;
        int minX = (int) ((min - min) / (max - min) * boxWidth);
        int q1X = (int) ((q1 - min) / (max - min) * boxWidth);
        int medianX = (int) ((median - min) / (max - min) * boxWidth);
        int q3X = (int) ((q3 - min) / (max - min) * boxWidth);
        int maxX = (int) ((max - min) / (max - min) * boxWidth);
        g.setColor(Color.BLUE);
        g.fillRect(boxX + q1X, boxY, q3X - q1X, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(boxX + q1X, boxY, q3X - q1X, boxHeight);
        g.setColor(Color.RED);
        g.drawLine(boxX + medianX, boxY, boxX + medianX, boxY + boxHeight);
        g.setColor(Color.BLACK);
        g.drawLine(boxX + minX, boxY + boxHeight / 2, boxX + q1X, boxY + boxHeight / 2);
        g.drawLine(boxX + q3X, boxY + boxHeight / 2, boxX + maxX, boxY + boxHeight / 2);
        g.drawLine(boxX + minX, boxY + boxHeight / 2 - 5, boxX + minX, boxY + boxHeight / 2 + 5);
        g.drawLine(boxX + maxX, boxY + boxHeight / 2 - 5, boxX + maxX, boxY + boxHeight / 2 + 5);
        g.setColor(Color.BLACK);
        g.drawLine(padding, boxY + boxHeight + 20, width - padding, boxY + boxHeight + 20);
        double range = max - min;
        int numTicks = (int) (width / 80);
        double tickSpacing = range / numTicks;

        for (int i = 0; i <= numTicks; i++) {
            int tickX = padding + i * (width - 2 * padding) / numTicks;
            g.drawLine(tickX, boxY + boxHeight + 15, tickX, boxY + boxHeight + 25);
            double tickValue = min + i * tickSpacing;
            g.drawString(String.format("%.2f", tickValue), tickX - 15, boxY + boxHeight + 35);
        }
        g.drawString("IQR: " + (q3 - q1), 10, 20);
    }

    private double calculatePercentile(List<Double> data, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * data.size()) - 1;
        return data.get(index);
    }

    private double calculateMedian(List<Double> data) {
        int size = data.size();
        if (size % 2 == 0) {
            return (data.get(size / 2 - 1) + data.get(size / 2)) / 2;
        } else {
            return data.get(size / 2);
        }
    }
}


class ScatterPlotPanel extends JPanel {
    private List<Point> points = new ArrayList<>();
    private boolean showLineOfBestFit = true;

    public void setPoints(List<Point> points) {
        this.points = points;
        repaint();
    }

    public void setShowLineOfBestFit(boolean showLineOfBestFit) {
        this.showLineOfBestFit = showLineOfBestFit;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (points.isEmpty()) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        g.drawRect(padding, padding, width - 2 * padding, height - 2 * padding);
        int maxX = points.stream().mapToInt(p -> p.x).max().orElse(1);
        int maxY = points.stream().mapToInt(p -> p.y).max().orElse(1);
        int xRange = Math.max(maxX, 10);
        int yRange = Math.max(maxY, 10);
        for (Point p : points) {
            int x = padding + (int) ((double) p.x / xRange * (width - 2 * padding));
            int y = height - padding - (int) ((double) p.y / yRange * (height - 2 * padding));
            g.fillOval(x - 3, y - 3, 6, 6);
        }
        if (showLineOfBestFit && points.size() > 1) {
            double[] line = calculateLineOfBestFit(points);
            double slope = line[0];
            double intercept = line[1];
            int x1 = padding;
            int y1 = height - padding - (int) ((slope * 0 + intercept) / yRange * (height - 2 * padding));
            int x2 = padding + (width - 2 * padding);
            int y2 = height - padding - (int) ((slope * xRange + intercept) / yRange * (height - 2 * padding));
            g.setColor(Color.RED);
            g.drawLine(x1, y1, x2, y2);
        }

        g.setColor(Color.BLACK);
        g.drawLine(padding, height - padding, width - padding, height - padding);
        g.drawLine(padding, padding, padding, height - padding);
        int xTicks = 5;
        for (int i = 0; i <= xTicks; i++) {
            int x = padding + i * (width - 2 * padding) / xTicks;
            int value = i * xRange / xTicks;
            g.drawLine(x, height - padding - 5, x, height - padding + 5);
            g.drawString(String.valueOf(value), x - 10, height - padding + 20);
        }
        int yTicks = 5;
        for (int i = 0; i <= yTicks; i++) {
            int y = height - padding - i * (height - 2 * padding) / yTicks;
            int value = i * yRange / yTicks;
            g.drawLine(padding - 5, y, padding + 5, y);
            g.drawString(String.valueOf(value), padding - 30, y + 5);
        }
    }
    private double[] calculateLineOfBestFit(List<Point> points) {
        int n = points.size();
        double sumX = points.stream().mapToDouble(p -> p.x).sum();
        double sumY = points.stream().mapToDouble(p -> p.y).sum();
        double sumXY = points.stream().mapToDouble(p -> p.x * p.y).sum();
        double sumX2 = points.stream().mapToDouble(p -> p.x * p.x).sum();

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[]{slope, intercept};
    }
}
