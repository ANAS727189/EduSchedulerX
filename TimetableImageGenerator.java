import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TimetableImageGenerator {
    private static final int CELL_WIDTH = 150;
    private static final int CELL_HEIGHT = 50;
    private static final int MARGIN = 20;
    private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI"};
    private static final String[] TIME_SLOTS = {"09:00-10:30", "10:45-12:15", "12:15-13:15", "14:30-16:00", "16:00-17:30"};

    public static void generateTimetableImage(List<Class> classes, String batchName, String outputPath) {
        int width = CELL_WIDTH * 6 + MARGIN * 2;
        int height = CELL_HEIGHT * (TIME_SLOTS.length + 1) + MARGIN * 2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw grid
        g2d.setColor(Color.BLACK);
        for (int i = 0; i <= 6; i++) {
            g2d.drawLine(MARGIN + i * CELL_WIDTH, MARGIN, MARGIN + i * CELL_WIDTH, height - MARGIN);
        }
        for (int i = 0; i <= TIME_SLOTS.length + 1; i++) {
            g2d.drawLine(MARGIN, MARGIN + i * CELL_HEIGHT, width - MARGIN, MARGIN + i * CELL_HEIGHT);
        }

        // Draw headers
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Time", MARGIN + 10, MARGIN + CELL_HEIGHT - 10);
        for (int i = 0; i < DAYS.length; i++) {
            g2d.drawString(DAYS[i], MARGIN + (i + 1) * CELL_WIDTH + 10, MARGIN + CELL_HEIGHT - 10);
        }

        // Draw time slots
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            g2d.drawString(TIME_SLOTS[i], MARGIN + 10, MARGIN + (i + 2) * CELL_HEIGHT - 10);
        }

        // Draw classes
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        for (Class cls : classes) {
            int dayIndex = getDayIndex(cls.getTimeSlot().getDay());
            int timeIndex = getTimeIndex(cls.getTimeSlot().getStartTime());
            if (dayIndex != -1 && timeIndex != -1) {
                g2d.setColor(new Color(230, 230, 250));
                g2d.fillRect(MARGIN + (dayIndex + 1) * CELL_WIDTH + 1, MARGIN + (timeIndex + 1) * CELL_HEIGHT + 1, CELL_WIDTH - 1, CELL_HEIGHT - 1);
                g2d.setColor(Color.BLACK);
                g2d.drawString(cls.getCourse().getCourseCode(), MARGIN + (dayIndex + 1) * CELL_WIDTH + 5, MARGIN + (timeIndex + 2) * CELL_HEIGHT - 15);
                g2d.drawString(cls.getClassroom(), MARGIN + (dayIndex + 1) * CELL_WIDTH + 5, MARGIN + (timeIndex + 2) * CELL_HEIGHT - 5);
            }
        }

        g2d.dispose();

        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("Timetable image generated successfully: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error generating timetable image: " + e.getMessage());
        }
    }

    private static int getDayIndex(String day) {
        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equals(day)) {
                return i;
            }
        }
        return -1;
    }

    private static int getTimeIndex(String startTime) {
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            if (TIME_SLOTS[i].startsWith(startTime)) {
                return i;
            }
        }
        return -1;
    }
}

