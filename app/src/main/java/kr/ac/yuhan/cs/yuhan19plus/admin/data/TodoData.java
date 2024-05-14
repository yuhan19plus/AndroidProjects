package kr.ac.yuhan.cs.yuhan19plus.admin.data;

import androidx.annotation.NonNull;

public class TodoData {
    // Todo Data Filed
    private int id;
    private String adminId;
    private String todoContent;
    private String selectedDate;
    private String creationDate;

    // Getter & Setter, toString
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    public String getAdminId() {
        return adminId;
    }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }
    public String getTodoContent() {
        return todoContent;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }
    public String getSelectedDate() {
        return selectedDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    public String getCreationDate() {
        return creationDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "TodoData{" +
                "id=" + id +
                ", adminId='" + adminId + '\'' +
                ", todoContent='" + todoContent + '\'' +
                ", selectedDate='" + selectedDate + '\'' +
                ", creationDate='" + creationDate + '\'' +
                '}';
    }
}
