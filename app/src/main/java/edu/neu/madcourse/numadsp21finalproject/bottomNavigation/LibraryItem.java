package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

public class LibraryItem implements LibraryViewClickListener{
    private final String projectName;

    public LibraryItem(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public void onItemClick(int position) {

    }
}
