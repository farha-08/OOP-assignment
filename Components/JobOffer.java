package placementportal.company.ui;

public class JobOffer {
    private final String title;
    private final String department;
    private final String location;
    private final String type;       // Full-time / Internship
    private final String salary;
    private final int positions;
    private final String deadline;   // keep as String for now (dd/mm/yyyy)
    private final String description;
    private final String qualifications;
    private final String[] skills;

    // demo field for UI (you can replace later)
    private final int applicants;

    public JobOffer(String title, String department, String location, String type,
                    String salary, int positions, String deadline,
                    String description, String qualifications, String[] skills,
                    int applicants) {
        this.title = title;
        this.department = department;
        this.location = location;
        this.type = type;
        this.salary = salary;
        this.positions = positions;
        this.deadline = deadline;
        this.description = description;
        this.qualifications = qualifications;
        this.skills = skills;
        this.applicants = applicants;
    }

    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getSalary() { return salary; }
    public int getPositions() { return positions; }
    public String getDeadline() { return deadline; }
    public String getDescription() { return description; }
    public String getQualifications() { return qualifications; }
    public String[] getSkills() { return skills; }
    public int getApplicants() { return applicants; }
}