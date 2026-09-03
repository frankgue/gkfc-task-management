// task-bootstrap/src/main/java/com/gkfcsolution/taskmanager/TaskManagerApplication.java
package com.gkfcsolution.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.gkfcsolution.taskmanager.web",      // ✅ Pour les contrôleurs
        "com.gkfcsolution.taskmanager.service",  // ✅ Pour les services
        "com.gkfcsolution.taskmanager.domain",   // ✅ Pour le domaine
        "com.gkfcsolution.taskmanager.config" ,
        "com.gkfcsolution.taskmanager.initializer"// ✅ Pour les configurations
})
@EnableJpaRepositories(basePackages = "com.gkfcsolution.taskmanager.domain.repository")
@EntityScan(basePackages = "com.gkfcsolution.taskmanager.domain.entity")
public class TaskManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}