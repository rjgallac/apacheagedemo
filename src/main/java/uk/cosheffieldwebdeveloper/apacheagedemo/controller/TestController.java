package uk.cosheffieldwebdeveloper.apacheagedemo.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.cosheffieldwebdeveloper.apacheagedemo.repository.PersonRepository;

@RestController
public class TestController {

    private final PersonRepository personRepository;

    public TestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
   
    @GetMapping("/create-node")
    public String createNode(@RequestParam String propertyName) {
        return personRepository.createPerson(propertyName);
        
    }

    @GetMapping("/create-edge")
    public String createEdge(
            @RequestParam String prop1,
            @RequestParam String prop2) {
        return personRepository.createRelation(prop1, prop2);
    }

    @GetMapping("/test")
    public String test() {
        return "AGE Database Controller is running!";
    }

    
    @DeleteMapping("/delete-all")
    public String deleteAll(){

        return personRepository.deleteAll();

    }

}
