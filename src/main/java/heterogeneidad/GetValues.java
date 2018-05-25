package heterogeneidad;



import java.awt.*;
import java.util.*;
import javax.swing.*;

public class GetValues {
	private double[] marks;
	private JTextField[] marksField;
	private JLabel resultLabel;

	public GetValues() {
		marks = new double[3];
		marksField = new JTextField[3];
		marksField[0] = new JTextField(5);
		marksField[1] = new JTextField(5);
		marksField[2] = new JTextField(5);
	}

	public void displayGUI() {
		int selection = JOptionPane.showConfirmDialog(null, getPanel(),
				"Input Form : ", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (selection == JOptionPane.OK_OPTION) {
			for (int i = 0; i < 3; i++) {
				marks[i] = Double.valueOf(marksField[i].getText());
			}
			Arrays.sort(marks);
			double average = (marks[1] + marks[2]) / 2.0;
			JOptionPane.showMessageDialog(null,
					"Average is : " + Double.toString(average), "Average : ",
					JOptionPane.PLAIN_MESSAGE);
		} else if (selection == JOptionPane.CANCEL_OPTION) {
			// Do something here.
		}
	}

	private JPanel getPanel() {
		JPanel basePanel = new JPanel();
		// basePanel.setLayout(new BorderLayout(5, 5));
		basePanel.setOpaque(true);
		basePanel.setBackground(Color.RED.darker());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(3, 2, 5, 5));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		centerPanel.setOpaque(true);
		centerPanel.setBackground(Color.WHITE);

		JLabel mLabel1 = new JLabel("Marks 1 : ");
		JLabel mLabel2 = new JLabel("Marks 2 : ");
		JLabel mLabel3 = new JLabel("Marks 3 : ");

		centerPanel.add(mLabel1);
		centerPanel.add(marksField[0]);
		centerPanel.add(mLabel2);
		centerPanel.add(marksField[1]);
		centerPanel.add(mLabel3);
		centerPanel.add(marksField[2]);

		basePanel.add(centerPanel);

		return basePanel;
	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GetValues().displayGUI();
			}
		});
	}
}
