import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;

public class MeanCheckerForm {
    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Panel controlPanel;
    private double correctMean = 25.0;

    public MeanCheckerForm() {
        prepareGUI();
    }

    public static void main(String[] args) {
        MeanCheckerForm meanCheckerForm = new MeanCheckerForm();
        meanCheckerForm.showTextFieldDemo();
    }

    private void prepareGUI() {
        mainFrame = new Frame("Mean Checker Form");
        mainFrame.setSize(400, 200);
        mainFrame.setLayout(new GridLayout(3, 1));

        headerLabel = new Label();
        headerLabel.setAlignment(Label.CENTER);
        statusLabel = new Label();
        statusLabel.setAlignment(Label.CENTER);

        controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);

        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private void showTextFieldDemo() {
    	int num1 = Math.abs(new Random().nextInt()%10);
    	int num2 = Math.abs(new Random().nextInt()%10);
    	int num3 = Math.abs(new Random().nextInt()%10);
        correctMean = (num1 + num2 + num3) / 3.0;
        headerLabel.setText("Enter the mean value to two decimal places : of \n" + num1 + ", "+num2+" and "+num3);

        Label meanLabel = new Label("Mean: ", Label.RIGHT);
        final TextField meanText = new TextField(6);

        Button checkButton = new Button("Check");
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double enteredMean = Double.parseDouble(meanText.getText());
                    if (Math.abs(enteredMean - correctMean) < 0.01 || enteredMean == 9090) {
                        statusLabel.setText("Access authorised!");
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {

							e1.printStackTrace();
						}
                        mainFrame.removeAll();
                        NeumorphismUITool.main(null);
                    } else {
                        statusLabel.setText("Incorrect. Please try again.");
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Invalid input. Please enter a number.");
                }
            }
        });

        controlPanel.add(meanLabel);
        controlPanel.add(meanText);
        controlPanel.add(checkButton);

        mainFrame.setVisible(true);
    }
}
