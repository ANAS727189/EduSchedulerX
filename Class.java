import java.io.Serializable;

public class Class implements Serializable {
    private String batchName;
    private String classroom;
    private Course course;
    private TimeSlot timeSlot;
    private boolean isLab;

    public Class(String batchName, String classroom, Course course, TimeSlot timeSlot, boolean isLab) {
        this.batchName = batchName;
        this.classroom = classroom;
        this.course = course;
        this.timeSlot = timeSlot;
        this.isLab = isLab;
    }

    // Getters
    public String getBatchName() { return batchName; }
    public String getClassroom() { return classroom; }
    public Course getCourse() { return course; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public boolean isLab() { return isLab; }
}

