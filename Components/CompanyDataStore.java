package placementportal.company.ui;

import java.util.ArrayList;
import java.util.List;

public class CompanyDataStore {
    private final List<JobOffer> offers = new ArrayList<>();

    public void addOffer(JobOffer offer) {
        offers.add(0, offer); // newest first
    }

    public List<JobOffer> getOffers() {
        return offers;
    }

    // optional demo starter data
    public void seedDemo() {
        offers.clear();
        offers.add(new JobOffer(
                "Junior Software Developer", "Engineering", "Cape Town", "Full-time",
                "$25,000 - $35,000/month", 1, "15/03/2026",
                "Build and maintain software features.",
                "Year 2+ in CS/SE.",
                new String[]{"JavaScript", "React", "Node.js", "+1 more"},
                3
        ));
        offers.add(new JobOffer(
                "Data Analyst Intern", "Analytics", "Johannesburg", "Internship",
                "$15,000 - $20,000/month", 2, "01/04/2026",
                "Assist with dashboards and reports.",
                "Basic SQL and Excel.",
                new String[]{"Python", "SQL", "Tableau", "+1 more"},
                3
        ));
        offers.add(new JobOffer(
                "IT Support Specialist", "IT Operations", "Pretoria", "Part-time",
                "$20,000 - $28,000/month", 1, "28/02/2026",
                "Support staff and troubleshoot devices.",
                "Networking basics.",
                new String[]{"Networking", "Windows Server", "Troubleshooting"},
                1
        ));
    }
}