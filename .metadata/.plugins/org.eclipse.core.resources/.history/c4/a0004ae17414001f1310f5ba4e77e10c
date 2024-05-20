import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Autoassociator {
    private int weights[][];
    private int trainingCapacity;
    private String filePath;

    public Autoassociator(CourseArray courses) {
    	this.filePath = "/Users/edgharutyunyan/Library/CloudStorage/GoogleDrive-edgar_harutyunyan@edu.aua.am/Other computers/My MacBook Pro/AUA/Spring 2024/Mechanics/Mechanics_Project/SchedulingCS140/Weights";
        int numNeurons = courses.length();
        weights = new int[numNeurons][numNeurons];
        initializeWeights();
        trainingCapacity = numNeurons;
    }

    public int getTrainingCapacity() {
        return trainingCapacity;
    }

    public void training(int pattern[]) {
        System.out.println("Training example: " + Arrays.toString(pattern));
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                weights[i][j] += pattern[i] * pattern[j];
            }
        }
    }

    public int unitUpdate(int neurons[]) {
        Random random = new Random();
        int index = random.nextInt(neurons.length);
        int netInput = 0;

        for (int i = 0; i < neurons.length; i++) {
            netInput += weights[index][i] * neurons[i];
        }

        if (netInput >= 0) {
            neurons[index] = 1;
        } else {
            neurons[index] = -1;
        }

        return index;
    }

    public void unitUpdate(int neurons[], int index) {
        int netInput = 0;

        for (int i = 0; i < neurons.length; i++) {
            netInput += weights[index][i] * neurons[i];
        }

        if (netInput >= 0) {
            neurons[index] = 1;
        } else {
            neurons[index] = -1;
        }
    }

    public void chainUpdate(int neurons[], int steps) {
        for (int i = 0; i < steps; i++) {
            unitUpdate(neurons);
        }
    }

    public void fullUpdate(int neurons[]) {
        int[] oldNeurons = new int[neurons.length];
        System.arraycopy(neurons, 0, oldNeurons, 0, neurons.length);

        boolean updated = true;
        while (updated) {
            updated = false;
            for (int i = 0; i < neurons.length; i++) {
                int netInput = 0;
                for (int j = 0; j < neurons.length; j++) {
                    netInput += weights[i][j] * oldNeurons[j];
                }

                if (netInput >= 0) {
                    neurons[i] = 1;
                } else {
                    neurons[i] = -1;
                }

                if (neurons[i] != oldNeurons[i]) {
                    updated = true;
                }
            }
            System.arraycopy(neurons, 0, oldNeurons, 0, neurons.length);
        }
    }

    private void initializeWeights() {
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = (random.nextBoolean() ? 1 : -1);
            }
        }
    }
    
    public void saveWeights() {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    writer.write(weights[i][j] + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
