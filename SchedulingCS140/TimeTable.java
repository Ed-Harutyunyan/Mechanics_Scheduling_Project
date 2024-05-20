import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {
    private JPanel screen = new JPanel(), tools = new JPanel();
    private JButton tool[], continueButton;
    private JTextField field[];
    private CourseArray courses;
    private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};
    private String fileName;
    private Autoassociator autoassociator;

    public TimeTable(int slots, int iterations, int shifts, String fileName, String filePathWeights) {
        super("Dynamic Time Table");
        setSize(800, 800);
        setLayout(new FlowLayout());

        screen.setPreferredSize(new Dimension(400, 800));
        add(screen);

        setTools(slots, iterations, shifts, fileName);

        courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
        courses.readClashes(field[2].getText());

        add(tools);

        autoassociator = new Autoassociator(courses, filePathWeights);

        setVisible(true);
    }

    public CourseArray getC() {
        return this.courses;
    }

    public void setTools(int slots, int iterations, int shifts, String fileName) {
        String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
        field = new JTextField[capField.length];

        String capButton[] = {"Load", "Start", "Step", "Print", "Exit", "Continue"};
        tool = new JButton[capButton.length];

        tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));

        for (int i = 0; i < field.length; i++) {
            tools.add(new JLabel(capField[i]));
            field[i] = new JTextField(5);
            tools.add(field[i]);
        }

        for (int i = 0; i < tool.length; i++) {
            tool[i] = new JButton(capButton[i]);
            tool[i].addActionListener(this);
            tools.add(tool[i]);
        }

        field[0].setText(Integer.toString(slots));
        field[1].setText("136");
        field[2].setText(fileName);
        field[3].setText(Integer.toString(iterations));
        field[4].setText(Integer.toString(shifts));
    }

    public void draw() {
        Graphics g = screen.getGraphics();
        int width = Integer.parseInt(field[0].getText()) * 10;
        for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
            g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
            g.drawLine(0, courseIndex, width, courseIndex);
            g.setColor(CRScolor[CRScolor.length - 1]);
            g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
        }
    }

    private int getButtonIndex(JButton source) {
        int result = 0;
        while (source != tool[result]) result++;
        return result;
    }

    public void actionPerformed(ActionEvent click) {
        int min = Integer.MAX_VALUE;
        int step = 0;
        int iterationCount = Integer.parseInt(field[3].getText());
        int shiftCount = Integer.parseInt(field[4].getText());
        int maxNoImprovement = 10; // Ensure this is initialized

        switch (getButtonIndex((JButton) click.getSource())) {
            case 0:
                int slots = Integer.parseInt(field[0].getText());
                courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
                courses.readClashes(field[2].getText());
                draw();
                break;
            case 1:
                for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);
                int noImprovementCounter = 0;
                for (int iteration = 1; iteration <= iterationCount; iteration++) {
                    courses.iterate(shiftCount);
                    draw();
                    int clashes = courses.clashesLeft();
                    if (clashes < min) {
                        min = clashes;
                        step = iteration;
                        noImprovementCounter = 0;
                    } else {
                        noImprovementCounter++;
                    }

                    // Check if the algorithm is stuck
                    if (noImprovementCounter >= maxNoImprovement) {
                        System.out.println("Algorithm stuck at iteration " + iteration + ", performing unit update.");
                        int[] slotsStatus = new int[courses.length()];
                        for (int i = 1; i < courses.length(); i++) {
                            slotsStatus[i] = courses.slot(i) == 0 ? -1 : 1;
                        }
                        autoassociator.unitUpdate(slotsStatus);
                        for (int i = 1; i < courses.length(); i++) {
                            courses.setSlot(i, slotsStatus[i] == -1 ? 0 : slotsStatus[i]);
                        }
                        noImprovementCounter = 0; // Reset counter after intervention
                    }
                }
                System.out.println("Iterations = " + iterationCount + " Shift = " + shiftCount + "\tMin clashes = " + min + "\tat step " + step);
                setVisible(true);
                break;
            case 2:
                courses.iterate(shiftCount);
                draw();
                System.out.println(courses.slotStatus(1)[1]);
                break;
            case 3:
                System.out.println("Exam\tSlot\tClashes");
                for (int i = 1; i < courses.length(); i++)
                    System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
                break;
            case 4:
                System.exit(0);
                break;
            case 5:
                min = Integer.MAX_VALUE;
                step = 0;
                noImprovementCounter = 0;
                for (int iteration = 1; iteration <= iterationCount; iteration++) {
                    courses.iterate(shiftCount);
                    draw();
                    int clashes = courses.clashesLeft();
                    if (clashes < min) {
                        min = clashes;
                        step = iteration;
                        noImprovementCounter = 0;
                    } else {
                        noImprovementCounter++;
                    }

                    // Check if the algorithm is stuck
                    if (noImprovementCounter >= maxNoImprovement) {
                        System.out.println("Algorithm stuck at iteration " + iteration + ", performing unit update.");
                        int[] slotsStatus = new int[courses.length()];
                        for (int i = 1; i < courses.length(); i++) {
                            slotsStatus[i] = courses.slot(i) == 0 ? -1 : 1;
                        }
                        autoassociator.unitUpdate(slotsStatus);
                        for (int i = 1; i < courses.length(); i++) {
                            courses.setSlot(i, slotsStatus[i] == -1 ? 0 : slotsStatus[i]);
                        }
                        noImprovementCounter = 0; // Reset counter after intervention
                    }
                }
                System.out.println("Continuing from the current state...");
                System.out.println("Iterations = " + iterationCount + " Shift = " + shiftCount + "\tMin clashes = " + min + "\tat step " + step);
                setVisible(true);
                break;
        }
    }

    public void trainAutoassociator(String csvFilePath) {
        List<int[]> trainingExamples = readTrainingExamplesFromCSV(csvFilePath);
        for (int[] example : trainingExamples) {
            autoassociator.training(example);
        }
        autoassociator.saveWeights();
    }

    private List<int[]> readTrainingExamplesFromCSV(String csvFilePath) {
        List<int[]> trainingExamples = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 3) {
                    int[] example = new int[3];
                    example[0] = Integer.parseInt(parts[0].trim());
                    example[1] = Integer.parseInt(parts[1].trim());
                    example[2] = Integer.parseInt(parts[2].trim());
                    trainingExamples.add(example);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trainingExamples;
    }

    public static void main(String[] args) {

        String filePathWeights_sta = "/Users/edgharutyunyan/Library/CloudStorage/GoogleDrive-edgar_harutyunyan@edu.aua.am/Other computers/My MacBook Pro/AUA/Spring 2024/Mechanics/Mechanics_Project/SchedulingCS140/Weights_sta-f-83";
        String filePathWeights_uta = "/Users/edgharutyunyan/Library/CloudStorage/GoogleDrive-edgar_harutyunyan@edu.aua.am/Other computers/My MacBook Pro/AUA/Spring 2024/Mechanics/Mechanics_Project/SchedulingCS140/Weights_uta-s-92";
        String trainingExamplesFilePath_sta = "/Users/edgharutyunyan/Library/CloudStorage/GoogleDrive-edgar_harutyunyan@edu.aua.am/Other computers/My MacBook Pro/AUA/Spring 2024/Mechanics/Mechanics_Project/SchedulingCS140/sta-f-83_stu_example.csv";
        String trainingExamplesFilePath_uta = "/Users/edgharutyunyan/Library/CloudStorage/GoogleDrive-edgar_harutyunyan@edu.aua.am/Other computers/My MacBook Pro/AUA/Spring 2024/Mechanics/Mechanics_Project/SchedulingCS140/uta-s-92_example.csv";
        Scanner sc = new Scanner(System.in);
        int desiredFile = sc.nextInt();

        if (desiredFile == 1) {
            // 139 courses
            TimeTable tbSTA = new TimeTable(13, 10, 10, "sta-f-83.stu", filePathWeights_sta);
            tbSTA.trainAutoassociator(trainingExamplesFilePath_sta);

        } else {
            // 622 courses but doesn't work
            TimeTable tbUTA = new TimeTable(30, 10, 10, "uta-s-92.stu", filePathWeights_uta);
            tbUTA.trainAutoassociator(trainingExamplesFilePath_uta);

        }
    }

}
