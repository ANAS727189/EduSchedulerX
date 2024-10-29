import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {
    private String id;
    private String courseCode;
    private String name;
    private String courseType;
    private String branch;
    private String section;
    private int lecture;
    private int theory;
    private int practical;
    private String credits;
    private int hoursPerWeek;
    private List<String> eligibleFacultyIds;

    public Course(String id, String courseCode, String name, String courseType, String branch, String section,
                  int lecture, int theory, int practical, String credits, int hoursPerWeek, List<String> eligibleFacultyIds) {
        this.id = id;
        this.courseCode = courseCode;
        this.name = name;
        this.courseType = courseType;
        this.branch = branch;
        this.section = section;
        this.lecture = lecture;
        this.theory = theory;
        this.practical = practical;
        this.credits = credits;
        this.hoursPerWeek = hoursPerWeek;
        this.eligibleFacultyIds = eligibleFacultyIds;
    }

    // Getters
    public String getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getName() { return name; }
    public String getCourseType() { return courseType; }
    public String getBranch() { return branch; }
    public String getSection() { return section; }
    public int getLecture() { return lecture; }
    public int getTheory() { return theory; }
    public int getPractical() { return practical; }
    public String getCredits() { return credits; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public List<String> getEligibleFacultyIds() { return eligibleFacultyIds; }
}

