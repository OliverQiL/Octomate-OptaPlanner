package org.acme.schooltimetabling.domain;

/**
 * Represents an employee.
 * An employee is defined by a unique ID and a name.
 * This class is immutable after creation, meaning that
 * once an Employee object is created, its ID and name cannot be changed.
 */
public class Employee {

    private Long id; // Unique identifier for the employee
    private String name;

    public Employee() {
        // Default constructor
    }

    public Employee(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // No setters as Employee is immutable after creation

    @Override
    public String toString() {
        return name;
    }
}
