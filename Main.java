import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Desktop;
import java.util.Random;

public class Main {
    private static final String DATA_FOLDER = "timetable_data";
    private static final String DATA_TIMETABLE = "time_table";
    private static final String DATA_FILE = DATA_FOLDER + File.separator + "timetable.dat";
    private static final String COURSES_CSV = DATA_FOLDER + File.separator + "courses.csv";
    private static final String BATCHES_CSV = DATA_FOLDER + File.separator + "batches.csv";
    private static final int MAX_RETRIES = 10;

    public static void main(String[] args) {
        displayWelcomeBanner();
        createRequiredDirectories();
        TimeTable timeTable = loadTimeTable();
        if (timeTable.isEmpty()) {
            timeTable = createTimeTableFromCSV();
        }
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n╔════════════════════════════════╗");
            System.out.println("║   Timetable Management System  ║");
            System.out.println("╠════════════════════════════════╣");
            System.out.println("║ 1. View All Timetables         ║");
            System.out.println("║ 2. View Specific Timetable     ║");
            System.out.println("║ 3. View Free Slots             ║");
            System.out.println("║ 4. Generate Timetable Image    ║");
            System.out.println("║ 5. Generate Timetable CSV      ║");
            System.out.println("║ 6. Exit                        ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    viewAllTimetables(timeTable);
                    break;
                case 2:
                    viewTimetable(scanner, timeTable);
                    break;
                case 3:
                    findFreeSlots(scanner, timeTable);
                    break;
                case 4:
                    generateTimetableImage(scanner, timeTable);
                    break;
                case 5:
                    generateTimetableCSV(scanner, timeTable);
                    break;
                case 6:
                    saveTimeTable(timeTable);
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }


    private static void displayWelcomeBanner() {
        System.out.println("\n\n");
        System.out.println("███████╗██████╗ ██╗   ██╗███████╗ ██████╗██╗  ██╗███████╗██████╗ ██╗   ██╗██╗     ███████╗██████╗ ██╗  ██╗");
        System.out.println("██╔════╝██╔══██╗██║   ██║██╔════╝██╔════╝██║  ██║██╔════╝██╔══██╗██║   ██║██║     ██╔════╝██╔══██╗╚██╗██╔╝");
        System.out.println("█████╗  ██║  ██║██║   ██║███████╗██║     ███████║█████╗  ██║  ██║██║   ██║██║     █████╗  ██████╔╝ ╚███╔╝ ");
        System.out.println("██╔══╝  ██║  ██║██║   ██║╚════██║██║     ██╔══██║██╔══╝  ██║  ██║██║   ██║██║     ██╔══╝  ██╔══██╗ ██╔██╗ ");
        System.out.println("███████╗██████╔╝╚██████╔╝███████║╚██████╗██║  ██║███████╗██████╔╝╚██████╔╝███████╗███████╗██║  ██║██╔╝ ██╗");
        System.out.println("╚══════╝╚═════╝  ╚═════╝ ╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝╚═════╝  ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝");
        System.out.println("\n                    Welcome to EduSchedulerX - Automating Academic Excellence                    ");
        System.out.println("                    A Java-based solution for managing academic timetables                      ");
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════════════════════\n");
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
                }
            }
            System.out.println("Number of batches loaded: " + batchesMap.size());

            // Generate timetable
            String[] days = {"MON", "TUE", "WED", "THU", "FRI"};
            String[] regularSlots = {"09:00-10:30", "10:45-12:15", "14:30-16:00", "16:00-17:30"};
            String[] labSlots = {"14:30-17:30"};
            
            List<String> minorCourses = Arrays.asList("cybersecurity", "genai", "vlsi", "research");
            String minorSlot = "08:00-09:00";

            for (Batch batch : batchesMap.values()) {
                // Schedule minor courses only for 2nd year students
                if (batch.getYear() == 2) {
                    for (String minorCourse : minorCourses) {
                        String day = days[new Random().nextInt(days.length)];
                        String classroom = batch.getLectureRoomIDs().get(new Random().nextInt(batch.getLectureRoomIDs().size()));
                        
                        String[] batchParts = batch.getBatchName().split("-");
                        String branch = batchParts[0];
                        String section = batchParts.length > 1 ? batchParts[1] : "A";
                        
                        Course course = new Course(
                            minorCourse.toUpperCase(),
                            minorCourse.toUpperCase(),
                            "Minor in " + minorCourse,
                            "minor",
                            branch,
                            section,
                            1, 0, 0,
                            "1-0-0-0-1",
                            1,
                            Arrays.asList("F001")
                        );
                        
                        Class newClass = new Class(
                            batch.getBatchName(),
                            classroom,
                            course,
                            new TimeSlot(day, minorSlot.split("-")[0], minorSlot.split("-")[1]),
                            false
                        );

                        timeTable.addClass(newClass, batch.getBatchName());
                    }
                }

                // Schedule regular courses
                for (String courseId : batch.getCourseIds()) {
                    Course course = coursesMap.get(courseId);
                    if (course != null) {
                        String[] batchParts = batch.getBatchName().split("-");
                        String branch = batchParts[0];
                        String section = batchParts.length > 1 ? batchParts[1] : "A";
                        course = new Course(
                            course.getId(), course.getCourseCode(), course.getName(),
                            course.getCourseType(), branch, section,
                            course.getLecture(), course.getTheory(), course.getPractical(),
                            course.getCredits(), course.getHoursPerWeek(),
                            course.getEligibleFacultyIds()
                        );

                        int totalHours = course.getLecture() + course.getTheory() + course.getPractical();
                        for (int i = 0; i < totalHours; i++) {
                            boolean added = false;
                            int retries = 0;
                            while (!added && retries < MAX_RETRIES) {
                                String day = days[new Random().nextInt(days.length)];
                                String timeSlot;
                                String classroom;

                                if (course.getCourseType().equalsIgnoreCase("practical")) {
                                    timeSlot = labSlots[new Random().nextInt(labSlots.length)];
                                    classroom = batch.getPracticalRoomIDs().get(new Random().nextInt(batch.getPracticalRoomIDs().size()));
                                } else {
                                    timeSlot = regularSlots[new Random().nextInt(regularSlots.length)];
                                    classroom = batch.getLectureRoomIDs().get(new Random().nextInt(batch.getLectureRoomIDs().size()));
                                }

                                String[] times = timeSlot.split("-");

                                Class newClass = new Class(
                                    batch.getBatchName(),
                                    classroom,
                                    course,
                                    new TimeSlot(day, times[0], times[1]),
                                    course.getCourseType().equalsIgnoreCase("practical")
                                );

                                if (timeTable.addClass(newClass, course.getBranch())) {
                                    added = true;
                                } else {
                                    retries++;
                                }
                            }
                        }
                    }
                }
            }
            timeTable.scheduleLunch();
            return timeTable;
        } catch (IOException e) {
            System.out.println("Error reading CSV files: " + e.getMessage());
            e.printStackTrace();
            return new TimeTable();
        }
    }


     private static void viewAllTimetables(TimeTable timeTable) {
        System.out.println("\n=== All Timetables ===");
        List<String> batches = getAllBatches();
        for (String batch : batches) {
            System.out.println("\nTimetable for " + batch + ":");
            timeTable.displayTimetable(batch);
            System.out.println("\nPress Enter to continue to the next timetable...");
            new Scanner(System.in).nextLine();
        }
    }

    private static List<String> getAllBatches() {
        List<String> batches = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(BATCHES_CSV));
            // Skip the header
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length > 1) {
                    batches.add(parts[1]); // Assuming batchName is the second column
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading batches from CSV: " + e.getMessage());
        }
        return batches;
    }

    private static void viewTimetable(Scanner scanner, TimeTable timeTable) {
        System.out.print("Enter batch name to view: ");
        String batchName = scanner.nextLine();
        timeTable.displayTimetable(batchName);
    }

    private static void generateTimetableCSV(Scanner scanner, TimeTable timeTable) {
        System.out.print("Enter batch name to generate CSV for: ");
        String batchName = scanner.nextLine();
        String outputPath = DATA_TIMETABLE + File.separator + "timetable_" + batchName + ".csv";
        
        try {
            timeTable.generateCSV(batchName, outputPath);
            System.out.println("CSV file generated successfully: " + outputPath);
            
            System.out.print("Do you want to open the generated CSV file? (Y/N): ");
            String openFile = scanner.nextLine().toUpperCase();
            if (openFile.equals("Y")) {
                File csvFile = new File(outputPath);
                if (csvFile.exists()) {
                    Desktop.getDesktop().open(csvFile);
                } else {
                    System.out.println("Error: The generated CSV file does not exist.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error generating or opening CSV file: " + e.getMessage());
        }
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

    private static void createRequiredDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
            Files.createDirectories(Paths.get(DATA_TIMETABLE));
        } catch (IOException e) {
            System.out.println("Error creating directories: " + e.getMessage());
        }
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
        String outputPath = DATA_TIMETABLE + File.separator + "timetable_" + batchName + ".png";
        
        try {
            timeTable.generateTimetableImage(batchName, outputPath);
            System.out.println("Image file generated successfully: " + outputPath);
            
            System.out.print("Do you want to open the generated image? (Y/N): ");
            String openImage = scanner.nextLine().toUpperCase();
            if (openImage.equals("Y")) {
                File imageFile = new File(outputPath);
                if (imageFile.exists()) {
                    Desktop.getDesktop().open(imageFile);
                } else {
                    System.out.println("Error: The generated image file does not exist.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error generating or opening image file: " + e.getMessage());
        }
    }
}

