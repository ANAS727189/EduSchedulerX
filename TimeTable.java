import java.util.*;
import java.io.Serializable;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class TimeTable implements Serializable {
    private Map<String, List<Class>> classes;
    private Map<String, List<TimeSlot>> teacherSchedule;
      private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI"};
    private static final String[] ALL_TIME_SLOTS = {"08:00-09:00", "09:00-10:30", "10:45-12:15", "14:30-16:00", "16:00-17:30"};
    private static final String[] REGULAR_TIME_SLOTS = {"09:00-10:30", "10:45-12:15", "14:30-16:00", "16:00-17:30"};
    private static final String[] LAB_SLOTS = {"14:30-16:30"};


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
        String facultyId = newClass.getCourse().getEligibleFacultyIds().get(0);
        
        // Check if the same subject is already scheduled for this day
        if (isSubjectScheduledForDay(batchName, newClass.getCourse().getCourseCode(), newClass.getTimeSlot().getDay())) {
            return false;
        }
        
        if (isSlotFree(newClass.getTimeSlot(), batchName, facultyId)) {
            classes.computeIfAbsent(batchName, k -> new ArrayList<>()).add(newClass);
            teacherSchedule.computeIfAbsent(facultyId, k -> new ArrayList<>()).add(newClass.getTimeSlot());
            return true;
        }
        return false;
    }

    private boolean isSubjectScheduledForDay(String batchName, String courseCode, String day) {
        if (classes.containsKey(batchName)) {
            for (Class cls : classes.get(batchName)) {
                if (cls.getTimeSlot().getDay().equals(day) && cls.getCourse().getCourseCode().equals(courseCode)) {
                    return true;
                }
            }
        }
        return false;
    }

  
       public void displayTimetable(String batchName) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                       Timetable for " + batchName + "                                                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║   Time   │   Monday   │   Tuesday  │  Wednesday │  Thursday  │   Friday   ║");
        System.out.println("╠══════════╪════════════╪════════════╪════════════╪════════════╪════════════╣");

        boolean hasMinorCourses = classes.get(batchName).stream().anyMatch(cls -> cls.getCourse().getCourseType().equalsIgnoreCase("minor"));
        String[] timeSlots = hasMinorCourses ? ALL_TIME_SLOTS : REGULAR_TIME_SLOTS;

        for (String timeSlot : timeSlots) {
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

    // Table header
    System.out.printf("%-10s | %-30s | %-10s | %-10s | %-15s | %-15s | %-15s\n",
                      "Code", "Name", "Type", "Credits", "Branch/Section", "Hours", "Faculty IDs");
    System.out.println("─".repeat(120));

    // Use a Set to track unique courses by course code
    Set<String> displayedCourses = new HashSet<>();
    
    // Table content with duplicate prevention
    for (Class cls : classes.getOrDefault(batchName, Collections.emptyList())) {
        Course course = cls.getCourse();
        // Only display the course if we haven't seen it before
        if (!displayedCourses.contains(course.getCourseCode()) && !course.getCourseCode().equals("LUNCH")) {
            displayedCourses.add(course.getCourseCode());
            System.out.printf("%-10s | %-30s | %-10s | %-10s | %-15s | L-%d T-%d P-%d | %-15s\n",
                              course.getCourseCode(),
                              course.getName(),
                              course.getCourseType(),
                              course.getCredits(),
                              course.getBranch() + "/" + course.getSection(),
                              course.getLecture(), course.getTheory(), course.getPractical(),
                              String.join(", ", course.getEligibleFacultyIds()));
        }
    }

    // Minor courses section
    System.out.println("\nMinor Courses:");
        System.out.println("═══════════════");
        boolean minorCoursesFound = false;
        Set<String> displayedMinorCourses = new HashSet<>();
        
        for (Class cls : classes.getOrDefault(batchName, Collections.emptyList())) {
            Course course = cls.getCourse();
            if (course.getCourseType().equalsIgnoreCase("minor") && 
                !displayedMinorCourses.contains(course.getCourseCode())) {
                displayedMinorCourses.add(course.getCourseCode());
                System.out.printf("%-10s | %-30s | %-20s\n",
                                  course.getCourseCode(),
                                  course.getName(),
                                  cls.getTimeSlot());
                minorCoursesFound = true;
            }
        }
        if (!minorCoursesFound) {
            System.out.println("No minor courses found for this batch.");
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

        public void generateCSV(String batchName, String outputPath) {
        try (FileWriter csvWriter = new FileWriter(outputPath)) {
            // Write header
            csvWriter.append("Day,Time,Course Code,Course Name,Room,Faculty\n");

            // Create a sorted map of classes by day and time
            Map<String, Map<String, Class>> sortedClasses = new TreeMap<>();
            for (Class cls : classes.getOrDefault(batchName, Collections.emptyList())) {
                sortedClasses
                    .computeIfAbsent(cls.getTimeSlot().getDay(), k -> new TreeMap<>())
                    .put(cls.getTimeSlot().getStartTime(), cls);
            }

            boolean hasMinorCourses = classes.get(batchName).stream().anyMatch(cls -> cls.getCourse().getCourseType().equalsIgnoreCase("minor"));
            String[] timeSlots = hasMinorCourses ? ALL_TIME_SLOTS : REGULAR_TIME_SLOTS;

            // Write data
            for (String day : DAYS) {
                Map<String, Class> dayClasses = sortedClasses.getOrDefault(day, Collections.emptyMap());
                for (String timeSlot : timeSlots) {
                    String startTime = timeSlot.split("-")[0];
                    Class cls = dayClasses.get(startTime);
                    if (cls != null) {
                        csvWriter.append(String.format("%s,%s,%s,%s,%s,%s\n",
                            day,
                            timeSlot,
                            cls.getCourse().getCourseCode(),
                            cls.getCourse().getName(),
                            cls.getClassroom(),
                            String.join(";", cls.getCourse().getEligibleFacultyIds())
                        ));
                    } else {
                        csvWriter.append(String.format("%s,%s,,,,\n", day, timeSlot));
                    }
                }
            }

            System.out.println("CSV file has been generated successfully: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error generating CSV file: " + e.getMessage());
        }
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

