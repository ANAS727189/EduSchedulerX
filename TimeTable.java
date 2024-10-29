import java.util.*;
import java.io.Serializable;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TimeTable implements Serializable {
    private Map<String, List<Class>> classes;
    private Map<String, List<TimeSlot>> teacherSchedule;
    private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI"};
    private static final String[] TIME_SLOTS = {"09:00-10:30", "10:45-12:15", "14:30-16:00", "16:00-17:30"};

    public TimeTable() {
        classes = new HashMap<>();
        teacherSchedule = new HashMap<>();
    }
    
    public boolean isEmpty() {
        return classes.isEmpty();
    }

    public void generateTimetableImage(String batchName, String outputPath) {
        TimetableImageGenerator.generateTimetableImage(classes.get(batchName), batchName, outputPath);
    }

    public boolean isSlotFree(TimeSlot newSlot, String batchName, String facultyId) {
        if (classes.containsKey(batchName)) {
            for (Class cls : classes.get(batchName)) {
                if (cls.getTimeSlot().getDay().equals(newSlot.getDay()) &&
                    isTimeOverlap(cls.getTimeSlot(), newSlot)) {
                    return false;
                }
            }
        }
        
        if (teacherSchedule.containsKey(facultyId)) {
            for (TimeSlot existingSlot : teacherSchedule.get(facultyId)) {
                if (existingSlot.getDay().equals(newSlot.getDay()) &&
                    isTimeOverlap(existingSlot, newSlot)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private boolean isTimeOverlap(TimeSlot slot1, TimeSlot slot2) {
        int start1 = timeToMinutes(slot1.getStartTime());
        int end1 = timeToMinutes(slot1.getEndTime());
        int start2 = timeToMinutes(slot2.getStartTime());
        int end2 = timeToMinutes(slot2.getEndTime());

        return (start1 < end2 && start2 < end1);
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public boolean addClass(Class newClass, String branch) {
        String batchName = newClass.getBatchName();
        if (isSlotFree(newClass.getTimeSlot(), batchName, newClass.getCourse().getEligibleFacultyIds().get(0))) {
            classes.computeIfAbsent(batchName, k -> new ArrayList<>()).add(newClass);
            
            for (String facultyId : newClass.getCourse().getEligibleFacultyIds()) {
                teacherSchedule.computeIfAbsent(facultyId, k -> new ArrayList<>())
                              .add(newClass.getTimeSlot());
            }
            return true;
        }
        return false;
    }

    public void displayTimetable(String batchName) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                       Timetable for " + batchName + "                                                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║   Time   │   Monday   │   Tuesday  │  Wednesday │  Thursday  │   Friday   ║");
        System.out.println("╠══════════╪════════════╪════════════╪════════════╪════════════╪════════════╣");

        for (String timeSlot : TIME_SLOTS) {
            System.out.printf("║ %8s │", timeSlot);
            for (String day : DAYS) {
                boolean slotFilled = false;
                for (Class cls : classes.getOrDefault(batchName, Collections.emptyList())) {
                    if (cls.getTimeSlot().getDay().equals(day) &&
                        isTimeOverlap(cls.getTimeSlot(), new TimeSlot(day, timeSlot.split("-")[0], timeSlot.split("-")[1]))) {
                        System.out.printf(" %-10s │", cls.getCourse().getCourseCode());
                        slotFilled = true;
                        break;
                    }
                }
                if (!slotFilled) {
                    System.out.print("           │");
                }
            }
            System.out.println();
        }

        System.out.println("╚══════════╧════════════╧════════════╧════════════╧════════════╧════════════╝");
        System.out.println("\nCourse Details:");
        System.out.println("═══════════════");

        for (Class cls : classes.getOrDefault(batchName, Collections.emptyList())) {
            Course course = cls.getCourse();
            System.out.printf("%-10s: %s (%s)\n", course.getCourseCode(), course.getName(), course.getCredits());
            System.out.printf("           Type: %s, Branch: %s, Section: %s\n", course.getCourseType(), course.getBranch(), course.getSection());
            System.out.printf("           Hours: L-%d T-%d P-%d\n", course.getLecture(), course.getTheory(), course.getPractical());
            System.out.printf("           Eligible Faculty IDs: %s\n", String.join(", ", course.getEligibleFacultyIds()));
            System.out.println();
        }
    }

    public List<TimeSlot> findFreeSlots(String batchName, String day) {
        List<TimeSlot> freeSlots = new ArrayList<>();
        List<TimeSlot> occupiedSlots = new ArrayList<>();

        for (Class cls : classes.getOrDefault(batchName, Collections.emptyList())) {
            if (cls.getTimeSlot().getDay().equals(day)) {
                occupiedSlots.add(cls.getTimeSlot());
            }
        }

        occupiedSlots.sort(Comparator.comparing(TimeSlot::getStartTime));

        String[] standardTimes = {"09:00", "10:45", "12:15", "14:30", "16:00", "17:30"};
        
        for (int i = 0; i < standardTimes.length - 1; i++) {
            TimeSlot potentialSlot = new TimeSlot(day, standardTimes[i], standardTimes[i+1]);
            boolean isFree = true;

            for (TimeSlot occupiedSlot : occupiedSlots) {
                if (isTimeOverlap(potentialSlot, occupiedSlot)) {
                    isFree = false;
                    break;
                }
            }

            if (isFree) {
                freeSlots.add(potentialSlot);
            }
        }

        return freeSlots;
    }

    public void scheduleLunch() {
        Random random = new Random();
        for (String batchName : classes.keySet()) {
            String day = DAYS[random.nextInt(DAYS.length)];
            int lunchHour = 12 + random.nextInt(2);
            int lunchMinute = 30 + random.nextInt(2) * 30;
            String lunchStart = "12:30";
            String lunchEnd = String.format("%02d:%02d", lunchHour, lunchMinute);
            TimeSlot lunchSlot = new TimeSlot(day, lunchStart, lunchEnd);
            Course lunchCourse = new Course("LUNCH", "LUNCH", "Lunch Break", "BREAK", "", "", 0, 0, 0, "", 0, new ArrayList<>());
            Class lunchClass = new Class(batchName, "CANTEEN", lunchCourse, lunchSlot, false);
            classes.get(batchName).add(lunchClass);
        }
    }
}

