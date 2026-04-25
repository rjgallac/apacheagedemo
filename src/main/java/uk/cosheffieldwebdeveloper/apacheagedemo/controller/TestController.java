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
    public String createNode(@RequestParam String name) {
        return personRepository.createPerson(name);
        
    }

    @GetMapping("/create-edge")
    public String createEdge(
            @RequestParam String name1,
            @RequestParam String name2) {
        return personRepository.createRelation(name1, name2);
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
