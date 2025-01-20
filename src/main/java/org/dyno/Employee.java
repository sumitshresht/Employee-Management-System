package org.dyno;

public class Employee {
    private int id;
    private String emp_name;
    private String job_role;
    private double salary;
    private static boolean isheader = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getJob_role() {
        return job_role;
    }

    public void setJob_role(String job_role) {
        this.job_role = job_role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
    private void header() {
        System.out.println("id" +"\t" + "emp_name" + "\t" +"job_role" +"\t" + "salary");
        isheader = true;
    }

    @Override
    public String toString() {
        if(!isheader){
            header();
        }
        return id +"\t" + emp_name + "\t\t" +job_role +"\t\t" + salary;
    }
}
