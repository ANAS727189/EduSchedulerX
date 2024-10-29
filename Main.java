import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Desktop;
import java.util.Random;

public class Main {
    private static final String DATA_FOLDER = "timetable_data";
    private static final String DATA_FILE = DATA_FOLDER + File.separator + "timetable.dat";
    private static final String COURSES_CSV = DATA_FOLDER + File.separator + "courses.csv";
    private static final String BATCHES_CSV = DATA_FOLDER + File.separator + "batches.csv";
    private static final int MAX_RETRIES = 10;

    public static void main(String[] args) {
        TimeTable timeTable = loadTimeTable();
        if (timeTable.isEmpty()) {
            timeTable = createTimeTableFromCSV();
        }
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n╔════════════════════════════════╗");
            System.out.println("║   Timetable Management System  ║");
            System.out.println("╠════════════════════════════════╣");
            System.out.println("║ 1. View Timetable              ║");
            System.out.println("║ 2. View Free Slots             ║");
            System.out.println("║ 3. Generate Timetable Image    ║");
            System.out.println("║ 4. Exit                        ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    viewTimetable(scanner, timeTable);
                    break;
                case 2:
                    findFreeSlots(scanner, timeTable);
                    break;
                case 3:
                    generateTimetableImage(scanner, timeTable);
                    break;
                case 4:
                    saveTimeTable(timeTable);
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static TimeTable createTimeTableFromCSV() {
        TimeTable timeTable = new TimeTable();
        Map<String, Course> coursesMap = new HashMap<>();
        Map<String, Batch> batchesMap = new HashMap<>();

        try {
            // Read courses
            List<String> courseLines = Files.readAllLines(Paths.get(COURSES_CSV));
            System.out.println("Number of course lines: " + courseLines.size());
            if (!courseLines.isEmpty()) {
                courseLines.remove(0); // Remove header
            }
            for (String line : courseLines) {
                String[] parts = line.split(",");
                if (parts.length >= 12) {
                    try {
                        Course course = new Course(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                                                   Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                                                   Integer.parseInt(parts[8]), parts[9], Integer.parseInt(parts[10]),
                                                   Arrays.asList(parts[11].split(";")));
                        coursesMap.put(course.getId(), course);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing course line: " + line);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Invalid course line (not enough fields): " + line);
                }
            }
            System.out.println("Number of courses loaded: " + coursesMap.size());

            // Read batches
            List<String> batchLines = Files.readAllLines(Paths.get(BATCHES_CSV));
            System.out.println("Number of batch lines: " + batchLines.size());
            if (!batchLines.isEmpty()) {
                batchLines.remove(0); // Remove header
            }
            for (String line : batchLines) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    try {
                        Batch batch = new Batch(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
                                                Arrays.asList(parts[4].split(";")),
                                                Arrays.asList(parts[5].split(";")),
                                                Arrays.asList(parts[6].split(";")));
                        batchesMap.put(batch.getId(), batch);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing batch line: " + line);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Invalid batch line (not enough fields): " + line);
                }
            }
            System.out.println("Number of batches loaded: " + batchesMap.size());

            // Generate timetable
            String[] days = {"MON", "TUE", "WED", "THU", "FRI"};
            String[] timeSlots = {"09:00-10:30", "10:45-12:15", "14:30-16:00", "16:00-17:30"};

            for (Batch batch : batchesMap.values()) {
                for (String courseId : batch.getCourseIds()) {
                    Course course = coursesMap.get(courseId);
                    if (course != null) {
                        int totalHours = course.getLecture() + course.getTheory() + course.getPractical();
                        for (int i = 0; i < totalHours; i++) {
                            boolean added = false;
                            int retries = 0;
                            while (!added && retries < MAX_RETRIES) {
                                String day = days[new Random().nextInt(days.length)];
                                String timeSlot = timeSlots[new Random().nextInt(timeSlots.length)];
                                String[] times = timeSlot.split("-");
                                String classroom = course.getCourseType().equalsIgnoreCase("practical") ?
                                                   batch.getPracticalRoomIDs().get(new Random().nextInt(batch.getPracticalRoomIDs().size())) :
                                                   batch.getLectureRoomIDs().get(new Random().nextInt(batch.getLectureRoomIDs().size()));

                                Class newClass = new Class(
                                    batch.getBatchName(),
                                    classroom,
                                    course,
                                    new TimeSlot(day, times[0], times[1]),
                                    course.getCourseType().equalsIgnoreCase("practical")
                                );

                                if (timeTable.addClass(newClass, course.getBranch())) {
                                    // System.out.println("Added " + course.getCourseCode() + " to " + day + " " + timeSlot + " for " + batch.getBatchName());
                                    added = true;
                                } else {
                                    retries++;
                                }
                            }
                            // if (!added) {
                            //     System.out.println("Unable to add " + course.getCourseCode() + " for " + batch.getBatchName() + " after " + MAX_RETRIES + " attempts.");
                            // }
                        }
                    }
                }
            }
            timeTable.scheduleLunch();
        } catch (IOException e) {
            // System.out.println("Error reading CSV files: " + e.getMessage());
            e.printStackTrace();
        }
        return timeTable;
    }

    private static void viewTimetable(Scanner scanner, TimeTable timeTable) {
        System.out.print("Enter batch name to view: ");
        String batchName = scanner.nextLine();
        timeTable.displayTimetable(batchName);
    }

    private static void findFreeSlots(Scanner scanner, TimeTable timeTable) {
        System.out.print("Enter batch name: ");
        String batchName = scanner.nextLine();
        System.out.print("Enter day (MON/TUE/WED/THU/FRI): ");
        String day = scanner.nextLine().toUpperCase();
        List<TimeSlot> freeSlots = timeTable.findFreeSlots(batchName, day);
        System.out.println("Free slots for " + day + ":");
        for (TimeSlot slot : freeSlots) {
            System.out.println(slot);
        }
    }

    private static TimeTable loadTimeTable() {
        File dataFile = new File(DATA_FILE);
        if (dataFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                return (TimeTable) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading timetable data. Starting with a new timetable.");
            }
        }
        return new TimeTable();
    }

    private static void saveTimeTable(TimeTable timeTable) {
        File dataFolder = new File(DATA_FOLDER);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(timeTable);
            System.out.println("Timetable data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving timetable data: " + e.getMessage());
        }
    }

    private static class Batch {
        private String id;
        private String batchName;
        private int year;
        private int strength;
        private List<String> courseIds;
        private List<String> lectureRoomIDs;
        private List<String> practicalRoomIDs;

        public Batch(String id, String batchName, int year, int strength, 
                     List<String> courseIds, List<String> lectureRoomIDs, List<String> practicalRoomIDs) {
            this.id = id;
            this.batchName = batchName;
            this.year = year;
            this.strength = strength;
            this.courseIds = courseIds;
            this.lectureRoomIDs = lectureRoomIDs;
            this.practicalRoomIDs = practicalRoomIDs;
        }
        // Getters
        public String getId() { return id; }
        public String getBatchName() { return batchName; }
        public int getYear() { return year; }
        public int getStrength() { return strength; }
        public List<String> getCourseIds() { return courseIds; }
        public List<String> getLectureRoomIDs() { return lectureRoomIDs; }
        public List<String> getPracticalRoomIDs() { return practicalRoomIDs; }
    }

    private static void generateTimetableImage(Scanner scanner, TimeTable timeTable) {
        System.out.print("Enter batch name to generate image for: ");
        String batchName = scanner.nextLine();
        String outputPath = DATA_FOLDER + File.separator + "timetable_" + batchName + ".png";
        timeTable.generateTimetableImage(batchName, outputPath);
        
        System.out.print("Do you want to open the generated image? (Y/N): ");
        String openImage = scanner.nextLine().toUpperCase();
        if (openImage.equals("Y")) {
            try {
                File imageFile = new File(outputPath);
                Desktop.getDesktop().open(imageFile);
            } catch (IOException e) {
                System.out.println("Error opening the image: " + e.getMessage());
            }
        }
    }
}