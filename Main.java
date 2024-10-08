import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Input scores
        System.out.println("Enter quiz 1 score (out of 10): ");
        double quiz1 = input.nextDouble();

        System.out.println("Enter quiz 2 score (out of 10): ");
        double quiz2 = input.nextDouble();

        System.out.println("Enter quiz 3 score (out of 10): ");
        double quiz3 = input.nextDouble();

        System.out.println("Enter midterm exam score (out of 100): ");
        double midterm = input.nextDouble();

        System.out.println("Enter final exam score (out of 100): ");
        double finalExam = input.nextDouble();

        // Create a StudentRecord object
        StudentRecord s1 = new StudentRecord(quiz1, quiz2, quiz3, midterm, finalExam);

        // Output the results
        System.out.println("\nStudent's Record:");
        System.out.println("Quiz 1: " + s1.getQuiz1());
        System.out.println("Quiz 2: " + s1.getQuiz2());
        System.out.println("Quiz 3: " + s1.getQuiz3());
        System.out.println("Midterm: " + s1.getMidterm());
        System.out.println("Final Exam: " + s1.getFinalExam());
        System.out.println("Overall Numeric Score: " + s1.getFinalScore());
        System.out.println("Final Letter Grade: " + s1.getKeterangan());
    }
}
