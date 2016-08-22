package org.vlis.operations.event.typeguess.bean;

public class RuleBean {
    private String project;
    private int action;
    private String id;

    public RuleBean(String project, int action, String id) {
        this.project = project;
        this.action = action;
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RuleBean [project=" + project + ", action=" + action + ", id=" + id + "]";
    }
}
